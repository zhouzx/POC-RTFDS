# Fraud Detection Engine

## Overview
The Fraud Detection Engine is the core component of the fraud detection system that processes financial transactions to identify potentially fraudulent activities. It evaluates each transaction against a set of configurable rules and generates fraud detection results which are sent to the Notification Service for further action.

## Features
- Real-time transaction fraud evaluation
- Rule-based detection system with multiple rule types
- Configurable rule parameters (thresholds, patterns)
- Rule enabling/disabling without service restart
- REST API for rule management and direct fraud detection
- Transaction history and rule effectiveness tracking
- Extensible architecture for adding new rule types

## Technical Stack
- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (embedded)
- RESTful API

## Configuration
The application uses Spring Boot's configuration system with YAML files:

```yaml
# Key configuration parameters (application.yml)
spring:
  datasource:
    url: jdbc:h2:mem:frauddb  # In-memory database for development
    username: sa
    password: 
  
  h2:
    console:
      enabled: true  # Enable H2 web console for development
      path: /h2-console

# Fraud detection settings
fraud:
  threshold:
    amount: ${FRAUD_THRESHOLD_AMOUNT:1000.0}
    suspicious-ip-enabled: ${FRAUD_CHECK_SUSPICIOUS_IP:true}

# Notification service settings
notification:
  service:
    url: ${NOTIFICATION_SERVICE_URL:http://localhost:8083}
    endpoint: ${NOTIFICATION_SERVICE_ENDPOINT:/api/notifications/fraud-result}
    enabled: ${NOTIFICATION_SERVICE_ENABLED:true}
```

For production deployment, a separate `application-prod.yml` configuration is provided that uses file-based H2 storage and disables the H2 console.

### Environment Variables
Key environment variables include:

| Variable | Description | Default |
|----------|-------------|---------|
| `FRAUD_THRESHOLD_AMOUNT` | Default threshold for amount-based rules | 1000.0 |
| `FRAUD_CHECK_SUSPICIOUS_IP` | Enable/disable IP-based checks | true |
| `NOTIFICATION_SERVICE_URL` | URL of notification service | http://localhost:8083 (dev), http://notify-service:8081 (prod) |
| `NOTIFICATION_SERVICE_ENABLED` | Enable/disable notification service integration | true |

## Rule Types
The engine supports the following types of fraud detection rules:

1. **AMOUNT_THRESHOLD**: Flags transactions exceeding a specified amount threshold
2. **SUSPICIOUS_ACCOUNT**: Flags transactions from accounts matching suspicious patterns (regex-based)

The rule system is extensible, with database schema supporting additional rule types.

## API Endpoints

### Rule Management
- `GET /api/rules` - List all fraud detection rules
- `GET /api/rules/{id}` - Get a specific rule by ID
- `POST /api/rules` - Create a new rule
- `PUT /api/rules/{id}` - Update an existing rule
- `DELETE /api/rules/{id}` - Delete a rule
- `PATCH /api/rules/{id}/enable` - Enable a specific rule
- `PATCH /api/rules/{id}/disable` - Disable a specific rule

### Fraud Detection
- `POST /api/fraud/detect` - Process a transaction for fraud detection
- `POST /api/fraud/rules/refresh` - Refresh the rule engine (reload rules)
- `GET /api/fraud/health` - Health check endpoint

## Transaction Data Format
The engine expects transactions in the following format:

```json
{
  "transactionId": "TRANS123456",
  "accountId": "ACC987654",
  "amount": 1250.00,
  "currency": "USD",
  "timestamp": "2023-06-15T10:30:00Z",
  "merchantId": "MERCH001",
  "merchantCategory": "RETAIL",
  "location": {
    "country": "US",
    "city": "New York"
  },
  "deviceId": "DEV123",
  "ipAddress": "192.168.1.1"
}
```

## Fraud Detection Response
Responses are returned in the following format:

```json
{
  "transactionId": "TRANS123456",
  "accountId": "ACC987654",
  "fraudulent": true,
  "triggeredRules": ["AMOUNT_THRESHOLD", "SUSPICIOUS_ACCOUNT"],
  "riskScore": 85,
  "processingTimeMs": 45,
  "timestamp": "2023-06-15T10:30:05Z"
}
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building
```bash
mvn clean package
```

### Running Locally
For development with in-memory database:
```bash
java -jar target/fraud-detection-engine-0.0.1-SNAPSHOT.jar
```

For production with persistent database:
```bash
java -jar target/fraud-detection-engine-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Running with Docker
To run with Docker:
```bash
# Build the image
docker build -t fraud-detection-engine .

# Run the container
docker run -p 8080:8080 fraud-detection-engine
```

### Database Schema
The system uses two main tables:
- `fraud_rules`: Stores all fraud detection rules
- `fraud_detection_results`: Records all processed transactions and detection results

Database tables are automatically created on startup using the `schema.sql` script, and initial rules are loaded from `data.sql`.

## Troubleshooting
- **Rule not working**: Check if the rule is enabled and the rule parameters are correctly set
- **Missing transaction fields**: Ensure all required fields are included in the transaction data
- **Performance issues**: Consider increasing Java heap size and database connection pool settings

## License
This project is proprietary and confidential. 