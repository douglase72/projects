#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PROJECT_ROOT="$SCRIPT_DIR/.."

# Build the Application
cd "$PROJECT_ROOT"

echo "Building emdb-gateway-service..."
gradle :emdb-gateway-service:clean :emdb-gateway-service:build -x :emdb-gateway-service:test

echo "Stopping and removing old containers..."
cd "$SCRIPT_DIR"
docker compose -f docker-compose.yaml down

# Build the Docker Images and start the containers
# The --build option forces Docker to rebuild the images every time.
echo "Building Docker images..."
cd "$SCRIPT_DIR"
docker compose -f docker-compose.yaml up -d --build

echo "Deployment complete."
