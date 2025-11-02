#!/bin/bash

# Script to generate RSA key pair for JWT signing
# Usage: ./generate_keys.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PRIVATE_KEY="$SCRIPT_DIR/private.pem"
PUBLIC_KEY="$SCRIPT_DIR/public.pem"

echo "Generating RSA key pair (2048 bit)..."

# Generate private key
openssl genrsa -out "$PRIVATE_KEY" 2048
echo "✓ Private key generated: $PRIVATE_KEY"

# Extract public key from private key
openssl rsa -in "$PRIVATE_KEY" -pubout -out "$PUBLIC_KEY"
echo "✓ Public key extracted: $PUBLIC_KEY"

# Set proper permissions
chmod 600 "$PRIVATE_KEY"
chmod 644 "$PUBLIC_KEY"

echo ""
echo "Key generation complete!"
echo "Private key: $PRIVATE_KEY (DO NOT COMMIT)"
echo "Public key: $PUBLIC_KEY"
