#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PROJECT_ROOT="$SCRIPT_DIR/.."

# Build the Application
cd "$PROJECT_ROOT"

echo "Cleaning media data..."
docker run --rm \
  -v /home/erdouglass/projects/emdb/media-data/images:/images \
  -v /home/erdouglass/projects/emdb/media-data/movies:/movies \
  -v /home/erdouglass/projects/emdb/media-data/people:/people \
  -v /home/erdouglass/projects/emdb/media-data/series:/series \
  alpine \
  sh -c "rm -rf /images/* /movies/* /people/* /series/*"

echo "Stopping and removing old containers..."
cd "$SCRIPT_DIR"
docker compose -f docker-compose.yaml down

# Build the Docker Images and start the containers
# The --build option forces Docker to rebuild the images every time.
echo "Building Docker images..."
cd "$SCRIPT_DIR"
docker compose -f docker-compose.yaml up -d --build
tput cnorm
echo "Deployment complete."
