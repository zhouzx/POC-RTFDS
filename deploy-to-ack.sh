#!/bin/bash

# Ensure k8s directory exists
if [ ! -d "k8s" ]; then
  echo "Error: k8s directory does not exist"
  exit 1
fi

# Ensure kubectl is configured
if ! command -v kubectl &> /dev/null; then
  echo "Error: kubectl command not found, please ensure kubectl is installed and configured"
  exit 1
fi

# Check ACK cluster connection
echo "Checking ACK cluster connection..."
if ! kubectl cluster-info &> /dev/null; then
  echo "Error: Cannot connect to ACK cluster, please check your kubeconfig configuration"
  exit 1
fi

# Create namespace (if it doesn't exist)
echo "Creating namespace..."
kubectl apply -f k8s/namespace.yaml

# Deploy application
echo "===== Starting deployment to ACK cluster ====="
kubectl apply -k k8s/

# Wait for deployment to complete
echo "Waiting for deployment to complete..."
sleep 5

# Check deployment status
echo "===== Checking deployment status ====="
kubectl get pods -n fraud-detection
kubectl get services -n fraud-detection
kubectl get ingress -n fraud-detection

echo "===== Deployment completed ====="
echo "You can check the application status with the following command: kubectl get all -n fraud-detection"
echo "Please ensure your images have been successfully pushed to the container registry" 