# syntax=docker/dockerfile:1.7
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY src/Sales.OrderService/*.csproj ./Sales.OrderService/
RUN dotnet restore Sales.OrderService/Sales.OrderService.csproj
COPY src/Sales.OrderService ./Sales.OrderService
RUN dotnet publish Sales.OrderService/Sales.OrderService.csproj -c Release -o /out --no-restore

FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
RUN groupadd --gid 65532 nonroot && useradd --uid 65532 --gid 65532 --no-create-home nonroot
COPY --from=build /out .
USER 65532:65532
ENV ASPNETCORE_URLS=http://0.0.0.0:8080
EXPOSE 8080
ENTRYPOINT ["dotnet", "Sales.OrderService.dll"]
