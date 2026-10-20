# Sary Gurumi (backend)

E-commerce en microservicios Spring Boot: Eureka, API Gateway, autenticación JWT, catálogo y pedidos.

## Requisitos

- Docker y Docker Compose
- Java 21 y Maven (solo si corres los servicios fuera de Docker)

## Levantar todo

```bash
docker compose up --build
```

Espera a que Eureka (`http://localhost:8761`) muestre `product-service`, `order-service`, `auth-service` y `api-gateway`. El punto de entrada es `http://localhost:8080`.

Bases de datos PostgreSQL:

| Servicio | Puerto local |
|---|---|
| products | 5433 |
| orders | 5434 |
| auth | 5435 |

## Flujo de prueba (vía gateway)

Registro:

```bash
curl -s -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"cliente@sary.com\",\"password\":\"secret123\"}"
```

Login (devuelve un JWT; el gateway aún no lo exige):

```bash
curl -s -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"cliente@sary.com\",\"password\":\"secret123\"}"
```

Crear productos:

```bash
curl -s -X POST http://localhost:8080/api/products ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Peluche Gurumi\",\"description\":\"Hecho a mano\",\"price\":250.00}"

curl -s http://localhost:8080/api/products
```

Crear y consultar una orden (usa el `id` del producto creado):

```bash
curl -s -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"items\":[{\"productId\":1,\"quantity\":2}]}"

curl -s http://localhost:8080/api/orders/1
```

En Linux/macOS sustituye `^` por `\` y no escapes las comillas del JSON.

## Puertos locales (sin Docker de apps)

Si solo levantas las bases con Compose y los jars en la máquina:

| Servicio | Puerto |
|---|---|
| discovery-server | 8761 |
| api-gateway | 8080 |
| product-service | 8081 |
| order-service | 8082 |
| auth-service | 8083 |
