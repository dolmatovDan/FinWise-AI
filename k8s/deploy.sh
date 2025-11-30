#!/bin/bash

set -e

echo "=== FinWise-AI Kubernetes Deployment ==="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}Error: kubectl is not installed${NC}"
    exit 1
fi

# Check if cluster is accessible
if ! kubectl cluster-info &> /dev/null; then
    echo -e "${RED}Error: Cannot connect to Kubernetes cluster${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Connected to Kubernetes cluster${NC}"
echo ""

# Deploy namespace and secrets
echo "Step 1: Creating namespace..."
kubectl apply -f base/namespace.yaml

echo "Step 2: Creating secrets..."
kubectl apply -f secrets/postgres-secret.yaml

# Check if JWT keys secret exists
if ! kubectl get secret jwt-keys -n finwise &> /dev/null; then
    echo -e "${YELLOW}Warning: JWT keys secret not found${NC}"
    echo "You need to create it manually:"
    echo "  kubectl create secret generic jwt-keys \\"
    echo "    --from-file=private.pem=../backend/auth/keys/private.pem \\"
    echo "    --from-file=public.pem=../backend/auth/keys/public.pem \\"
    echo "    -n finwise"
    echo ""
    read -p "Press Enter to continue or Ctrl+C to abort..."
fi

# Deploy ConfigMaps
echo "Step 3: Creating ConfigMaps..."
kubectl apply -f base/configmap.yaml

# Deploy PostgreSQL
echo "Step 4: Deploying PostgreSQL..."
kubectl apply -f base/postgres.yaml

echo "Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n finwise --timeout=300s
echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
echo ""

# Run migrations
echo "Step 5: Running database migrations..."

echo "Running auth migrations..."
kubectl apply -f jobs/migrate-auth.yaml
kubectl wait --for=condition=complete job/migrate-auth -n finwise --timeout=300s
echo -e "${GREEN}✓ Auth migrations completed${NC}"

echo "Running transactions migrations..."
kubectl apply -f jobs/migrate-transactions.yaml
kubectl wait --for=condition=complete job/migrate-transactions -n finwise --timeout=300s
echo -e "${GREEN}✓ Transactions migrations completed${NC}"
echo ""

# Deploy services
echo "Step 6: Deploying application services..."

echo "Deploying Auth service..."
kubectl apply -f base/auth.yaml

echo "Deploying Transactions service..."
kubectl apply -f base/transactions.yaml

echo "Deploying ML-API service..."
kubectl apply -f base/ml-api.yaml

echo ""
echo "Waiting for services to be ready..."
kubectl wait --for=condition=available deployment/auth -n finwise --timeout=300s
kubectl wait --for=condition=available deployment/transactions -n finwise --timeout=300s
kubectl wait --for=condition=available deployment/ml-api -n finwise --timeout=300s
echo -e "${GREEN}✓ All services are ready${NC}"
echo ""

# Deploy monitoring
echo "Step 7: Deploying monitoring..."
kubectl apply -f base/prometheus.yaml
kubectl wait --for=condition=available deployment/prometheus -n finwise --timeout=300s
echo -e "${GREEN}✓ Prometheus is ready${NC}"
echo ""

# Display status
echo "=== Deployment Summary ==="
echo ""
kubectl get all -n finwise
echo ""

echo -e "${GREEN}✓ Deployment completed successfully!${NC}"
echo ""
echo "To access services, use port-forwarding:"
echo "  Auth:         kubectl port-forward -n finwise svc/auth 8082:8082"
echo "  Transactions: kubectl port-forward -n finwise svc/transactions 8080:8080"
echo "  ML-API:       kubectl port-forward -n finwise svc/ml-api 8081:8081"
echo "  Prometheus:   kubectl port-forward -n finwise svc/prometheus 9090:9090"
echo ""
echo "Check health:"
echo "  curl http://localhost:8082/health"
echo "  curl http://localhost:8080/health"
echo "  curl http://localhost:8081/health"
