namespace Sales.OrderService.Domain;

public enum OrderStatus
{
    Draft,
    Reserved,
    PaymentPending,
    Confirmed,
    Cancelled,
    Compensated
}

public sealed record OrderLine(string Sku, int Quantity, decimal UnitPrice);

public sealed class SalesOrder
{
    public Guid Id { get; init; } = Guid.NewGuid();
    public required Guid CustomerId { get; init; }
    public required string Currency { get; init; }
    public required List<OrderLine> Lines { get; init; }
    public string IdempotencyKey { get; init; } = Guid.NewGuid().ToString();
    public OrderStatus Status { get; set; } = OrderStatus.Draft;
    public Guid? ReservationId { get; set; }
    public Guid? PaymentId { get; set; }
    public decimal Total => Lines.Sum(line => line.UnitPrice * line.Quantity);
}

public sealed class OrderStore
{
    private readonly Dictionary<Guid, SalesOrder> _orders = new();
    public List<object> Events { get; } = [];

    public SalesOrder Add(SalesOrder order)
    {
        _orders[order.Id] = order;
        Events.Add(new { type = "com.sales.order.created.v1", orderId = order.Id, customerId = order.CustomerId, status = order.Status.ToString(), totalAmount = order.Total, currency = order.Currency });
        return order;
    }

    public SalesOrder Get(Guid id) => _orders.TryGetValue(id, out var order) ? order : throw new KeyNotFoundException(id.ToString());

    public IReadOnlyCollection<SalesOrder> List() => _orders.Values;
}
