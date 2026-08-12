import { Module } from "@nestjs/common";
import { HealthController } from "./health/health.controller";
import { ProductsController } from "./products/products.controller";
import { ProductsService } from "./products/products.service";

@Module({
  controllers: [HealthController, ProductsController],
  providers: [ProductsService],
})
export class AppModule {}
