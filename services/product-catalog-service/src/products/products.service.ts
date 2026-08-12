import { Injectable, NotFoundException } from "@nestjs/common";
import { randomUUID } from "crypto";
import { OutboxEvent, Product } from "./product";

@Injectable()
export class ProductsService {
  private readonly products = new Map<string, Product>();
  readonly events: OutboxEvent[] = [];

  create(input: { sku: string; name: string; category: string; attributes?: Record<string, string> }): Product {
    const product: Product = {
      id: randomUUID(),
      sku: input.sku,
      name: input.name,
      category: input.category,
      attributes: input.attributes ?? {},
      status: "draft",
      version: 1,
    };
    this.products.set(product.id, product);
    return product;
  }

  publish(id: string): Product {
    const product = this.require(id);
    product.status = "published";
    product.version += 1;
    this.events.push({
      type: "com.sales.product.published.v1",
      productId: product.id,
      sku: product.sku,
      version: product.version,
    });
    return product;
  }

  update(id: string, name: string, attributes: Record<string, string>): Product {
    const product = this.require(id);
    product.name = name;
    product.attributes = attributes;
    product.version += 1;
    this.events.push({
      type: "com.sales.product.changed.v1",
      productId: product.id,
      sku: product.sku,
      version: product.version,
    });
    return product;
  }

  get(id: string): Product {
    return this.require(id);
  }

  list(): Product[] {
    return [...this.products.values()];
  }

  private require(id: string): Product {
    const product = this.products.get(id);
    if (!product) {
      throw new NotFoundException(`Product ${id} was not found`);
    }
    return product;
  }
}
