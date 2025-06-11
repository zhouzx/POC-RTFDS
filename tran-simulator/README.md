# Transaction Simulator

## Overview
The Transaction Simulator is a utility service that generates realistic transaction data to test the fraud detection system. It can simulate various transaction patterns, including normal transactions and potentially fraudulent ones, enabling thorough testing and demonstration of the fraud detection capabilities.

## Features
- Generation of random but realistic transaction data
- Configurable transaction rate and volume
- Simulation of different transaction patterns (normal, suspicious, fraudulent)
- Support for both continuous streaming and batch generation modes
- RESTful API for controlling simulation parameters
- Integration with RabbitMQ for transaction delivery
- Detailed logging of generated transactions
- Customizable transaction templates

## Technical Stack
- Java 17+
- Spring Boot
- Spring AMQP for RabbitMQ integration
- RESTful APIs
- Faker library for realistic data generation

## Configuration
The application is configured through the `application.yml` file located in the `src/main/resources` directory. Key configuration parameters include:

```yaml
spring:
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: guest
    password: guest

simulator:
  transaction:
    queue-name: transaction_queue
    rate: 10  # transactions per second
    fraud-ratio: 0.05  # 5% of transactions will be fraudulent
    batch-size: 100  # when using batch mode
```

### Environment Variables
The following environment variables can be used to override the default configuration:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_RABBITMQ_HOST` | RabbitMQ host | rabbitmq |
| `SPRING_RABBITMQ_PORT` | RabbitMQ port | 5672 |
| `SPRING_RABBITMQ_USERNAME` | RabbitMQ username | guest |
| `SPRING_RABBITMQ_PASSWORD` | RabbitMQ password | guest |
| `SIMULATOR_TRANSACTION_QUEUE_NAME` | Queue name for transactions | transaction_queue |
| `SIMULATOR_TRANSACTION_RATE` | Transactions per second | 10 |
| `SIMULATOR_FRAUD_RATIO` | Ratio of fraudulent transactions | 0.05 |
| `SIMULATOR_BATCH_SIZE` | Number of transactions in batch mode | 100 |

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building
To build the application:

```bash
mvn clean package
```

### Running Locally
To run the application locally:

```bash
java -jar target/tran-simulator-0.0.1-SNAPSHOT.jar
```

### Running with Docker
To run the application using Docker:

```bash
docker build -t tran-simulator .
docker run -p 8083:8083 tran-simulator
```

### Running with docker-compose
The service can be run as part of the full system using docker-compose:

```bash
cd /path/to/project/root
docker-compose up tran-simulator
```

## API Documentation

### Simulation Control API
- `POST /api/simulator/start` - Start the transaction simulation
- `POST /api/simulator/stop` - Stop the current simulation
- `POST /api/simulator/batch` - Generate a batch of transactions
- `PUT /api/simulator/config` - Update simulation configuration

Example request to update configuration:
```json
{
  "transactionRate": 20,
  "fraudRatio": 0.1,
  "batchSize": 200,
  "enableHighValueTransactions": true,
  "enableSuspiciousAccounts": true
}
```

### Single Transaction API
- `POST /api/simulator/transaction` - Generate a single transaction
- `POST /api/simulator/fraudulent-transaction` - Generate a fraudulent transaction

### Health and Monitoring API
- `GET /api/simulator/health` - Check the health status of the simulator
- `GET /api/simulator/stats` - Get statistics about generated transactions

## Generated Transaction Examples

### Normal Transaction
```json
{
  "transactionId": "TX98765432",
  "accountId": "ACC876543",
  "amount": 120.50,
  "currency": "USD",
  "timestamp": "2023-06-15T14:22:31Z",
  "merchantId": "MERCH456",
  "merchantCategory": "GROCERY",
  "location": {
    "country": "US",
    "city": "Chicago"
  },
  "deviceId": "DEV456"
}
```

### Potentially Fraudulent Transaction
```json
{
  "transactionId": "TX12345678",
  "accountId": "FRAUD-ACC123",
  "amount": 9999.99,
  "currency": "USD",
  "timestamp": "2023-06-15T03:45:12Z",
  "merchantId": "MERCH789",
  "merchantCategory": "ELECTRONICS",
  "location": {
    "country": "RU",
    "city": "Moscow"
  },
  "deviceId": "DEV789"
}
```

## Simulation Patterns

The simulator can generate different transaction patterns, including:

1. **Normal Transactions**: Transactions with typical amounts, frequencies, and locations
2. **High Amount Transactions**: Transactions with unusually large amounts
3. **Suspicious Account Transactions**: Transactions from accounts with suspicious identifiers
4. **Unusual Time Transactions**: Transactions occurring at unusual times
5. **Unusual Location Transactions**: Transactions from unexpected geographic locations
6. **Rapid Succession Transactions**: Multiple transactions in a short time period

## Troubleshooting
- **Connection to RabbitMQ fails**: Verify that RabbitMQ is running and the connection parameters are correct
- **Service not starting**: Check for port conflicts or missing dependencies
- **No transactions being generated**: Verify that the simulation has been started via the API
- **Low performance**: Adjust the transaction rate to match your system's capabilities

## License
This project is proprietary and confidential. 