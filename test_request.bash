#!/bin/bash

API_URLS=(
  "http://localhost:8080/api/v1/members"
  "http://localhost:8080/api/v1/authors"
  "http://localhost:8080/api/v1/employees"
  "http://localhost:8080/api/v1/transactions"
  "http://localhost:8080/api/v1/inventory"
)

get_id_key_from_url() {
  local url="$1"
  local endpoint=$(basename "$url")

  if [[ "$endpoint" == "inventory" ]]; then
    echo "bookid"
    return
  fi

  if [[ "$endpoint" == *s ]]; then
    echo "${endpoint%s}id"
  else
    echo "${endpoint}id"
  fi
}

print_response() {
  local STATUS=$1
  local BODY=$2
  echo "Status Code: $STATUS"
  echo "Response Body: $BODY"
  echo "--------------------------------------"
}

for BASE_URL in "${API_URLS[@]}"; do
  echo "Testing $BASE_URL"
  echo "----------------------------------------------"

  echo "GET ALL:"
  RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n 1)
  print_response "$STATUS" "$BODY"

  ID_KEY=$(get_id_key_from_url "$BASE_URL")
  ID=$(echo "$BODY" | jq -r ".[0].${ID_KEY}")
  echo "$ID_KEY"
  if [[ "$ID" == "null" || -z "$ID" ]]; then
      echo "No ID found in response. Skipping GetById/Put/Delete tests."
      echo "--------------------------------"
      continue
  fi

  # GET BY ID
  echo "GET BY ID: $ID"
  RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/$ID")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"

  # POST
  echo "POST (create new resource):"

  # Extract first item safely
  FIRST_ITEM=$(echo "$BODY" | jq ".[0]")

  if [[ -z "$FIRST_ITEM" || "$FIRST_ITEM" == "null" ]]; then
    echo "No valid item found to use for POST. Skipping."
    continue
  fi

  POST_PAYLOAD=$(echo "$FIRST_ITEM" | jq "del(.${ID_KEY}) | walk(if type == \"object\" then del(.links?) else . end)")

  echo "POST Payload: $POST_PAYLOAD"

  RESPONSE=$(curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -d "$POST_PAYLOAD" "$BASE_URL")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"
done
