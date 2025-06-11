# Notification Service

## Overview
The Notification Service is responsible for receiving fraud detection results from the Fraud Detection Engine and handling appropriate notifications. It logs suspicious transactions to a dedicated fraud alerts log file and provides an API endpoint for direct notification submission.

## Features
- Reception of fraud detection results via REST API
- Detailed logging of fraud alerts with comprehensive transaction data
- Separation of general service logs and fraud alert logs
- Health monitoring endpoint
- Extensible design for future notification channels

## Technical Stack
- Java 17+
- Spring Boot 3.x
- Logback for structured logging
- RESTful API

## Configuration
The application is configured through the `application.yml` file located in the `src/main/resources` directory. Key configuration parameters include:

```yaml
server:
  port: 8083
  shutdown: graceful

spring:
  application:
    name: notification-service
  
  # Graceful shutdown
  lifecycle:
    timeout-per-shutdown-phase: 20s
```


### Environment Variables
The following environment variables can be used to override the default configuration:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port number | 8083 (dev), 8081 (prod) |
| `NOTIFICATION_EMAIL_ENABLED` | Enable email notifications | false |
| `NOTIFICATION_SMS_ENABLED` | Enable SMS notifications | false |
| `FRAUD_ENGINE_URL` | URL of the fraud detection engine | http://fraud-detection-engine:8080 |

## Log Files

The service uses two log files:

1. `logs/notification-service.log`: General service log file
2. `logs/fraud-alerts.log`: Dedicated log file for fraudulent transactions

The format of the fraud alert log is:
```
timestamp WARN - FRAUD ALERT: Transaction {transactionId} from account {accountId} flagged as fraudulent
timestamp WARN - FRAUD DETAILS: {full JSON of the fraud detection result}
timestamp WARN - FRAUD RULES TRIGGERED: {list of triggered rules} for transaction {transactionId}
```

The logging configuration is managed through `logback-spring.xml`, which defines:

- Log formatting
- Log rotation policies (10MB files, 7 days for service logs, 30 days for fraud logs)
- Separate appenders for service and fraud logs

## API Endpoints

### Fraud Notification API
- `POST /api/notifications/fraud-result` - Submit a fraud detection result to the notification service

Example request body:
```json
{
  "transactionId": "TRANS123456",
  "accountId": "ACC987654",
  "fraudulent": true,
  "triggeredRules": ["AMOUNT_THRESHOLD", "SUSPICIOUS_ACCOUNT"],
  "processingTimeMs": 45,
  "transactionAmount": 1250.00,
  "ipAddress": "192.168.1.1",
  "deviceId": "DEV123",
  "countryCode": "US",
  "deviceType": "mobile",
  "channel": "web"
}
```

### Health Monitoring API
- `GET /api/notifications/health` - Check the health status of the notification service

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
java -jar target/notify-service-0.0.1-SNAPSHOT.jar
```

For production configuration:
```bash
java -jar target/notify-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Running with Docker
To run the application using Docker:

```bash
docker build -t notify-service .
docker run -p 8081:8081 notify-service
```

### Running with docker-compose
The service can be run as part of the full system using docker-compose:

```bash
cd /path/to/project/root
docker-compose up notify-service
```

## Extending the Service

The service is designed to be extended with additional notification channels. The current implementation:

1. Logs all fraud alerts to a dedicated log file
2. Provides hooks in the `NotificationService` class for adding additional notification methods

To implement a new notification channel (e.g., email, SMS):
1. Add appropriate configuration to `application-prod.yml`
2. Extend the `NotificationService` class with new notification methods
3. Use the existing fraud detection result data to populate notifications

## Troubleshooting
- **No fraud alerts in log**: Ensure that fraud detection results are being sent to the API endpoint and that `isFraudulent` is set to `true`
- **Logs not appearing**: Check that the logs directory exists and is writable
- **Service not starting**: Check the console output for error messages, usually related to port conflicts

## License
This project is proprietary and confidential. 