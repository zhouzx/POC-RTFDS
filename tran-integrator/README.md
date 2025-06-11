# Transaction Integrator

## Overview
The Transaction Integrator is a critical component in the fraud detection system that serves as a gateway for receiving transaction data from various sources. It validates, normalizes, and enriches incoming transaction data before forwarding it to the Fraud Detection Engine for analysis.

## Features
- RESTful API for receiving transaction data
- Support for multiple data formats (JSON, XML)
- Transaction validation and data normalization
- Transaction enrichment with additional metadata
- Integration with RabbitMQ for reliable message delivery
- Monitoring and health check endpoints
- Transaction tracking and logging
- Load balancing support

## Technical Stack
- Java 17+
- Spring Boot
- Spring AMQP for RabbitMQ integration
- RESTful APIs
- Jackson for JSON processing
- Spring Validation

## Configuration
The application is configured through the `application.yml` file located in the `src/main/resources` directory. Key configuration parameters include:

```yaml
spring:
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: guest
    password: guest

transaction:
  queue:
    name: transaction_queue
  validation:
    enable: true
  enrichment:
    enable: true
```

### Environment Variables
The following environment variables can be used to override the default configuration:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_RABBITMQ_HOST` | RabbitMQ host | rabbitmq |
| `SPRING_RABBITMQ_PORT` | RabbitMQ port | 5672 |
| `SPRING_RABBITMQ_USERNAME` | RabbitMQ username | guest |
| `SPRING_RABBITMQ_PASSWORD` | RabbitMQ password | guest |
| `TRANSACTION_QUEUE_NAME` | Queue name for transactions | transaction_queue |
| `VALIDATION_ENABLED` | Enable/disable transaction validation | true |
| `ENRICHMENT_ENABLED` | Enable/disable transaction enrichment | true |

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Building
To build the application:

```bash
mvn clean package
```

### Running Locally
To run the application locally:

```bash
java -jar target/tran-integrator-0.0.1-SNAPSHOT.jar
```

### Running with Docker
To run the application using Docker:

```bash
docker build -t tran-integrator .
docker run -p 8082:8082 tran-integrator
```

### Running with docker-compose
The service can be run as part of the full system using docker-compose:

```bash
cd /path/to/project/root
docker-compose up tran-integrator
```

## API Documentation

### Transaction API
- `POST /api/transactions` - Submit a new transaction for fraud detection

Example request body:
```json
{
  "transactionId": "TX12345",
  "accountId": "ACC123456",
  "amount": 1000.00,
  "currency": "USD",
  "timestamp": "2023-06-15T10:30:00Z",
  "merchantId": "MERCH001",
  "merchantCategory": "RETAIL",
  "location": {
    "country": "US",
    "city": "New York"
  },
  "deviceId": "DEV123"
}
```

Response:
```json
{
  "transactionId": "TX12345",
  "status": "PROCESSING",
  "receivedAt": "2023-06-15T10:30:05Z",
  "message": "Transaction submitted for fraud detection"
}
```

### Batch Transaction API
- `POST /api/transactions/batch` - Submit multiple transactions at once

### Health and Monitoring API
- `GET /api/transactions/health` - Check the health status of the transaction integrator
- `GET /api/transactions/stats` - Get statistics about processed transactions

## Transaction Processing Flow

1. **Receive Transaction**: The service receives transaction data via the REST API
2. **Validate Transaction**: Basic validation of required fields and data formats
3. **Normalize Data**: Convert data to a standard format (amounts, timestamps, etc.)
4. **Enrich Transaction**: Add additional metadata (IP geolocation, merchant details, etc.)
5. **Queue Transaction**: Submit the processed transaction to RabbitMQ
6. **Return Response**: Return a confirmation with tracking details to the client

## Message Publishing

The service publishes messages to the transaction queue in the following format:

```json
{
  "transactionId": "TX12345",
  "accountId": "ACC123456",
  "amount": 1000.00,
  "currency": "USD",
  "timestamp": "2023-06-15T10:30:00Z",
  "merchantId": "MERCH001",
  "merchantCategory": "RETAIL",
  "location": {
    "country": "US",
    "city": "New York"
  },
  "deviceId": "DEV123",
  "enrichedData": {
    "ipAddress": "192.168.1.1",
    "userAgent": "Mozilla/5.0",
    "riskLevel": "LOW"
  }
}
```

## Troubleshooting
- **Connection to RabbitMQ fails**: Verify that RabbitMQ is running and the connection parameters are correct
- **Validation errors**: Check the transaction data against the required schema
- **Service not starting**: Check for port conflicts or missing dependencies
- **Performance issues**: Monitor queue depth and adjust thread pool settings if needed

## License
This project is proprietary and confidential. 