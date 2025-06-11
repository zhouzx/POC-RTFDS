# Fraud Detection System

## Overview
This is a comprehensive fraud detection system built using a microservices architecture. The system processes financial transactions in real-time, applies configurable fraud detection rules, and generates alerts for potentially fraudulent activities. It provides a complete end-to-end solution from transaction ingestion to fraud detection, notification, and administrative management.

## Key Features
- Real-time transaction fraud detection with configurable rules
- Rule-based detection system with support for different rule types:
  - Amount threshold detection
  - Suspicious account pattern detection
- Administrative interface for rule management
- Transaction simulation for testing and demonstration
- Comprehensive logging and notification system
- Containerized deployment with Docker and Kubernetes support

## System Architecture

The system consists of the following microservices:

```
┌────────────────┐      ┌────────────────┐      ┌────────────────┐      ┌────────────────┐
│                │      │                │      │                │      │                │
│  Transaction   │─────▶│  Transaction   │─────▶│     Fraud      │─────▶│  Notification  │
│   Simulator    │      │   Integrator   │      │   Detection    │      │    Service     │
│                │      │                │      │    Engine      │      │                │
└────────────────┘      └────────────────┘      └────────────────┘      └────────────────┘
                                                        ▲
                                                        │
                                                        ▼
                                                ┌────────────────┐
                                                │                │
                                                │  Fraud Admin   │
                                                │      UI        │
                                                │                │
                                                └────────────────┘
```

### Components

1. **Transaction Simulator**: Generates simulated transaction data for testing and demonstration purposes
2. **Transaction Integrator**: Receives, validates, and enriches transaction data before sending it to the fraud detection engine
3. **Fraud Detection Engine**: Core service that applies fraud detection rules to transactions and identifies potentially fraudulent activities
4. **Notification Service**: Processes fraud detection results and generates appropriate notifications and alerts
5. **Fraud Admin UI**: Web-based user interface for managing fraud detection rules and reviewing fraud detection configurations

## Technologies Used

- **Programming Languages**: Java 17, TypeScript
- **Frameworks**: Spring Boot 3.x, React 18
- **Databases**: H2 (embedded)
- **Containerization**: Docker, Kubernetes
- **UI**: React with Material-UI
- **Testing**: JUnit

## Deployment Instructions

### Prerequisites

- Docker and Docker Compose (for local deployment)
- Access to Alibaba Cloud Container Service (for cloud deployment)
- Alibaba Cloud Container Registry (ACR) access
- kubectl configured for your ACK cluster

### Local Deployment with Docker Compose

The easiest way to run the entire system locally is using Docker Compose:

```bash
# Clone the repository
git clone
cd fraud-detection-system

# Start all services
docker-compose up -d
```

This will start all the microservices in detached mode. To view logs:

```bash
# View logs of all services
docker-compose logs -f

# View logs of a specific service
docker-compose logs -f fraud-detection-engine
```

To stop the services:

```bash
docker-compose down
```

### Cloud Deployment to Alibaba Cloud Container Service (ACK)

#### Step 1: Configure ACR Authentication

First, create a Kubernetes secret for accessing your Alibaba Cloud Container Registry:

```bash
# Replace these values with your actual credentials
export ACR_REGISTRY=crpi-XXXXXXXXXX.cn-hangzhou.personal.cr.aliyuncs.com
export ACR_USERNAME=your-username
export ACR_PASSWORD=your-password
export ACR_NAMESPACE=fraud

# Create the Kubernetes secret
kubectl create secret docker-registry acr-auth \
  --docker-server=$ACR_REGISTRY \
  --docker-username=$ACR_USERNAME \
  --docker-password=$ACR_PASSWORD \
  --docker-email=your-email@example.com
```

#### Step 2: Build and Push Docker Images to ACR

Use the provided script to build and push all images to Alibaba Cloud Container Registry:

```bash
# Make the script executable
chmod +x build-and-push-images.sh

# Run the script
./build-and-push-images.sh
```

The script does the following:
- Builds Docker images for all microservices
- Tags the images with the ACR registry
- Pushes the images to ACR

#### Step 3: Deploy to ACK

Use the provided script to deploy all services to your ACK cluster:

```bash
# Make the script executable
chmod +x deploy-to-ack.sh

# Run the script
./deploy-to-ack.sh
```

The script deploys:
- ConfigMaps for service configurations
- Secrets for sensitive information
- Deployments for all microservices
- Services for network connectivity
- Ingress resources for external access

#### Step 4: Verify Deployment

Verify that all pods are running:

```bash
kubectl get pods
```

Check the services and their endpoints:

```bash
kubectl get svc
```

Access the Fraud Admin UI through the ingress URL:

```bash
kubectl get ingress
```

## Resilience Testing

The system includes a resilience testing script to verify that the microservices can recover from various failure scenarios when deployed in Kubernetes. This is crucial for ensuring high availability and fault tolerance in production environments.

### Running Resilience Tests

To perform resilience testing on your deployed services in Kubernetes:

```bash
# Make the script executable
chmod +x resilience-testing.sh

# Run the resilience tests
./resilience-testing.sh
```

The script performs the following tests:

1. **Pod Restart Test**: Simulates pod failures by deleting pods and verifying that Kubernetes automatically restarts them and that they return to a healthy state.

2. **Service Load Test**: Tests the service's ability to scale under load by temporarily increasing the number of replicas and verifying that the scaling operation completes successfully.

3. **Network Resilience Test**: Verifies connectivity between services to ensure that the service mesh and network policies are properly configured.

### Test Results Interpretation

- **Green Messages**: Indicate successful tests
- **Red Messages**: Indicate test failures that require investigation
- **Yellow Messages**: Indicate test progress or information

Address any failures by:
1. Checking pod logs for error messages
2. Verifying resource limits and requests
3. Ensuring health checks are properly configured
4. Confirming network policies allow necessary communication

## Testing Instructions

### Testing with Simulated Transactions

The system includes a transaction simulator to generate test transactions:

```bash
# Run the end-to-end test script
./e2e-test.sh
```

This script:
1. Checks if all services are running
2. Creates a test transaction with a high amount ($100,000)
3. Sends the transaction through the system
4. Checks the logs to verify fraud detection

### Running Unit Tests with Coverage

To run all unit tests with coverage reports:

```bash
# Run tests for all Java components
./run-tests-with-coverage.sh
```

Coverage reports will be available in the `target/site/jacoco` directory of each service.

## Monitoring and Logs

### Viewing Logs

You can view the logs for each service using the following script:

```bash
# View logs for all services
./check-service-logs.sh all

# View logs for a specific service
./check-service-logs.sh fraud-detection-engine
```

### Health Checks

Each service exposes a health endpoint:

- Fraud Detection Engine: http://localhost:8080/api/fraud/health
- Notification Service: http://localhost:8083/api/notifications/health

## Component Documentation

Each component includes its own detailed README with component-specific information:

- [Fraud Detection Engine](./fraud-detection-engine/README.md)
- [Transaction Integrator](./tran-integrator/README.md)
- [Notification Service](./notify-service/README.md)
- [Transaction Simulator](./tran-simulator/README.md)
- [Fraud Admin UI](./fraud-admin-ui/README.md)

## License

This project is proprietary and confidential. 