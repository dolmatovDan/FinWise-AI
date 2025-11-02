# RSA Keys for JWT

This directory contains RSA keys for JWT token signing and verification.

## Files
- `private.pem` - Private key for signing tokens (gitignored, 2048 bit)
- `public.pem` - Public key for verifying tokens
- `private.example.pem` - Example private key for local development
- `public.example.pem` - Example public key for local development
- `generate_keys.sh` - Script to generate new RSA key pair

## Generation

To generate new RSA keys:

```bash
cd auth/keys
./generate_keys.sh
```

This will create:
- `private.pem` (600 permissions) - DO NOT COMMIT
- `public.pem` (644 permissions)

## Development vs Production

**Development:**
Use the `.example.pem` keys for local development. These are committed to the repository.

**Production:**
Generate new keys using `generate_keys.sh` and store them securely (env variables, secrets manager, etc.).
