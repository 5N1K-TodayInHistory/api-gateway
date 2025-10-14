#!/bin/bash

# Debug script for API Gateway
# This script starts the application with debug configuration

echo "🚀 Starting API Gateway in DEBUG mode..."
echo "📡 Remote debug will be available on port 5005"
echo "🔧 Using debug profile with enhanced logging"
echo ""

# Set debug profile
export SPRING_PROFILES_ACTIVE=debug

# Start with debug JVM arguments
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" \
  -Dspring-boot.run.profiles=debug
