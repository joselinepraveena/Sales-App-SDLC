import { Body, Controller, Get, Param, Post, Put } from "@nestjs/common";
import { ProductsService } from "./products.service";

@Controller("api/v1/products")
export class ProductsController {
  constructor(private readonly products: ProductsService) {}

  @Post()
  create(@Body() body: { sku: string; name: string; category: string; attributes?: Record<string, string> }) {
    return this.products.create(body);
  }

  @Get()
  list() {
    return this.products.list();
  }

  @Get(":id")
  get(@Param("id") id: string) {
    return this.products.get(id);
  }

  @Post(":id/publish")
  publish(@Param("id") id: string) {
    return this.products.publish(id);
  }

  @Put(":id")
  update(
    @Param("id") id: string,
    @Body() body: { name: string; attributes: Record<string, string> },
  ) {
    return this.products.update(id, body.name, body.attributes ?? {});
  }
}
