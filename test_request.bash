#!/bin/bash

BASE_URL="http://localhost:8080/api/v1"

declare -A SAMPLE_DATA
SAMPLE_DATA["members"]='{"name":"John Doe","email":"john@example.com"}'
SAMPLE_DATA["authors"]='{"name":"Jane Austen","genre":"Fiction"}'
SAMPLE_DATA["employees"]='{"name":"Alice Smith","position":"Manager"}'

declare -A SAMPLE_IDS
SAMPLE_IDS["members"]="123e4567-e89b-12d3-a456-556642440000"
SAMPLE_IDS["authors"]="123e4567-e89b-12d3-a456-556642440000"
SAMPLE_IDS["employees"]="6a8aeaec-cff9-4ace-a8f0-146f8ed180e5"

for resource in members authors employees; do
    echo "=== Testing $resource ==="

    # POST
    echo "--- POST $resource ---"
    curl -s -X POST "$BASE_URL/$resource" \
        -H "Content-Type: application/json" \
        -d "${SAMPLE_DATA[$resource]}"
    echo -e "\n"

    # PUT
    echo "--- PUT $resource/${SAMPLE_IDS[$resource]} ---"
    curl -s -X PUT "$BASE_URL/$resource/${SAMPLE_IDS[$resource]}" \
        -H "Content-Type: application/json" \
        -d "${SAMPLE_DATA[$resource]}"
    echo -e "\n"

    # GET by ID
    echo "--- GET $resource/${SAMPLE_IDS[$resource]} ---"
    curl -s -X GET "$BASE_URL/$resource/${SAMPLE_IDS[$resource]}"
    echo -e "\n"

    # DELETE
    echo "--- DELETE $resource/${SAMPLE_IDS[$resource]} ---"
    curl -s -X DELETE "$BASE_URL/$resource/${SAMPLE_IDS[$resource]}"
    echo -e "\n"

    # GET all
    echo "--- GET $resource (all) ---"
    curl -s -X GET "$BASE_URL/$resource"
    echo -e "\n\n"
done
