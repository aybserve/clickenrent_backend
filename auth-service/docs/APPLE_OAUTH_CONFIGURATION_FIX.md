# Apple OAuth Configuration Fix

## Issues Encountered

### Issue 1: Invalid Base64 in Placeholder Key
The application failed to start with the error:
```
unable to decode base64 string: invalid characters encountered in base64 data
```

This occurred because the default placeholder value for the Apple private key in `application.properties` was being parsed on startup, even though it wasn't a valid key.

### Issue 2: Missing Bean Dependencies
After fixing Issue 1, the application failed with:
```
No qualifying bean of type 'java.security.interfaces.ECPrivateKey' available
```

This occurred because when the `applePrivateKey()` bean returns `null` (for placeholder values), Spring doesn't register it as a bean, but other beans (`appleJwtSigner`) were trying to autowire it.

## Root Causes

1. **Invalid Placeholder Parsing**: The `AppleOAuthConfig` bean was attempting to parse the private key from the placeholder value, causing PEM parser to fail.

2. **Required Bean Dependencies**: Spring's dependency injection requires beans to exist, even if they're null. When `applePrivateKey()` returned `null`, subsequent beans that depended on it failed to initialize.

## Solution
Modified the configuration to gracefully handle missing or placeholder credentials using Spring's `ObjectProvider`:

### Changes Made

1. **AppleOAuthConfig.applePrivateKey()** - Added placeholder detection:
   - Returns `null` if the key contains placeholder text
   - Logs a warning that Apple OAuth won't work until properly configured
   - Prevents application startup failure due to invalid PEM parsing

2. **AppleOAuthConfig.appleJwtSigner()** - Made dependency optional:
   - Uses `ObjectProvider<ECPrivateKey>` to accept optional dependency
   - Returns `null` if private key is not configured
   - Logs warning about missing configuration

3. **AppleOAuthConfig.appleJwtProcessor()** - Made JWKS fetching fail-safe:
   - Returns `null` if JWKS cannot be fetched (network issues, etc.)
   - Logs warning instead of throwing exception
   - Allows application to start even if Apple's JWKS endpoint is unreachable

4. **AppleOAuthService constructor** - Uses optional providers:
   - Uses `ObjectProvider<JWSSigner>` for optional JWT signer
   - Uses `ObjectProvider<ConfigurableJWTProcessor>` for optional processor
   - Gracefully handles null beans

5. **AppleOAuthService.authenticateWithApple()** - Added early validation:
   - Checks if JWT signer is configured before processing request
   - Returns clear error message: "Apple Sign In is not configured on this server"

6. **AppleOAuthService.extractUserInfoFromIdToken()** - Added processor validation:
   - Checks if JWT processor is available before verification
   - Returns meaningful error if processor is not configured

7. **AppleOAuthConfig.verifyIdToken()** - Added null check:
   - Validates processor is not null before attempting verification
   - Throws clear error message if processor is missing

8. **Tests updated** - Fixed constructor calls:
   - Updated `AppleOAuthServiceTest` to use `ObjectProvider` mocks
   - Ensures tests match production code structure

## Result
- ✅ Application starts successfully even without Apple credentials configured
- ✅ Clear warning messages indicate Apple OAuth needs configuration
- ✅ Meaningful error messages when Apple login is attempted without configuration
- ✅ No impact on other OAuth providers (Google) or authentication methods

## Configuration for Production

To enable Apple OAuth, set these environment variables with real values:

```bash
APPLE_TEAM_ID=your-10-character-team-id
APPLE_CLIENT_ID=com.yourcompany.service
APPLE_KEY_ID=your-10-character-key-id
APPLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQg...
-----END PRIVATE KEY-----"
```

See [APPLE_OAUTH_SETUP.md](./APPLE_OAUTH_SETUP.md) for detailed setup instructions.

## Testing

The application will start in these scenarios:
1. ✅ No environment variables set (uses placeholders)
2. ✅ Partial environment variables set
3. ✅ All environment variables properly configured
4. ❌ Invalid key format (will fail at startup with clear error)

When Apple OAuth is not configured, attempts to use `/api/v1/auth/apple/login` will return:
- HTTP 401 Unauthorized
- Message: "Apple Sign In is not configured on this server"
