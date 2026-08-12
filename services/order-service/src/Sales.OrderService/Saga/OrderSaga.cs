namespace Sales.OrderService.Saga;

using Sales.OrderService.Domain;

public sealed class OrderSaga(OrderStore store)
{
    public SalesOrder Confirm(Guid orderId, Guid reservationId, Guid paymentId)
    {
        var order = store.Get(orderId);
        order.ReservationId = reservationId;
        order.PaymentId = paymentId;
        order.Status = OrderStatus.Confirmed;
        store.Events.Add(new { type = "com.sales.order.confirmed.v1", orderId = order.Id, customerId = order.CustomerId, status = order.Status.ToString() });
        return order;
    }

    public SalesOrder Compensate(Guid orderId, string reason)
    {
        var order = store.Get(orderId);
        order.Status = OrderStatus.Compensated;
        store.Events.Add(new { type = "com.sales.order.cancelled.v1", orderId = order.Id, customerId = order.CustomerId, status = order.Status.ToString(), reason });
        return order;
    }
}
