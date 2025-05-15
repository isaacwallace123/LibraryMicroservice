#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

MICROSERVICES=(
  "./transaction-service"
  "./inventory-service"
  "./membership-service"
  "./author-service"
  "./employee-service"
)

echo -e "${GREEN}🚀 Starting build and test for all microservices...${NC}"

for SERVICE_PATH in "${MICROSERVICES[@]}"; do
  echo -e "\n${GREEN}🔧 Processing: $SERVICE_PATH${NC}"

  if [[ ! -d "$SERVICE_PATH" ]]; then
    echo -e "${RED}❌ Directory not found: $SERVICE_PATH${NC}"
    continue
  fi

  cd "$SERVICE_PATH" || {
    echo -e "${RED}❌ Failed to cd into $SERVICE_PATH${NC}"
    continue
  }

  if [[ -x "./gradlew" ]]; then
    ./gradlew clean build test
    BUILD_RESULT=$?
    if [[ $BUILD_RESULT -ne 0 ]]; then
      echo -e "${RED}❌ Build failed for $SERVICE_PATH${NC}"
    else
      echo -e "${GREEN}✅ Build succeeded for $SERVICE_PATH${NC}"
    fi
  else
    echo -e "${RED}⚠️ gradlew not found or not executable in $SERVICE_PATH${NC}"
  fi

  cd - > /dev/null || exit
done

echo -e "\n${GREEN}🏁 All done.${NC}"
