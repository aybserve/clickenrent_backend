# Apple Sign In Setup Guide

Complete guide to set up Apple Sign In authentication for ClickEnRent Auth Service.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Apple Developer Account Setup](#apple-developer-account-setup)
- [Backend Configuration](#backend-configuration)
- [Frontend Integration](#frontend-integration)
- [Testing](#testing)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)

## Prerequisites

- Apple Developer Account (paid membership required - $99/year)
- Access to [Apple Developer Portal](https://developer.apple.com/)
- Backend service running (auth-service on port 8081)
- Frontend application (e.g., React, Angular, Vue)

## Apple Developer Account Setup

### Step 1: Register an App ID

1. Go to [Apple Developer Portal](https://developer.apple.com/account/)
2. Navigate to **Certificates, Identifiers & Profiles**
3. Select **Identifiers** from the left sidebar
4. Click the **+** button to create a new identifier
5. Select **App IDs** and click **Continue**
6. Select **App** as the type and click **Continue**
7. Fill in the App ID details:
   - **Description**: `ClickEnRent`
   - **Bundle ID**: `com.clickenrent.app` (or your domain)
   - **Explicit Bundle ID** is recommended
8. Scroll down to **Capabilities** section
9. Check **Sign in with Apple**
10. Click **Continue** and then **Register**

### Step 2: Create a Services ID (Client ID)

1. In **Identifiers**, click the **+** button again
2. Select **Services IDs** and click **Continue**
3. Fill in the Service ID details:
   - **Description**: `ClickEnRent Web Service`
   - **Identifier**: `com.clickenrent.service` (this will be your Client ID)
4. Click **Continue** and then **Register**
5. Click on the newly created Service ID to configure it
6. Check **Sign in with Apple**
7. Click **Configure** next to Sign in with Apple
8. Configure the following:
   
   **Primary App ID**: Select the App ID created in Step 1
   
   **Web Domain**: Add your domains (without protocol):
   - `localhost` (for development)
   - `yourdomain.com` (for production)
   
   **Return URLs**: Add callback URLs:
   - `http://localhost:3000/auth/apple/callback` (development)
   - `https://yourdomain.com/auth/apple/callback` (production)

9. Click **Save**, **Continue**, and **Save** again

### Step 3: Create a Private Key

1. In the left sidebar, select **Keys**
2. Click the **+** button to create a new key
3. Fill in the key details:
   - **Key Name**: `ClickEnRent Apple Sign In Key`
4. Check **Sign in with Apple**
5. Click **Configure** next to Sign in with Apple
6. Select the **Primary App ID** from Step 1
7. Click **Save**
8. Click **Continue** and then **Register**
9. **Download the key file** (`.p8` file)
   
   ⚠️ **IMPORTANT**: 
   - This is the ONLY time you can download this key
   - Store it securely - you cannot download it again
   - The key file name format: `AuthKey_XXXXXXXXXX.p8`
   - Note the **Key ID** (10 characters) shown on this page

10. Click **Done**

### Step 4: Get Your Team ID

1. In the Apple Developer Portal, click on your name in the top right
2. Go to **Membership**
3. Note your **Team ID** (10 characters, alphanumeric)

## Backend Configuration

### Step 1: Prepare the Private Key

Open the `.p8` file you downloaded and copy its contents. The format should be:

```
-----BEGIN PRIVATE KEY-----
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQg...
... (multiple lines of base64 encoded key) ...
-----END PRIVATE KEY-----
```

### Step 2: Update Environment Variables

Create or edit your `.env` file:

```bash
# Apple OAuth Configuration
APPLE_TEAM_ID=ABC1234567
APPLE_CLIENT_ID=com.clickenrent.service
APPLE_KEY_ID=DEF8901234
APPLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQg...
-----END PRIVATE KEY-----"
APPLE_VERIFY_ID_TOKEN=true
```

**Important Notes**:
- `APPLE_TEAM_ID`: Your Team ID from Step 4
- `APPLE_CLIENT_ID`: Your Service ID from Step 2
- `APPLE_KEY_ID`: The Key ID from Step 3
- `APPLE_PRIVATE_KEY`: The full contents of the .p8 file (including BEGIN/END lines)
- For multi-line environment variables, use quotes and `\n` for newlines, or use the format shown above

Alternative format (single line with escaped newlines):

```bash
APPLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nMIGTAgEAMBMGByqGSM49...\n-----END PRIVATE KEY-----"
```

### Step 3: Verify Application Properties

The auth-service is already configured in `application.properties`:

```properties
oauth2.apple.team-id=${APPLE_TEAM_ID:your-apple-team-id}
oauth2.apple.client-id=${APPLE_CLIENT_ID:your-apple-service-id}
oauth2.apple.key-id=${APPLE_KEY_ID:your-apple-key-id}
oauth2.apple.private-key=${APPLE_PRIVATE_KEY:-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY_CONTENT\n-----END PRIVATE KEY-----}
oauth2.apple.token-uri=https://appleid.apple.com/auth/token
oauth2.apple.jwks-uri=https://appleid.apple.com/auth/keys
oauth2.apple.verify-id-token=${APPLE_VERIFY_ID_TOKEN:true}
```

No changes needed unless you want to customize URIs.

### Step 4: Start the Service

```bash
cd auth-service
mvn spring-boot:run
```

Verify the service is running:
- Auth Service: http://localhost:8081/actuator/health
- Apple OAuth endpoint: http://localhost:8081/api/v1/auth/apple/login

## Frontend Integration

### Option 1: Using Apple's JavaScript SDK

#### Include Apple's JS SDK

```html
<!-- Add to your index.html -->
<script type="text/javascript" src="https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js"></script>
```

#### React Example

```jsx
import { useEffect } from 'react';
import axios from 'axios';

function AppleLoginButton() {
  useEffect(() => {
    // Initialize Apple Sign In
    if (window.AppleID) {
      window.AppleID.auth.init({
        clientId: 'com.clickenrent.service', // Your Service ID
        scope: 'name email',
        redirectURI: window.location.origin + '/auth/apple/callback',
        state: 'origin:web',
        usePopup: true // or false to use redirect
      });
    }
  }, []);

  const handleAppleSignIn = async () => {
    try {
      const data = await window.AppleID.auth.signIn();
      
      // Send the authorization code to your backend
      const response = await axios.post(
        'http://localhost:8081/api/v1/auth/apple/login',
        {
          code: data.authorization.code,
          redirectUri: window.location.origin + '/auth/apple/callback'
        }
      );

      // Store JWT tokens
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);

      // Redirect to dashboard
      window.location.href = '/dashboard';
    } catch (error) {
      console.error('Apple sign in failed:', error);
      alert('Login failed. Please try again.');
    }
  };

  return (
    <div
      id="appleid-signin"
      data-color="black"
      data-border="true"
      data-type="sign in"
      onClick={handleAppleSignIn}
      style={{ cursor: 'pointer' }}
    />
  );
}

export default AppleLoginButton;
```

### Option 2: Using Manual OAuth 2.0 Flow

#### Step 1: Redirect to Apple

```javascript
function redirectToApple() {
  const clientId = 'com.clickenrent.service';
  const redirectUri = encodeURIComponent(
    window.location.origin + '/auth/apple/callback'
  );
  const scope = encodeURIComponent('name email');
  const responseType = 'code';
  const responseMode = 'form_post'; // or 'query'
  const state = 'some-random-state'; // CSRF protection

  const appleAuthUrl = `https://appleid.apple.com/auth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&response_type=${responseType}&response_mode=${responseMode}&state=${state}`;

  window.location.href = appleAuthUrl;
}
```

#### Step 2: Handle Callback

```javascript
// On /auth/apple/callback page
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

function AppleCallback() {
  const location = useLocation();

  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');

    if (code) {
      authenticateWithBackend(code);
    }
  }, [location]);

  async function authenticateWithBackend(code) {
    try {
      const response = await axios.post(
        'http://localhost:8081/api/v1/auth/apple/login',
        {
          code: code,
          redirectUri: window.location.origin + '/auth/apple/callback'
        }
      );

      // Store tokens
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);

      // Redirect to dashboard
      window.location.href = '/dashboard';
    } catch (error) {
      console.error('Authentication failed:', error);
    }
  }

  return <div>Processing Apple Sign In...</div>;
}

export default AppleCallback;
```

## Testing

### Manual Testing

1. **Start the backend**:
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```

2. **Test with cURL** (if you have an authorization code):
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/apple/login \
     -H "Content-Type: application/json" \
     -d '{
       "code": "c1a2b3c4d5...",
       "redirectUri": "http://localhost:3000/auth/apple/callback"
     }'
   ```

3. **Test with Postman**:
   - Method: POST
   - URL: `http://localhost:8081/api/v1/auth/apple/login`
   - Body (JSON):
     ```json
     {
       "code": "YOUR_AUTHORIZATION_CODE",
       "redirectUri": "http://localhost:3000/auth/apple/callback"
     }
     ```

4. **Test with Swagger UI**:
   - Open: http://localhost:8081/swagger-ui.html
   - Find: "Apple Authentication" section
   - Try the `/api/v1/auth/apple/login` endpoint

### Expected Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "userName": "john.doe",
    "email": "john.doe@privaterelay.appleid.com",
    "firstName": "John",
    "lastName": "Doe",
    "imageUrl": null,
    "isEmailVerified": true,
    "isActive": true
  }
}
```

### Test Cases

- ✅ New user registration via Apple
- ✅ Existing user login via Apple
- ✅ Auto-linking Apple account to existing verified email
- ✅ Rejecting auto-link to unverified email
- ✅ Handling Apple private relay emails
- ✅ Client secret generation and caching
- ✅ ID token verification (signature, issuer, audience)
- ✅ Invalid authorization code handling
- ✅ Network error retry mechanism

## Production Deployment

### Pre-deployment Checklist

- [ ] Update **Web Domains** in Apple Developer Portal with production domain
- [ ] Update **Return URLs** with production callback URL
- [ ] Set strong `JWT_SECRET` environment variable
- [ ] Enable HTTPS for your backend and frontend
- [ ] Set `APPLE_VERIFY_ID_TOKEN=true` in production
- [ ] Store `APPLE_PRIVATE_KEY` securely (use secrets manager, not in code)
- [ ] Configure proper CORS settings in gateway
- [ ] Set up monitoring and alerting for OAuth failures
- [ ] Review rate limiting configuration
- [ ] Test OAuth flow in staging environment
- [ ] Document key rotation schedule (every 6 months recommended)

### Security Best Practices

1. **Always use HTTPS in production**
   - HTTP is not allowed for OAuth redirect URIs in production

2. **Protect your private key**
   - Never commit the .p8 file or private key to git
   - Use environment variables or secrets manager (AWS Secrets Manager, Azure Key Vault, etc.)
   - Rotate keys periodically (create new key, update config, delete old key)

3. **Enable ID token verification**
   - Set `APPLE_VERIFY_ID_TOKEN=true` in production

4. **Validate redirect URIs**
   - Only whitelist trusted domains in Apple Developer Portal

5. **Monitor for suspicious activity**
   - Set up alerts for OAuth failures
   - Track metrics: login attempts, success rate, auto-linking events

6. **Implement rate limiting**
   - Already configured at Gateway level (applies to Apple endpoints)

7. **Handle private relay emails appropriately**
   - Users may provide Apple's private relay emails (@privaterelay.appleid.com)
   - Store and use these emails normally
   - Be aware that emails may change if user disables email forwarding

8. **Client secret rotation**
   - Apple client secrets (JWT) are valid for up to 6 months
   - The system caches generated secrets for 5 months
   - Consider rotating your signing key annually

## Troubleshooting

### Error: "invalid_client"

**Cause**: Incorrect Team ID, Client ID, or Key ID

**Solution**: 
- Verify `APPLE_TEAM_ID` matches your Team ID in Apple Developer Portal
- Verify `APPLE_CLIENT_ID` matches your Service ID
- Verify `APPLE_KEY_ID` matches the Key ID from the downloaded key
- Restart the auth-service after updating environment variables

### Error: "invalid_grant"

**Cause**: Authorization code has expired or already been used

**Solution**:
- Authorization codes are single-use only
- Codes expire after 5 minutes
- Generate a new authorization code and try again

### Error: "Failed to parse PEM object from private key"

**Cause**: Private key format is incorrect

**Solution**:
- Ensure the private key includes the BEGIN and END lines
- Check for proper newlines (use `\n` in environment variables)
- Verify no extra spaces or characters in the key
- Example correct format:
  ```
  -----BEGIN PRIVATE KEY-----
  MIGTAgEAMBMGByqGSM49...
  -----END PRIVATE KEY-----
  ```

### Error: "ID token verification failed"

**Cause**: Token signature invalid or claims mismatch

**Solution**:
- Check that `APPLE_CLIENT_ID` matches the audience in the ID token
- Ensure system clock is synchronized (token expiration validation)
- Verify Apple's public keys are accessible (JWKS endpoint)
- Check backend logs for detailed error message

### Error: "Invalid redirect URI"

**Cause**: Redirect URI doesn't match what's configured in Apple Developer Portal

**Solution**:
- Ensure the redirect URI in your frontend exactly matches what's configured in Apple
- Check protocol (http vs https), domain, port, and path
- Add the exact URI to Apple Developer Portal if missing

### Error: "User cancelled the authorization"

**Cause**: User clicked "Cancel" on Apple's sign-in page

**Solution**:
- This is normal user behavior
- Handle gracefully in your frontend
- Show a friendly message and option to try again

### OAuth flow works but JWT generation fails

**Cause**: Missing or incorrect JWT configuration

**Solution**:
- Verify `JWT_SECRET` is set in environment
- Check `JwtService` bean is properly configured
- Review backend logs for stack traces

## Monitoring & Metrics

The auth-service exposes the following OAuth metrics via Actuator:

- `oauth.login.attempts{provider="apple"}` - Total login attempts
- `oauth.login.success{provider="apple"}` - Successful logins
- `oauth.login.failure{provider="apple",reason="..."}` - Failed logins by reason
- `oauth.user.registration{provider="apple"}` - New user registrations
- `oauth.user.autolinking{provider="apple"}` - Auto-linking events
- `oauth.flow.duration{provider="apple",outcome="success|failure"}` - Flow duration

Access metrics at: http://localhost:8081/actuator/prometheus

## Private Relay Email Considerations

Apple offers "Hide My Email" feature, which provides users with a private relay email address that forwards to their real email.

**Format**: `random-string@privaterelay.appleid.com`

**Handling**:
- Store and use these emails normally in your system
- User can disable forwarding at any time (emails will bounce)
- Cannot determine the real email address behind a relay
- Consider this when implementing email-dependent features
- Track usage with the service's logging (already implemented)

## Support & Resources

- **Apple Documentation**: https://developer.apple.com/sign-in-with-apple/
- **Apple REST API Reference**: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api
- **Auth Service Swagger**: http://localhost:8081/swagger-ui.html
- **Backend Logs**: `docker-compose logs -f auth-service`

## Appendix: Environment Variables Reference

```bash
# Apple OAuth (required)
APPLE_TEAM_ID=your-10-character-team-id
APPLE_CLIENT_ID=com.yourcompany.service
APPLE_KEY_ID=your-10-character-key-id
APPLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
...your-private-key-content...
-----END PRIVATE KEY-----"
APPLE_VERIFY_ID_TOKEN=true

# JWT (required)
JWT_SECRET=your-secure-256-bit-secret
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Database (required)
DB_URL=jdbc:postgresql://localhost:5432/clickenrent-auth
DB_USERNAME=postgres
DB_PASSWORD=your-password
```

## Next Steps

After successfully setting up Apple Sign In:

1. Test the integration thoroughly in development
2. Set up similar OAuth for other providers if needed
3. Configure email verification as a backup authentication method
4. Implement account linking UI for users with multiple auth methods
5. Set up monitoring and alerting for OAuth metrics
6. Document OAuth flow for your team
7. Plan for key rotation (every 6-12 months)

For questions or issues, refer to the main [README.md](../../README.md) or check the [SECURITY_ARCHITECTURE.md](../../SECURITY_ARCHITECTURE.md) for security details.
