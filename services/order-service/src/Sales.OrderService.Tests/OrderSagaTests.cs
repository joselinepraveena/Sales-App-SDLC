using Sales.OrderService.Domain;
using Sales.OrderService.Saga;
using Xunit;

namespace Sales.OrderService.Tests;

public class OrderSagaTests
{
    [Fact]
    public void ConfirmPublishesOrderConfirmed()
    {
        var store = new OrderStore();
        var saga = new OrderSaga(store);
        var order = store.Add(new SalesOrder
        {
            CustomerId = Guid.NewGuid(),
            Currency = "USD",
            Lines = [new OrderLine("SKU-100", 1, 249m)]
        });

        var confirmed = saga.Confirm(order.Id, Guid.NewGuid(), Guid.NewGuid());
        Assert.Equal(OrderStatus.Confirmed, confirmed.Status);
        Assert.Contains(store.Events, e => e.ToString()!.Contains("com.sales.order.confirmed.v1"));
    }

    [Fact]
    public void CompensateReleasesSaga()
    {
        var store = new OrderStore();
        var saga = new OrderSaga(store);
        var order = store.Add(new SalesOrder
        {
            CustomerId = Guid.NewGuid(),
            Currency = "USD",
            Lines = [new OrderLine("SKU-100", 2, 10m)]
        });
        saga.Compensate(order.Id, "payment-failed");
        Assert.Equal(OrderStatus.Compensated, store.Get(order.Id).Status);
    }
}
