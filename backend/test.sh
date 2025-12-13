#!/bin/bash

# Цвета для вывода
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

AUTH_URL="http://localhost:8082"
TRANSACTIONS_URL="http://localhost:8080"

echo -e "${BLUE}=== 1. Регистрация пользователя ===${NC}"
REGISTER_RESPONSE=$(curl -s -X POST $AUTH_URL/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo'$(date +%s)'@example.com",
    "password": "securePassword123",
    "full_name": "Demo User"
  }')
echo $REGISTER_RESPONSE | jq .
EMAIL=$(echo $REGISTER_RESPONSE | jq -r '.email')
echo -e "\n"

echo -e "${BLUE}=== 2. Логин и получение токена ===${NC}"
LOGIN_RESPONSE=$(curl -s -X POST $AUTH_URL/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "'$EMAIL'",
    "password": "securePassword123"
  }')
echo $LOGIN_RESPONSE | jq .
ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.access_token')
REFRESH_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.refresh_token')
echo -e "${GREEN}✓ Токен получен${NC}\n"

echo -e "${BLUE}=== 3. Создание дохода (с токеном) ===${NC}"
curl -s -X POST $TRANSACTIONS_URL/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "type": "income",
    "category": "salary",
    "amount": 5000.00,
    "description": "Monthly salary",
    "date": "2025-12-13T10:00:00Z"
  }' | jq .
echo -e "\n"

echo -e "${BLUE}=== 4. Создание расхода (с токеном) ===${NC}"
curl -s -X POST $TRANSACTIONS_URL/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "type": "expense",
    "category": "food",
    "amount": 150.00,
    "description": "Groceries",
    "date": "2025-12-13T12:00:00Z"
  }' | jq .
echo -e "\n"

echo -e "${BLUE}=== 5. Получение всех транзакций (с токеном) ===${NC}"
curl -s -X GET $TRANSACTIONS_URL/api/v1/transactions \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq .
echo -e "\n"

echo -e "${BLUE}=== 6. Получение статистики (с токеном) ===${NC}"
curl -s -X GET "$TRANSACTIONS_URL/api/v1/transactions/stats?start_date=2025-01-01&end_date=2025-12-31" \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq .
echo -e "\n"

echo -e "${BLUE}=== 7. Попытка без токена (должна вернуть 401) ===${NC}"
curl -s -X GET $TRANSACTIONS_URL/api/v1/transactions \
  -w "\nHTTP Status: %{http_code}\n" | jq .
echo -e "\n"

echo -e "${BLUE}=== 8. Попытка с невалидным токеном (должна вернуть 401) ===${NC}"
curl -s -X GET $TRANSACTIONS_URL/api/v1/transactions \
  -H "Authorization: Bearer invalid_token_here" \
  -w "\nHTTP Status: %{http_code}\n" | jq .
echo -e "\n"

echo -e "${GREEN}✅ Все тесты завершены!${NC}"
echo -e "Access Token: $ACCESS_TOKEN"
echo -e "Refresh Token: $REFRESH_TOKEN"
