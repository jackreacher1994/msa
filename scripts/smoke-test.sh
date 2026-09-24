#!/usr/bin/env bash
# End-to-end smoke test through Kong: get a token from Keycloak, then exercise both services.
set -euo pipefail
KONG=${KONG_URL:-http://localhost:8000}
KEYCLOAK=${KEYCLOAK_URL:-http://localhost:8080}

echo "== 1. Unauthenticated call is rejected by Kong (expect 401)"
curl -s -o /dev/null -w "%{http_code}\n" "$KONG/api/v1/countries"

echo "== 2. Get an access token for test user alice (OIDC password grant, dev only)"
TOKEN=$(curl -s -X POST "$KEYCLOAK/realms/customer/protocol/openid-connect/token" \
  -d grant_type=password -d client_id=customer-app -d username=alice -d password=alice123 \
  | sed -E 's/.*"access_token":"([^"]+)".*/\1/')
AUTH="Authorization: Bearer $TOKEN"

echo "== 3. Supporting service (CRUD): list countries"
curl -s -H "$AUTH" "$KONG/api/v1/countries"; echo

echo "== 4. Core service: register a customer"
EMAIL="alice.$(date +%s)@example.com"
CUSTOMER=$(curl -s -H "$AUTH" -H 'Content-Type: application/json' -X POST "$KONG/api/v1/customers" \
  -d "{\"fullName\":\"Alice Nguyen\",\"email\":\"$EMAIL\",\"phoneNumber\":\"+84901234567\",\"countryCode\":\"VN\"}")
echo "$CUSTOMER"
ID=$(echo "$CUSTOMER" | sed -E 's/.*"id":"([^"]+)".*/\1/')

echo "== 5. Core service: update the customer profile"
curl -s -H "$AUTH" -H 'Content-Type: application/json' -X PUT "$KONG/api/v1/customers/$ID/profile" \
  -d '{"fullName":"Alice Tran","phoneNumber":"+6591234567","countryCode":"SG"}'; echo

echo "== 6. Business rule: inactive country is rejected (expect 422)"
curl -s -H "$AUTH" -H 'Content-Type: application/json' -X PUT "$KONG/api/v1/customers/$ID/profile" \
  -d '{"fullName":"Alice Tran","phoneNumber":"+6591234567","countryCode":"AQ"}'; echo

echo "== 7. Get the customer"
curl -s -H "$AUTH" "$KONG/api/v1/customers/$ID"; echo
