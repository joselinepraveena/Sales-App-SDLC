using Sales.OrderService.Api;
using Sales.OrderService.Domain;
using Sales.OrderService.Saga;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddSingleton<OrderStore>();
builder.Services.AddSingleton<OrderSaga>();
builder.Services.AddEndpointsApiExplorer();

var app = builder.Build();
app.MapHealth();
app.MapOrders();
app.Run();

public partial class Program;
