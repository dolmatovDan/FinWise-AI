# Public Key for JWT Verification

This directory contains the public RSA key for verifying JWT tokens issued by the auth service.

## Files
- `public.pem` - Public key for verifying JWT tokens (copied from auth service)
- `public.example.pem` - Example public key for local development

## Usage

The transactions service uses this public key to verify JWT tokens in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

The public key is loaded by the JWT middleware to validate token signatures without needing access to the private key.

## Updates

When the auth service regenerates keys, copy the new `public.pem` here:
```bash
cp auth/keys/public.pem transactions/keys/
```
