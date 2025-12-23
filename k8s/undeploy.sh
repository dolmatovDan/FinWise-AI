#!/bin/bash

set -e

echo "=== FinWise-AI Kubernetes Cleanup ==="
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

# Confirm deletion
echo -e "${YELLOW}Warning: This will delete all FinWise-AI resources from the cluster${NC}"
read -p "Are you sure? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Aborted"
    exit 0
fi

echo ""
echo "Deleting resources..."

# Delete in reverse order
echo "Deleting monitoring..."
kubectl delete -f base/prometheus.yaml --ignore-not-found=true

echo "Deleting application services..."
kubectl delete -f base/ml-api.yaml --ignore-not-found=true
kubectl delete -f base/transactions.yaml --ignore-not-found=true
kubectl delete -f base/auth.yaml --ignore-not-found=true

echo "Deleting migration jobs..."
kubectl delete -f jobs/migrate-transactions.yaml --ignore-not-found=true
kubectl delete -f jobs/migrate-auth.yaml --ignore-not-found=true

echo "Deleting PostgreSQL..."
kubectl delete -f base/postgres.yaml --ignore-not-found=true

echo "Deleting ConfigMaps..."
kubectl delete -f base/configmap.yaml --ignore-not-found=true

echo "Deleting secrets..."
kubectl delete -f secrets/postgres-secret.yaml --ignore-not-found=true
kubectl delete secret jwt-keys -n finwise --ignore-not-found=true

# Optional: Delete namespace (this will delete everything)
echo ""
read -p "Delete namespace 'finwise'? This will remove all resources including PVCs (yes/no): " delete_ns

if [ "$delete_ns" = "yes" ]; then
    kubectl delete namespace finwise
    echo -e "${GREEN}✓ Namespace deleted${NC}"
else
    echo "Namespace kept. To delete manually:"
    echo "  kubectl delete namespace finwise"
fi

echo ""
echo -e "${GREEN}✓ Cleanup completed${NC}"
