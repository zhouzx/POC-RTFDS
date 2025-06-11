#!/bin/bash

echo "Running tests and generating coverage reports for all services"

for service in fraud-detection-engine notify-service tran-integrator; do
  echo "=== Running tests and generating coverage report for $service ==="
  cd $service
  mvn clean test -Dnet.bytebuddy.experimental=true
  cd ..
done

echo "Completed tests and coverage report generation for all services"

echo "Coverage report locations:"
for service in fraud-detection-engine notify-service tran-integrator tran-simulator; do
  if [ -d "$service/target/site/jacoco" ]; then
    echo "$service: target/site/jacoco/index.html"
  else
    echo "$service: No coverage report generated"
  fi
done 