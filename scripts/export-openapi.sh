#!/bin/bash

# Export OpenAPI specification
# Usage: ./scripts/export-openapi.sh [port]

PORT=${1:-8080}
OUTPUT_FILE="contracts/openapi.yaml"

echo "🚀 Starting application on port $PORT..."

# Start the application in background
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=$PORT" > /dev/null 2>&1 &
APP_PID=$!

echo "⏳ Waiting for application to start..."
sleep 30

# Check if application is running
if ! curl -f -s "http://localhost:$PORT/actuator/health" > /dev/null; then
    echo "❌ Application failed to start"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo "📄 Exporting OpenAPI specification..."

# Create contracts directory if it doesn't exist
mkdir -p contracts

# Export OpenAPI spec
curl -s "http://localhost:$PORT/v3/api-docs" -o "$OUTPUT_FILE"

if [ $? -eq 0 ]; then
    echo "✅ OpenAPI specification exported to $OUTPUT_FILE"
    echo "📊 File size: $(wc -c < "$OUTPUT_FILE") bytes"
    echo "🔗 Swagger UI: http://localhost:$PORT/swagger-ui.html"
else
    echo "❌ Failed to export OpenAPI specification"
    kill $APP_PID 2>/dev/null
    exit 1
fi

# Stop the application
echo "🛑 Stopping application..."
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null

echo "🎉 Export completed successfully!"
