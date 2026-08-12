namespace Sales.OrderService.Api;

using Microsoft.AspNetCore.Mvc;
using Sales.OrderService.Domain;
using Sales.OrderService.Saga;

public sealed record CreateOrderRequest(Guid CustomerId, string Currency, List<OrderLine> Lines, string? IdempotencyKey);
public sealed record ConfirmOrderRequest(Guid ReservationId, Guid PaymentId);

public static class OrderEndpoints
{
    public static void MapOrders(this WebApplication app)
    {
        app.MapPost("/api/v1/orders", (CreateOrderRequest request, OrderStore store) =>
        {
            var order = store.Add(new SalesOrder
            {
                CustomerId = request.CustomerId,
                Currency = request.Currency,
                Lines = request.Lines,
                IdempotencyKey = request.IdempotencyKey ?? Guid.NewGuid().ToString()
            });
            return Results.Created($"/api/v1/orders/{order.Id}", order);
        });

        app.MapGet("/api/v1/orders/{id:guid}", (Guid id, OrderStore store) =>
        {
            try
            {
                return Results.Ok(store.Get(id));
            }
            catch (KeyNotFoundException)
            {
                return Results.NotFound();
            }
        });

        app.MapPost("/api/v1/orders/{id:guid}/confirm", (Guid id, ConfirmOrderRequest request, OrderSaga saga) =>
            Results.Ok(saga.Confirm(id, request.ReservationId, request.PaymentId)));

        app.MapPost("/api/v1/orders/{id:guid}/cancel", (Guid id, OrderSaga saga) =>
            Results.Ok(saga.Compensate(id, "caller-cancelled")));
    }

    public static void MapHealth(this WebApplication app)
    {
        app.MapGet("/health/live", () => Results.Ok(new { status = "UP" }));
        app.MapGet("/health/ready", () => Results.Ok(new { status = "UP" }));
        app.MapGet("/health/startup", () => Results.Ok(new { status = "UP" }));
    }
}
