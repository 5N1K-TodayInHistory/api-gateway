#!/bin/bash

# Remote debug script for API Gateway
# This script starts the application with remote debug configuration
# Connect your IDE to localhost:5005

echo "🚀 Starting API Gateway in REMOTE DEBUG mode..."
echo "📡 Remote debug available on: localhost:5005"
echo "🔧 Using debug profile with enhanced logging"
echo "⏳ Application will wait for debugger connection (suspend=y)"
echo ""

# Set debug profile
export SPRING_PROFILES_ACTIVE=debug

# Start with remote debug JVM arguments (suspend=y to wait for debugger)
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" \
  -Dspring-boot.run.profiles=debug
