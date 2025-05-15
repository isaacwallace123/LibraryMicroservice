#!/bin/bash

API_URLS=(
  "http://localhost:8080/api/v1/members"
  "http://localhost:8080/api/v1/authors"
  "http://localhost:8080/api/v1/employees"
  "http://localhost:8080/api/v1/transactions"
  "http://localhost:8080/api/v1/inventory"
)

generate_random_value_for_key() {
  local key=$1
  case "$key" in
    firstName) echo "\"$(shuf -n1 -e Alice Bob Carol David Emma Nora Jason Sophia)$(shuf -i 1-9999 -n 1)\"" ;;
    lastName) echo "\"$(shuf -n1 -e Johnson Lee Patel Smith Wong Evans Garcia)\"" ;;
    email) echo "\"user$(date +%s%N | cut -c10-14)+$RANDOM@example.com\"" ;;
    genre) echo "\"$(shuf -n1 -e Fantasy Adventure Mystery Sci-Fi Horror Thriller Non-fiction)\"" ;;
    pseudonym) echo "\"$(shuf -n1 -e A.C. R.G. B.K. T.S. M.L. C.J. S.K. G.K. M.T. H.G. H.W.)\"" ;;
    *) echo "\"unknown\"" ;;
  esac
}

get_randomize_fields_for_url() {
  case "$1" in
    *members*) echo "firstName lastName email" ;;
    *authors*) echo "firstName lastName pseudonym" ;;
    *employees*) echo "firstName lastName email" ;;
    *inventory*) echo "genre title publisher" ;;
    *transactions*) echo "" ;;
    *) echo "firstName lastName email" ;;
  esac
}

randomize_payload_fields() {
  local payload=$1
  shift
  local fields=("$@")
  local result="$payload"

  for field in "${fields[@]}"; do
    value=$(generate_random_value_for_key "$field")
    result=$(echo "$result" | jq --argjson val "$value" ".${field} = \$val")
  done

  echo "$result"
}

get_id_key_from_url() {
  local url="$1"
  local endpoint=$(basename "$url")

  if [[ "$endpoint" == "inventory" ]]; then
    echo "bookid"
  else
    echo "${endpoint%s}id"
  fi
}

print_response() {
  local STATUS=$1
  local BODY=$2
  echo "Status Code: $STATUS"
  # echo "Response Body: $BODY"
  echo "--------------------------------------"
}

for BASE_URL in "${API_URLS[@]}"; do
  echo "Testing $BASE_URL"
  echo "----------------------------------------------"

  echo "GET ALL:"
  RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"

  ID_KEY=$(get_id_key_from_url "$BASE_URL")
  ID=$(echo "$BODY" | jq -r ".[0].${ID_KEY}")
  if [[ "$ID" == "null" || -z "$ID" ]]; then
    echo "No ID found in response. Skipping GetById/Put/Delete tests."
    echo "--------------------------------"
    continue
  fi

  echo "GET BY ID: $ID"
  RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/$ID")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"

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

  # Randomize relevant fields only
  read -ra FIELDS <<< "$(get_randomize_fields_for_url "$BASE_URL")"
  POST_PAYLOAD=$(randomize_payload_fields "$POST_PAYLOAD" "${FIELDS[@]}")

  echo "Randomized POST Payload: $POST_PAYLOAD"
  RESPONSE=$(curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -d "$POST_PAYLOAD" "$BASE_URL")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"

  echo "PUT (update existing resource):"
  UPDATED_PAYLOAD=$(randomize_payload_fields "$POST_PAYLOAD" "${FIELDS[@]}")
  echo "PUT Payload: $UPDATED_PAYLOAD"
  RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT -H "Content-Type: application/json" -d "$UPDATED_PAYLOAD" "$BASE_URL/$ID")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"

  echo "DELETE (remove resource):"
  RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/$ID")
  BODY=$(echo "$RESPONSE" | head -n -1)
  STATUS=$(echo "$RESPONSE" | tail -n1)
  print_response "$STATUS" "$BODY"
done
echo "----------------------------------------------\n\n"

echo -n "Would you like to run integration tests? (y/n): "
read -r answer

if [[ "$answer" =~ ^[Yy]$ ]]; then
  echo "▶ Running integration tests..."
  ./run_integration_tests.bash
else
  echo "🛑 Skipping integration tests."
fi