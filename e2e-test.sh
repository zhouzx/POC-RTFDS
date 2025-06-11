#!/bin/bash

# Color definitions
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Starting fraud detection flow test...${NC}"

# Check if services are running
echo -e "${YELLOW}Checking if services are running...${NC}"
docker ps | grep -E 'fraud-detection-engine|notify-service|tran-simulator'
if [ $? -ne 0 ]; then
  echo -e "${RED}Error: One or more services are not running. Please start the services first.${NC}"
  echo -e "${YELLOW}You can start the services by running:${NC}"
  echo "docker-compose up -d"
  exit 1
fi

# Wait for services to fully start
echo -e "${YELLOW}Waiting for services to fully start...${NC}"
sleep 5

# Generate random transaction ID
RANDOM_ID=$(date +"%Y%m%d%H%M%S")
TIMESTAMP=$(date -u +"%Y-%m-%dT%H:%M:%S")

# Create dynamic test transaction data
echo -e "${YELLOW}Creating new test transaction data, Transaction ID: TX-TEST-${RANDOM_ID}...${NC}"
cat > test-transaction-${RANDOM_ID}.json << EOF
{
  "transactionId": "TX-TEST-${RANDOM_ID}",
  "amount": 100000.00,
  "accountId": "ACC-TEST-001",
  "merchantId": "MERCHANT-001",
  "location": "Beijing, China",
  "timestamp": "${TIMESTAMP}",
  "ipAddress": "192.168.1.100",
  "deviceId": "DEVICE-TEST-001"
}
EOF

# Send a 100,000 transaction to tran-simulator
echo -e "${YELLOW}Sending test transaction with amount 100,000...${NC}"
curl -X POST http://localhost:8083/api/simulator/send \
  -H "Content-Type: application/json" \
  -d @test-transaction-${RANDOM_ID}.json

# Wait for transaction processing
echo -e "${YELLOW}Waiting for transaction processing...${NC}"
sleep 10

# Check notify-service logs
echo -e "${YELLOW}Checking notify-service logs...${NC}"
echo "==================== Notify Service Logs ===================="
docker logs notify-service | grep -E "FRAUD|TX-TEST-${RANDOM_ID}" | tail -n 10

# Check fraud-detection-engine logs
echo -e "${YELLOW}Checking fraud-detection-engine logs...${NC}"
echo "==================== Fraud Detection Engine Logs ===================="
docker logs fraud-detection-engine | grep "TX-TEST-${RANDOM_ID}" | tail -n 10

echo -e "${GREEN}Test completed!${NC}"

# Clean up temporary files
rm test-transaction-${RANDOM_ID}.json 