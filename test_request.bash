#!/bin/bash

declare -A API_FIELDS

# ── API Endpoints ─────────────────────────────

API_FIELDS["http://localhost:8080/api/v1/members"]="firstName lastName email"
API_FIELDS["http://localhost:8080/api/v1/authors"]="firstName lastName pseudonym"
API_FIELDS["http://localhost:8080/api/v1/employees"]="firstName lastName email"
API_FIELDS["http://localhost:8080/api/v1/transactions"]=""
API_FIELDS["http://localhost:8080/api/v1/members/123e4567-e89b-12d3-a456-556642440000/transactions"]=""
API_FIELDS["http://localhost:8080/api/v1/inventory"]="genre title publisher"

###############################################################################################
#                                                                                             #
#                                                                                             #
#                                       TEST SCRIPT                                           #
#                                                                                             #
#                                                                                             #
###############################################################################################

# ── Utilities ────────────────────────────────

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[0m'

docker_control() {
  local action="$1"
  local verb icon color past
  local delay=0.1
  local spin=('⠋' '⠙' '⠹' '⠸' '⠼' '⠴' '⠦' '⠧' '⠇' '⠏')

  case "$action" in
    up)   verb="Starting"; past="started"; icon="📦"; color="${CYAN}" ;;
    down) verb="Stopping"; past="stopped"; icon="🧼"; color="${CYAN}" ;;
    *)    echo -e "${RED}❌ Unknown Docker action: $action${WHITE}"; exit 1 ;;
  esac

  echo -ne "${color}${icon} ${verb} Docker containers... ${WHITE}"

  {
    if [[ "$action" == "up" ]]; then
      $COMPOSE_CMD up --build -d > /dev/null 2>&1
    else
      $COMPOSE_CMD down > /dev/null 2>&1
    fi
  } &

  local pid=$!
  local i=0
  tput civis

  while kill -0 $pid 2>/dev/null; do
    printf "\r${color}${icon} ${verb} Docker containers... ${WHITE}\033[1m${spin[i]}\033[0m"
    ((i=(i+1)%${#spin[@]}))
    sleep $delay
  done

  tput cnorm
  wait $pid
  local exit_code=$?

  echo

  if [[ $exit_code -eq 0 ]]; then
    echo -e "\r${GREEN}✅ Docker ${past} successfully${WHITE}              "
  else
    echo -e "\r${RED}❌ Failed to ${verb,,} Docker containers${WHITE}     "
    exit 1
  fi
}

# ── Check Required Commands ──────────────────

for cmd in curl jq shuf docker; do
  if ! command -v "$cmd" > /dev/null; then
    echo -e "${RED}❌ Missing command: $cmd${WHITE}"
    exit 1
  fi
done

# ── Determine Docker Compose Command ─────────

if command -v docker-compose >/dev/null; then
  COMPOSE_CMD="docker-compose"
elif docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
else
  echo -e "${RED}❌ Docker Compose not found.${WHITE}"
  exit 1
fi

# ── Start Docker Containers ──────────────────

docker_control up

# ── Wait for Services to Become Available ────

echo -e "${CYAN}🔄 Waiting for services to become available...${WHITE}"

for url in "${!API_FIELDS[@]}"; do
  echo -en "${YELLOW}⏳ Checking $url... ${WHITE}"
  for i in {1..20}; do
    if curl -sf -o /dev/null "$url"; then
      echo -e "${GREEN}✅${WHITE}"
      break
    fi
    echo -n "."
    sleep 3
    if [[ $i -eq 20 ]]; then
      echo -e "\n${RED}❌ $url did not respond in time.${WHITE}"
      exit 1
    fi
  done
done

echo -e "${GREEN}✅ All services are up!${WHITE}\n"

# ── Test Methods ─────────────────────────────

assertCurl() {
  local expected="$1" cmd="$2"
  local response status method url color

  response=$(eval "$cmd -s -w '\n%{http_code}'")
  status=$(echo "$response" | tail -n1)

  method=$(echo "$cmd" | grep -oE '\-X[[:space:]]+[A-Z]+' | awk '{print $2}')
  method=${method:-GET}
  url=$(echo "$cmd" | grep -oE 'https?://[^[:space:]"]+')

  case "$method" in
    GET)    color="${GREEN}"  ;;
    POST)   color="${YELLOW}" ;;
    PUT)    color="${CYAN}"   ;; # Blue
    DELETE) color="${RED}"    ;;
    *)      color="${WHITE}"  ;;
  esac

  if [[ "$status" != "$expected" ]]; then
    echo -e "${RED}❌ [$status]${WHITE} ${color}$method${WHITE} $url"
    exit 1
  else
    echo -e "${GREEN}✅ [$status]${WHITE} ${color}$method${WHITE} $url"
  fi

  RESPONSE_BODY=$(echo "$response" | head -n -1)
}


ID_KEY_FROM_URL() {
  local endpoint=$(basename "$1")
  [[ "$endpoint" == "inventory" ]] && echo "bookid" || echo "${endpoint%s}id"
}

randomize_fields() {
  local json="$1"; shift
  local fields=("$@")
  local result="$json"

  for field in "${fields[@]}"; do
    local value="test_$(date +%s%N | cut -c10-14)$RANDOM"
    result=$(echo "$result" | jq --arg val "$value" ".${field} = \$val")
  done

  echo "$result"
}

test_crud_operations() {
  local base_url="$1"
  local field_string="$2"
  local id_key
  id_key=$(ID_KEY_FROM_URL "$base_url")
  read -ra fields <<< "$field_string"

  echo -e "${CYAN}🔍 Testing $base_url${WHITE}"

  # GET ALL
  assertCurl 200 "curl $base_url"
  local items="$RESPONSE_BODY"
  local count=$(echo "$items" | jq 'length')
  (( count == 0 )) && echo -e "${YELLOW}⚠️ No items. Skipping.${WHITE}" && return

  local base_item=$(echo "$items" | jq '.[0]')
  local id=$(echo "$base_item" | jq -r ".${id_key}")

  # GET BY ID
  assertCurl 200 "curl $base_url/$id"
  local base_payload=$(echo "$RESPONSE_BODY")

  # Prepare POST Payload
  local post_payload=$(echo "$base_payload" | jq "del(.${id_key}, ._links?, .links?)")
  if [[ -z "$post_payload" || "$post_payload" == "null" ]]; then
    echo -e "${YELLOW}⚠️ Cannot generate valid POST payload for $base_url. Skipping.${WHITE}"
    return
  fi

  # Randomize required fields
  post_payload=$(randomize_fields "$post_payload" "${fields[@]}")

  # POST
  assertCurl 201 "curl -X POST -H 'Content-Type: application/json' -d '$post_payload' $base_url"
  local new_id=$(echo "$RESPONSE_BODY" | jq -r ".${id_key}")

  # 409 CONFLICT
  if [[ -n "$field_string" ]]; then
    assertCurl 409 "curl -X POST -H 'Content-Type: application/json' -d '$post_payload' $base_url"
  fi

  # PUT (randomize again)
  local put_payload=$(randomize_fields "$post_payload" "${fields[@]}")
  assertCurl 200 "curl -X PUT -H 'Content-Type: application/json' -d '$put_payload' $base_url/$new_id"

  # DELETE
  assertCurl 204 "curl -X DELETE $base_url/$new_id"

  # 404 Not Found
  assertCurl 404 "curl $base_url/$new_id"

  # 422 Unprocessable Entity
  assertCurl 422 "curl $base_url/invalid-id"

  echo -e "${GREEN}✅ Endpoint tests passed for $base_url${WHITE}\n"
}

# ── Testing ──────────────────────────────────

for url in "${!API_FIELDS[@]}"; do
  id_key=$(ID_KEY_FROM_URL "$url")
  fields="${API_FIELDS[$url]}"
  test_crud_operations "$url" "$fields"
done

# ── Cleanup ──────────────────────────────────

docker_control down