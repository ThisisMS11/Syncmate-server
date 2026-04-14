#!/bin/bash
set -e

PROFILE=${1:-dev}

echo "Starting SyncMate with profile: $PROFILE"
./mvnw spring-boot:run -Dspring-boot.run.profiles=$PROFILE
