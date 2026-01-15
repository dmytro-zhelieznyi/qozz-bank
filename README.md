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

## 📊 Monitoring & Management (Spring Boot Actuator / Prometheus / Grafana)
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

This project includes a pre-configured monitoring stack to observe application performance and database behavior under heavy load.

### Component Overview
* **Prometheus**: A time-series database that pulls (scrapes) metrics from the Spring Boot `/actuator/prometheus` endpoint every 5 seconds.
* **Grafana**: A visualization platform that connects to Prometheus to display real-time dashboards (JVM health, HikariCP pool status, HTTP throughput).

### 🚀 Getting Started

1.  **Start the infrastructure**:
    Run the following command in the root directory:
    ```bash
    docker-compose up -d
    ```

2.  **Access Grafana**:
    * **URL**: `http://localhost:3000`
    * **Login**: `admin`
    * **Password**: `admin` (you may be asked to change it on first login).

3.  **Connect Prometheus to Grafana**:
    * Go to **Connections** -> **Data Sources** in the left sidebar.
    * Click **Add data source** and select **Prometheus**.
    * Set the URL to: `http://prometheus:9090` (this works because they share the same Docker network).
    * Scroll down and click **Save & Test**.

4.  **Import Dashboard**:
    * Go to **Dashboards** -> **New** -> **Import**.
    * Enter ID `4701` (JVM Micrometer) or `11378`.
    * Select your **Prometheus** data source and click **Import**.

### 🛠 Tech Stack Details
| Service | Internal Port | External Port | Description              |
| :--- | :--- | :--- |:-------------------------|
| **App** | 8080 | 8080 | Spring Boot Bank Service |
| **PostgreSQL** | 5432 | 5432 | Database                 |
| **Prometheus** | 9090 | 9090 | Metrics Aggregator       |
| **Grafana** | 3000 | 3000 | Dashboard UI             |

---

## 📖 API Documentation (OpenAPI / Swagger)

Swagger UI is integrated into the project for convenient testing and integration.

* **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) — A visual interface for sending API requests.
* **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) — API specification in JSON format for client generation.

---
