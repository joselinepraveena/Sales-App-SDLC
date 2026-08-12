import { ProductsService } from "./products.service";

describe("ProductsService", () => {
  it("publishes a product and records a ProductPublished event", () => {
    const service = new ProductsService();
    const created = service.create({
      sku: "SKU-100",
      name: "Ergonomic Chair",
      category: "furniture",
    });
    const published = service.publish(created.id);
    expect(published.status).toBe("published");
    expect(service.events[0].type).toBe("com.sales.product.published.v1");
    expect(service.events[0].sku).toBe("SKU-100");
  });
});
