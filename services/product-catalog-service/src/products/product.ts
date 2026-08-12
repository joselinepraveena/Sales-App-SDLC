export type ProductStatus = "draft" | "published" | "retired";

export interface Product {
  id: string;
  sku: string;
  name: string;
  category: string;
  attributes: Record<string, string>;
  status: ProductStatus;
  version: number;
}

export interface OutboxEvent {
  type: string;
  productId: string;
  sku: string;
  version: number;
}
