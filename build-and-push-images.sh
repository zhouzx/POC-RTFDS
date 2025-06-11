#!/bin/bash

# Set variables
REGISTRY="crpi-zb9l0odmy4zwf14t.cn-hangzhou.personal.cr.aliyuncs.com"
NAMESPACE="fraud"
TAG="latest"

# Build and push all service images to Alibaba Cloud Container Registry
build_and_push() {
  SERVICE=$1

  echo "===== Building $SERVICE image ====="
  
  # Enter service directory
  cd $SERVICE
  
  echo "Building $SERVICE in directory: $(pwd)"
  
  # Login to Alibaba Cloud Container Registry (you need to configure credentials in advance)
  # If you're already logged in, you can comment out the line below
  # docker login --username=your_username $REGISTRY
  
  # Build image
  docker build --platform linux/amd64 -t $REGISTRY/$NAMESPACE/$SERVICE:$TAG .
  
  # Push image
  docker push $REGISTRY/$NAMESPACE/$SERVICE:$TAG
  
  # Return to root directory
  cd ..
  
  echo "===== $SERVICE image built and pushed successfully ====="
}

# Ensure script is run from the correct root directory
if [ ! -d "fraud-detection-engine" ] || [ ! -d "notify-service" ] || [ ! -d "tran-integrator" ]; then
  echo "Error: Please run this script from the project root directory"
  exit 1
fi

# Build and push all service images
echo "Starting to build and push all service images..."

build_and_push "fraud-detection-engine"
build_and_push "notify-service"
build_and_push "tran-integrator"
build_and_push "tran-simulator"
build_and_push "fraud-admin-ui"

echo "===== All images built and pushed successfully ====="
echo "Now you can run deploy-to-ack.sh to deploy to ACK cluster" 