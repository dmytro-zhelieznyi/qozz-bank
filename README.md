# QozzBank Core Service 🏦
**QozzBank** is a high-performance banking backend prototype built with **Java 25** and **Spring Boot 4.x**.
---

## 🛠 Tech Stack
* **Runtime**: Java 25 (Virtual Threads enabled)
* **Framework**: Spring Boot 4.0
* **Database**: PostgreSQL
* **Migration**: Flyway
* **Monitoring**: Micrometer + Prometheus
* **Documentation**: SpringDoc OpenAPI (Swagger)
---

## 📊 Monitoring & Management (Spring Boot Actuator)
The project is equipped with powerful monitoring tools available via the `/actuator` base path.

### Core Endpoints:
* **Health Check**: `http://localhost:8080/actuator/health` — Status of the application, database, and Flyway migrations.
* **Info**: `http://localhost:8080/actuator/info` — Information about version, build, and Git commit.
* **Metrics**: `http://localhost:8080/actuator/metrics` — Detailed performance metrics (memory, CPU, threads).
* **Prometheus**: `http://localhost:8080/actuator/prometheus` — Metrics formatted for scraping by a Prometheus server.
* **Loggers**: `http://localhost:8080/actuator/loggers` — Real-time management of logging levels.

> **Tip:** To enable **DEBUG** logging for services without a restart, execute the following command:
> ```bash
> curl -X POST http://localhost:8080/actuator/loggers/com.qozzbank \
>      -H "Content-Type: application/json" \
>      -d '{"configuredLevel":"DEBUG"}'
> ```
---

## 📖 API Documentation (OpenAPI / Swagger)

Swagger UI is integrated into the project for convenient testing and integration.

* **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) — A visual interface for sending API requests.
* **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) — API specification in JSON format for client generation.

---
