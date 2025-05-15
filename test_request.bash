#!/bin/bash

API_URLS=(
  "http://localhost:8080/api/v1/members"
  "http://localhost:8080/api/v1/authors"
  "http://localhost:8080/api/v1/employees"
  "http://localhost:8080/api/v1/transactions"
  "http://localhost:8080/api/v1/inventory"
)

declare -A SAMPLE_POST_DATA

SAMPLE_POST_DATA["members"]="{
  \"firstName\": \"Alice\",
  \"lastName\": \"Johnson\",
  \"email\": \"alice.johnson+$(date +%s)@example.com\",
  \"address\": {
    \"street\": \"999 New Street\",
    \"city\": \"Edmonton\",
    \"postal\": \"T5T 1A1\",
    \"province\": \"Alberta\"
  },
  \"phone\": {
    \"number\": \"514-123-4567\",
    \"type\": \"MOBILE\"
  }
}"

SAMPLE_POST_DATA["authors"]="{
  \"firstName\": \"Arthur\",
  \"lastName\": \"Clarke\",
  \"pseudonym\": \"A.C.\"
}"

SAMPLE_POST_DATA["employees"]="{
  \"firstName\": \"Nora\",
  \"lastName\": \"Evans\",
  \"dob\": \"1990-01-01\",
  \"age\": 34,
  \"email\": \"nora.evans+$(date +%s)@workmail.com\",
  \"title\": \"LIBRARIAN\",
  \"salary\": 5500.00
}"

SAMPLE_POST_DATA["inventory"]="{
  \"authorid\": \"123e4567-e89b-12d3-a456-556642440000\",
  \"title\": \"New Horizons\",
  \"genre\": \"Sci-Fi\",
  \"publisher\": \"Galaxy Press\",
  \"released\": \"$(date -u +%Y-%m-%dT%H:%M:%S)\",
  \"stock\": 12,
  \"availability\": \"AVAILABLE\"
}"

SAMPLE_POST_DATA["transactions"]="{
  \"memberid\": \"123e4567-e89b-12d3-a456-556642440000\",
  \"bookid\": \"c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5\",
  \"employeeid\": \"61d2d9f8-e144-4984-8bcb-7fa29ef4fdf6\",
  \"transactionDate\": \"$(date -Iseconds)\",
  \"status\": \"PENDING\",
  \"payment\": {
    \"method\": \"CREDIT\",
    \"currency\": \"CAD\",
    \"amount\": 19.99
  }
}"

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

  if echo "$BODY" | jq -e 'type == "array"' > /dev/null 2>&1; then
    FIRST_ITEM=$(echo "$BODY" | jq ".[0]")
  else
    FIRST_ITEM=$(echo "$BODY")
  fi

  if [[ -z "$FIRST_ITEM" || "$FIRST_ITEM" == "null" ]]; then
    echo "No valid item found to use for POST. Skipping."
    echo "--------------------------------"
    continue
  fi

  ID_KEY=$(get_id_key_from_url "$BASE_URL")
  POST_PAYLOAD=$(echo "$FIRST_ITEM" | jq "del(.${ID_KEY}, .links, .book, .member, .employee, ._links)")

  echo "POST Payload (from existing item): $POST_PAYLOAD"

  RESPONSE=$(curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -d "$POST_PAYLOAD" "$BASE_URL")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"

  # Retry with sample data if conflict occurred
  if [[ "$STATUS" == "409" ]]; then
    echo "Conflict detected (409). Retrying with sample data..."

    RESOURCE=$(basename "$BASE_URL")
    PAYLOAD=${SAMPLE_POST_DATA[$RESOURCE]}

    if [[ -n "$PAYLOAD" ]]; then
      echo "Sample POST Payload: $PAYLOAD"
      RESPONSE=$(curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -d "$PAYLOAD" "$BASE_URL")
      BODY=$(echo "$RESPONSE" | head -n -1)
      STATUS=$(echo "$RESPONSE" | tail -n1)
      print_response "$STATUS" "$BODY"
    else
      echo "No sample data found for $RESOURCE. Skipping."
    fi
  fi
done
