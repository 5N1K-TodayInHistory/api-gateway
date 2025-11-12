#!/bin/bash

# dev script for API Gateway
# This script starts the application with dev configuration

echo "🚀 Starting API Gateway in dev mode..."
echo "📡 Remote dev will be available on port 5005"
echo "🔧 Using dev profile with enhanced logging"
echo ""

# Set dev profile
export SPRING_PROFILES_ACTIVE=dev

# Start with dev JVM arguments
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Xdev -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" \
  -Dspring-boot.run.profiles=dev
