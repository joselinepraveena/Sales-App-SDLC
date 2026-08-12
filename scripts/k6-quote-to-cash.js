import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 10,
  duration: "30s",
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500"],
  },
};

const BASE = __ENV.API_BASE || "https://api.sales.example";

export default function () {
  const customer = http.post(`${BASE}/customer-service/api/v1/customers`, JSON.stringify({
    legalName: "Load Test Buyer",
    email: `buyer-${__VU}-${__ITER}@example.com`,
    marketingConsent: false,
  }), { headers: { "Content-Type": "application/json" } });
  check(customer, { "customer created": (r) => r.status === 201 });
  sleep(1);
}
