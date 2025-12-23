#!/bin/bash

set -e

echo "=== Building FinWise-AI Docker Images ==="
echo ""

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

cd "$(dirname "$0")/.."

echo "Building service images..."
echo ""

echo "Building auth service..."
docker build -t finwise-auth:latest -f backend/auth/Dockerfile backend
echo -e "${GREEN}✓ Auth service built${NC}"
echo ""

echo "Building transactions service..."
docker build -t finwise-transactions:latest -f backend/transactions/Dockerfile backend
echo -e "${GREEN}✓ Transactions service built${NC}"
echo ""

echo "Building ML-API service..."
docker build -t finwise-ml-api:latest -f backend/ml-api/Dockerfile backend
echo -e "${GREEN}✓ ML-API service built${NC}"
echo ""

echo "Building migration images..."
echo ""

echo "Building auth migrations..."
docker build -t finwise-auth-migrate:latest -f backend/auth/Dockerfile.migrate backend
echo -e "${GREEN}✓ Auth migrations built${NC}"
echo ""

echo "Building transactions migrations..."
docker build -t finwise-transactions-migrate:latest -f backend/transactions/Dockerfile.migrate backend
echo -e "${GREEN}✓ Transactions migrations built${NC}"
echo ""

echo -e "${GREEN}=== All images built successfully! ===${NC}"
echo ""

if command -v minikube &> /dev/null && minikube status &> /dev/null; then
    echo -e "${YELLOW}Detected running Minikube cluster${NC}"
    read -p "Load images into Minikube? (y/n): " load_minikube
    
    if [ "$load_minikube" = "y" ] || [ "$load_minikube" = "Y" ]; then
        echo ""
        echo "Loading images into Minikube..."
        minikube image load finwise-auth:latest
        minikube image load finwise-transactions:latest
        minikube image load finwise-ml-api:latest
        minikube image load finwise-auth-migrate:latest
        minikube image load finwise-transactions-migrate:latest
        echo -e "${GREEN}✓ Images loaded into Minikube${NC}"
    fi
fi

echo ""
echo "Images:"
docker images | grep finwise
echo ""
echo "Ready to deploy! Run: ./k8s/deploy.sh"

