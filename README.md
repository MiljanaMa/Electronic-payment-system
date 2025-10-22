# Electronic Payment System 

## Overview 
**Telecommunication Operator** is a full-stack web shop system that allows users to **browse, subscribe, and purchase telecommunication services** such as mobile and fixed telephony, internet, and digital TV. 

All payments are processed securely via an external **Payment Service Provider (PSP)** system, which acts as an independent intermediary between merchants and various payment services (Bank, PayPal, Bitcoin, etc.).


## Functionalities

### Web Shop (Telecom Operator)
- User registration and authentication (JWT)
- Service browsing with detailed descriptions
- Subscription management (1+ years, renewal, cancellation)
- Purchase and automatic redirect to PSP for payment

### Payment Service Provider (PSP)
- Multi-tenant support (multiple merchants)
- Modular (plug-in) payment methods:
  - Bank (Card, QR code)
  - PayPal (with subscription support)
  - Crypto (via testnet)
- Merchant management (super admin)
- High-availability, scalable architecture
- Logging and monitoring for all payment requests


## Technologies 

| Layer | Technologies |
|-------|---------------|
| **Webshop / PSP** Backend | Java 17 / Spring Boot, Spring Security, JPA (Hibernate) |
| **Webshop / PSP / Bank** Frontend | React.js |
| **Crypto** Frontend | Angular |
| **Webshop / PSP** Database | PostgreSQL |
| **Service discovery** | Eureka |
| **PSP / PSP-PMS Gateway** | Spring Cloud Gateway |
| **Bank / PayPal / Bitcoin** Backend | Python / FastAPI |
| **Bank / PayPal / Bitcoin** Database | SQLite |
| **Communication** | REST API (HTTPS) |
| **Security** | HTTPS, JWT Authentication, OAuth2 |
| **Monitoring & Observability** | Prometheus, Promtail, Loki, Grafana, Jaeger |
| **Deployment** | Docker, Docker Compose |

## Security

This system is designed in compliance with key **PCI DSS** requirements to ensure maximum payment security:

- **Protection of account and cardholder data** through strong encryption and secure storage.  
- **Comprehensive monitoring and logging** of all access to cardholder and sensitive data.  
- **Encrypted communication** using HTTPS across all services and internal APIs.  
- **Regular penetration testing** performed on all payment modules.  
  - Penetration tests were conducted for the **Bitcoin Frontend** and **Bank Frontend**.  
  - Reports generated with **OWASP ZAP** are available in the `/penetration-testing` directory.

## Setup & Installation

You can run the system in **two different ways** — using **Docker Compose for the full stack**, or manually running services while using Docker only for **monitoring tools**.

### Option 1: Run Entire Stack via Docker Compose

**1. Clone repository**

```bash
git clone https://github.com/MiljanaMa/Electronic-payment-system.git
```

**2. Navigate to the root folder containing the docker-compose.yml file and run**

```bash
docker-compose up -d
```
This will start: Webshop, PSP, PostgreSQL db, PgAdmin, Service discovery (Eureka), PSP Gateway and monitoring stack (Prometheus, Loki, Grafana, Promtail, Jaeger)

⚠️ Note:
The **Bank, Paypal, Crypto services** and their **Databases** are not included in ```bash docker-compose.yml ``` and must be started manually.

⚠️ Note:
Initial data must be added to databases manually.

### Option 2: Manual Startup + Monitoring via Docker

**1. Clone repository**

```bash
git clone https://github.com/MiljanaMa/Electronic-payment-system.git
```

**2. Navigate to the monitoring folder containing the docker-compose.yml file and run**

```bash
cd monitoring
docker-compose up -d
```
**3. Start PSP, Webshop, Eureka service discovery and PSP gateway  (manually)**
```bash
cd ...
mvn clean install
mvn spring-boot:run
```
⚠️ Note:
Initial data must be added to databases manually.

**4. Start PSP, Webshop, Bank frontend  (manually)**

```bash
cd ..
npm install
npm start
```

**5. Start Crypto frontend  (manually)**

```bash
cd ..
ng build
ng serve
```
**5. Start PayPal, Bank and Crypto backend  (manually)**

```bash
cd ..
python main.py
```

## Services & URLs

| Application | URL |
|-------|---------------|
| **Webshop Backend** | https://localhost:8081 |
| **Webshop Frontend** | https://localhost:3005 |
| **PSP Backend** | https://localhost:8082 |
| **PSP Frontend** | https://localhost:3001 |
| **PSP Gateway** | https://localhost:8089 |
| **Eureka Service discovery** | http://localhost:8761 |
| **PayPal** | http://localhost:8087 |
| **Crypto** | http://localhost:8070 |
| **Crypto Frontend** | http://localhost:4200 |
| **Bank Frontend** | http://localhost:3002 |
| **Grafana Dashboard** | http://localhost:3000 |
| **Prometheus** | http://localhost:9090 |
| **Jaeger UI**| http://localhost:16686 |
| **Banks** | TO BE ADDED |

## Future Improvements

- **RabbitMQ Integration:** Plan to use RabbitMQ as a message broker so that whenever a new payment method becomes available, PSP can automatically subscribe and register it for merchants.  
- **PSP Gateway Database:** The PSP Gateway will include a dedicated database to support dynamic routing from multiple clients (webshops) to the appropriate PSP instance.  
- **Enhanced Service Discovery:** Eureka Service Discovery will enable **high scalability** of the PSP system by automatically managing available instances and load balancing across them.
