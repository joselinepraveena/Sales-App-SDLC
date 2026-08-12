const services = [
  { name: "Customer", lang: "Java / Spring Boot", path: "/api/v1/customers" },
  { name: "Product Catalogue", lang: "TypeScript / NestJS", path: "/api/v1/products" },
  { name: "Pricing", lang: "Python / FastAPI", path: "/api/v1/quotes" },
  { name: "Inventory", lang: "Go / Gin", path: "/api/v1/reservations" },
  { name: "Orders", lang: "C# / ASP.NET Core", path: "/api/v1/orders" },
  { name: "Payments", lang: "Kotlin / Ktor", path: "/api/v1/payments/authorize" },
  { name: "Notifications", lang: "Rust / Axum", path: "/api/v1/notifications" },
  { name: "Analytics", lang: "Scala / Play", path: "/api/v1/kpis" },
];

export default function HomePage() {
  return (
    <main style={{ maxWidth: 960, margin: "0 auto", padding: "48px 24px" }}>
      <p style={{ letterSpacing: 2, textTransform: "uppercase", color: "#7ad7ff" }}>Sales platform</p>
      <h1>Quote-to-cash walking skeleton</h1>
      <p>
        React / Next.js experience hosted on Azure Static Web Apps or App Service, fronted by Azure Front Door
        Premium and Azure API Management. Domain APIs stay independently deployable on AKS.
      </p>
      <section style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 16, marginTop: 32 }}>
        {services.map((service) => (
          <article key={service.name} style={{ background: "#132c46", padding: 16, borderRadius: 12 }}>
            <h2 style={{ marginTop: 0 }}>{service.name}</h2>
            <p style={{ color: "#9bb4c9" }}>{service.lang}</p>
            <code>{service.path}</code>
          </article>
        ))}
      </section>
    </main>
  );
}
