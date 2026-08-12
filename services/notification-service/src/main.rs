use axum::{
    extract::{Path, State},
    http::StatusCode,
    routing::{get, post},
    Json, Router,
};
use serde::{Deserialize, Serialize};
use std::{
    collections::HashMap,
    net::SocketAddr,
    sync::{Arc, Mutex},
};
use uuid::Uuid;

#[derive(Clone, Serialize, Deserialize)]
#[serde(rename_all = "lowercase")]
pub enum Channel {
    Email,
    Sms,
    Push,
    Webhook,
}

#[derive(Clone, Serialize)]
pub struct Notification {
    pub notification_id: Uuid,
    pub channel: Channel,
    pub template_id: String,
    pub recipient: String,
    pub status: String,
}

#[derive(Clone, Serialize)]
pub struct Event {
    #[serde(rename = "type")]
    pub event_type: String,
    pub notification_id: Uuid,
    pub channel: Channel,
    pub template_id: String,
    pub recipient: String,
}

#[derive(Default)]
pub struct Store {
    pub notifications: HashMap<Uuid, Notification>,
    pub events: Vec<Event>,
}

pub type SharedStore = Arc<Mutex<Store>>;

#[derive(Deserialize)]
pub struct DispatchRequest {
    pub channel: Channel,
    pub template_id: String,
    pub recipient: String,
}

async fn health() -> Json<serde_json::Value> {
    Json(serde_json::json!({ "status": "UP" }))
}

pub async fn dispatch(
    State(store): State<SharedStore>,
    Json(req): Json<DispatchRequest>,
) -> (StatusCode, Json<Notification>) {
    let notification = Notification {
        notification_id: Uuid::new_v4(),
        channel: req.channel.clone(),
        template_id: req.template_id.clone(),
        recipient: req.recipient.clone(),
        status: "delivered".to_string(),
    };
    let mut locked = store.lock().expect("store");
    locked.events.push(Event {
        event_type: "com.sales.notification.requested.v1".into(),
        notification_id: notification.notification_id,
        channel: req.channel.clone(),
        template_id: req.template_id.clone(),
        recipient: req.recipient.clone(),
    });
    locked.events.push(Event {
        event_type: "com.sales.notification.delivered.v1".into(),
        notification_id: notification.notification_id,
        channel: req.channel,
        template_id: req.template_id,
        recipient: req.recipient,
    });
    locked
        .notifications
        .insert(notification.notification_id, notification.clone());
    (StatusCode::ACCEPTED, Json(notification))
}

async fn get_notification(
    State(store): State<SharedStore>,
    Path(id): Path<Uuid>,
) -> Result<Json<Notification>, StatusCode> {
    store
        .lock()
        .expect("store")
        .notifications
        .get(&id)
        .cloned()
        .map(Json)
        .ok_or(StatusCode::NOT_FOUND)
}

pub fn app(store: SharedStore) -> Router {
    Router::new()
        .route("/health/live", get(health))
        .route("/health/ready", get(health))
        .route("/health/startup", get(health))
        .route("/api/v1/notifications", post(dispatch))
        .route("/api/v1/notifications/{id}", get(get_notification))
        .with_state(store)
}

#[tokio::main]
async fn main() {
    let store: SharedStore = Arc::new(Mutex::new(Store::default()));
    let port: u16 = std::env::var("PORT")
        .ok()
        .and_then(|v| v.parse().ok())
        .unwrap_or(8080);
    let addr = SocketAddr::from(([0, 0, 0, 0], port));
    let listener = tokio::net::TcpListener::bind(addr).await.expect("bind");
    axum::serve(listener, app(store)).await.expect("server");
}

#[cfg(test)]
mod tests {
    use super::*;
    use axum::{
        body::Body,
        http::{Request, StatusCode},
    };
    use http_body_util::BodyExt;
    use tower::ServiceExt;

    #[tokio::test]
    async fn dispatch_records_requested_and_delivered_events() {
        let store: SharedStore = Arc::new(Mutex::new(Store::default()));
        let response = app(store.clone())
            .oneshot(
                Request::builder()
                    .method("POST")
                    .uri("/api/v1/notifications")
                    .header("content-type", "application/json")
                    .body(Body::from(
                        r#"{"channel":"email","template_id":"order-confirmed","recipient":"buyer@northwind.example"}"#,
                    ))
                    .unwrap(),
            )
            .await
            .unwrap();
        assert_eq!(response.status(), StatusCode::ACCEPTED);
        let events = store.lock().unwrap().events.clone();
        assert_eq!(events.len(), 2);
        assert_eq!(events[0].event_type, "com.sales.notification.requested.v1");
        assert_eq!(events[1].event_type, "com.sales.notification.delivered.v1");
        let _ = response.into_body().collect().await.unwrap();
    }
}
