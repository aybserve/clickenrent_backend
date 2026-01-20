# Google OAuth Setup Guide

Complete guide to set up Google OAuth authentication for ClickEnRent Auth Service.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Google Cloud Console Setup](#google-cloud-console-setup)
- [Backend Configuration](#backend-configuration)
- [Frontend Integration](#frontend-integration)
- [Testing](#testing)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)

## Prerequisites

- Google Account (Gmail)
- Access to [Google Cloud Console](https://console.cloud.google.com/)
- Backend service running (auth-service on port 8081)
- Frontend application (e.g., React, Angular, Vue)

## Google Cloud Console Setup

### Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click **Select a project** → **NEW PROJECT**
3. Enter project details:
   - **Project name**: `ClickEnRent` (or your preferred name)
   - **Organization**: Leave default or select your organization
4. Click **CREATE**
5. Wait for the project to be created (usually takes 10-30 seconds)

### Step 2: Enable Required APIs

1. In the left sidebar, navigate to **APIs & Services** → **Library**
2. Search for and enable the following APIs:
   - **Google+ API** (for user profile information)
   - **People API** (optional, for extended user data)
3. Click **ENABLE** for each API

### Step 3: Configure OAuth Consent Screen

1. Navigate to **APIs & Services** → **OAuth consent screen**
2. Select **User Type**:
   - **External**: For public applications (recommended for most cases)
   - **Internal**: Only if you have a Google Workspace organization
3. Click **CREATE**

#### Fill in App Information

**OAuth consent screen** tab:

- **App name**: `ClickEnRent`
- **User support email**: Your email address
- **App logo**: Upload your app logo (optional, 120x120px)
- **Application home page**: `https://yourdomain.com` (or `http://localhost:3000` for dev)
- **Application privacy policy link**: `https://yourdomain.com/privacy`
- **Application terms of service link**: `https://yourdomain.com/terms`
- **Authorized domains**: 
  - `yourdomain.com` (production)
  - `localhost` (development)
- **Developer contact information**: Your email address

Click **SAVE AND CONTINUE**

**Scopes** tab:

1. Click **ADD OR REMOVE SCOPES**
2. Select the following scopes:
   - `../auth/userinfo.email` - See your email address
   - `../auth/userinfo.profile` - See your personal info
   - `openid` - Associate you with your personal info on Google
3. Click **UPDATE**
4. Click **SAVE AND CONTINUE**

**Test users** tab (for External apps in testing):

1. Click **ADD USERS**
2. Add email addresses of users who can test the app during development
3. Click **SAVE AND CONTINUE**

**Summary** tab:

- Review all information
- Click **BACK TO DASHBOARD**

### Step 4: Create OAuth 2.0 Credentials

1. Navigate to **APIs & Services** → **Credentials**
2. Click **CREATE CREDENTIALS** → **OAuth client ID**
3. Select **Application type**: **Web application**
4. Configure the OAuth client:

   **Name**: `ClickEnRent Web Client`
   
   **Authorized JavaScript origins**:
   - `http://localhost:3000` (development frontend)
   - `https://yourdomain.com` (production frontend)
   
   **Authorized redirect URIs**:
   - `http://localhost:3000/auth/google/callback` (development)
   - `https://yourdomain.com/auth/google/callback` (production)
   - `http://localhost:8080/login/oauth2/code/google` (Spring Boot default, optional)

5. Click **CREATE**

### Step 5: Copy Credentials

After creation, a dialog will appear with:

- **Your Client ID**: `1234567890-abc123def456.apps.googleusercontent.com`
- **Your Client Secret**: `GOCSPX-abc123def456...`

⚠️ **Important**: Copy both values immediately and store them securely!

Click **OK** to close the dialog.

## Backend Configuration

### Step 1: Update Environment Variables

Edit your `.env` file (or create from `.env.example`):

```bash
# Google OAuth Configuration
GOOGLE_CLIENT_ID=1234567890-abc123def456.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-abc123def456...
GOOGLE_VERIFY_ID_TOKEN=true
```

### Step 2: Verify Application Properties

The auth-service is already configured in `application.properties`:

```properties
oauth2.google.client-id=${GOOGLE_CLIENT_ID:your-google-client-id}
oauth2.google.client-secret=${GOOGLE_CLIENT_SECRET:your-google-client-secret}
oauth2.google.token-uri=https://oauth2.googleapis.com/token
oauth2.google.user-info-uri=https://www.googleapis.com/oauth2/v2/userinfo
oauth2.google.verify-id-token=${GOOGLE_VERIFY_ID_TOKEN:true}
```

No changes needed unless you want to customize URIs.

### Step 3: Start the Service

```bash
cd auth-service
mvn spring-boot:run
```

Verify the service is running:
- Auth Service: http://localhost:8081/actuator/health
- Google OAuth endpoint: http://localhost:8081/api/v1/auth/google/login

## Frontend Integration

### Option 1: Using Google Sign-In JavaScript Library

#### Install Google Sign-In

```html
<!-- Add to your index.html -->
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

#### React Example

```jsx
import { useEffect } from 'react';
import axios from 'axios';

function GoogleLoginButton() {
  useEffect(() => {
    // Initialize Google Sign-In
    window.google.accounts.id.initialize({
      client_id: 'YOUR_GOOGLE_CLIENT_ID',
      callback: handleCredentialResponse,
    });

    // Render the button
    window.google.accounts.id.renderButton(
      document.getElementById('googleSignInButton'),
      { theme: 'outline', size: 'large' }
    );
  }, []);

  const handleCredentialResponse = async (response) => {
    try {
      // Send the credential to your backend
      const result = await axios.post(
        'http://localhost:8081/api/v1/auth/google/login',
        {
          code: response.credential,
          redirectUri: window.location.origin + '/auth/google/callback',
        }
      );

      // Store JWT tokens
      localStorage.setItem('accessToken', result.data.accessToken);
      localStorage.setItem('refreshToken', result.data.refreshToken);

      // Redirect to dashboard
      window.location.href = '/dashboard';
    } catch (error) {
      console.error('Google login failed:', error);
      alert('Login failed. Please try again.');
    }
  };

  return <div id="googleSignInButton"></div>;
}

export default GoogleLoginButton;
```

### Option 2: Using OAuth 2.0 Authorization Code Flow

#### Step 1: Redirect to Google

```javascript
function redirectToGoogle() {
  const clientId = 'YOUR_GOOGLE_CLIENT_ID';
  const redirectUri = encodeURIComponent(
    window.location.origin + '/auth/google/callback'
  );
  const scope = encodeURIComponent('openid email profile');
  const responseType = 'code';
  const accessType = 'offline';
  const prompt = 'consent';

  const googleAuthUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}&response_type=${responseType}&access_type=${accessType}&prompt=${prompt}`;

  window.location.href = googleAuthUrl;
}
```

#### Step 2: Handle Callback

```javascript
// On /auth/google/callback page
useEffect(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');

  if (code) {
    authenticateWithBackend(code);
  }
}, []);

async function authenticateWithBackend(code) {
  try {
    const response = await axios.post(
      'http://localhost:8081/api/v1/auth/google/login',
      {
        code: code,
        redirectUri: window.location.origin + '/auth/google/callback',
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
   curl -X POST http://localhost:8081/api/v1/auth/google/login \
     -H "Content-Type: application/json" \
     -d '{
       "code": "4/0AY0e-g7...",
       "redirectUri": "http://localhost:3000/auth/google/callback"
     }'
   ```

3. **Test with Postman**:
   - Method: POST
   - URL: `http://localhost:8081/api/v1/auth/google/login`
   - Body (JSON):
     ```json
     {
       "code": "YOUR_AUTHORIZATION_CODE",
       "redirectUri": "http://localhost:3000/auth/google/callback"
     }
     ```

4. **Test with Swagger UI**:
   - Open: http://localhost:8081/swagger-ui.html
   - Find: "Google Authentication" section
   - Try the `/api/v1/auth/google/login` endpoint

### Expected Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "userName": "john.doe",
    "email": "john.doe@gmail.com",
    "firstName": "John",
    "lastName": "Doe",
    "imageUrl": "https://lh3.googleusercontent.com/a/...",
    "isEmailVerified": true,
    "isActive": true
  }
}
```

### Test Cases

- ✅ New user registration via Google
- ✅ Existing user login via Google
- ✅ Auto-linking Google account to existing verified email
- ✅ Rejecting auto-link to unverified email
- ✅ Invalid authorization code handling
- ✅ Network error retry mechanism
- ✅ ID token verification

## Production Deployment

### Pre-deployment Checklist

- [ ] Update **Authorized JavaScript origins** in Google Cloud Console with production domain
- [ ] Update **Authorized redirect URIs** with production callback URL
- [ ] Set strong `JWT_SECRET` environment variable
- [ ] Enable HTTPS for your backend and frontend
- [ ] Set `GOOGLE_VERIFY_ID_TOKEN=true` in production
- [ ] Configure proper CORS settings in gateway
- [ ] Set up monitoring and alerting for OAuth failures
- [ ] Review rate limiting configuration
- [ ] Test OAuth flow in staging environment

### Security Best Practices

1. **Always use HTTPS in production**
   - HTTP is not allowed for OAuth redirect URIs in production

2. **Validate redirect URIs**
   - Only whitelist trusted domains in Google Cloud Console

3. **Enable ID token verification**
   - Set `GOOGLE_VERIFY_ID_TOKEN=true`

4. **Rotate secrets regularly**
   - Change `GOOGLE_CLIENT_SECRET` periodically
   - Update `JWT_SECRET` during scheduled maintenance

5. **Monitor for suspicious activity**
   - Set up alerts for OAuth failures
   - Track metrics: login attempts, success rate, auto-linking events

6. **Implement rate limiting**
   - Already configured at Gateway level (20 req/sec for public auth endpoints)

## Troubleshooting

### Error: "Invalid client"

**Cause**: Incorrect `GOOGLE_CLIENT_ID` or `GOOGLE_CLIENT_SECRET`

**Solution**: 
- Verify credentials in Google Cloud Console
- Check `.env` file has correct values
- Restart the auth-service after updating

### Error: "Redirect URI mismatch"

**Cause**: The redirect URI in the request doesn't match what's configured in Google Cloud Console

**Solution**:
- Add the exact redirect URI to Google Cloud Console
- Ensure protocol (http/https), domain, and path match exactly
- Check for trailing slashes

### Error: "Access blocked: This app's request is invalid"

**Cause**: OAuth consent screen not configured or app in testing mode with unauthorized user

**Solution**:
- Complete OAuth consent screen configuration
- Add test users in Google Cloud Console (if app is in testing)
- Publish the app to production (removes test user restriction)

### Error: "Failed to authenticate with Google"

**Cause**: Network error, Google API downtime, or invalid authorization code

**Solution**:
- Check backend logs for detailed error message
- Verify authorization code hasn't expired (valid for 10 minutes)
- Check internet connectivity
- Review retry mechanism logs

### Error: "An account with this email already exists but is not verified"

**Cause**: Attempting to auto-link Google account to unverified email

**Solution**:
- User should verify their email first via email verification flow
- Or contact support to verify the account manually

### OAuth flow works but JWT generation fails

**Cause**: Missing or incorrect JWT configuration

**Solution**:
- Verify `JWT_SECRET` is set in environment
- Check `JwtService` bean is properly configured
- Review backend logs for stack traces

## Monitoring & Metrics

The auth-service exposes the following OAuth metrics via Actuator:

- `oauth.login.attempts{provider="google"}` - Total login attempts
- `oauth.login.success{provider="google"}` - Successful logins
- `oauth.login.failure{provider="google",reason="..."}` - Failed logins by reason
- `oauth.user.registration{provider="google"}` - New user registrations
- `oauth.user.autolinking{provider="google"}` - Auto-linking events
- `oauth.flow.duration{provider="google",outcome="success|failure"}` - Flow duration

Access metrics at: http://localhost:8081/actuator/prometheus

## Support & Resources

- **Google OAuth Documentation**: https://developers.google.com/identity/protocols/oauth2
- **Google Cloud Console**: https://console.cloud.google.com/
- **Auth Service Swagger**: http://localhost:8081/swagger-ui.html
- **Backend Logs**: `docker-compose logs -f auth-service`

## Appendix: Environment Variables Reference

```bash
# Google OAuth (required)
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret
GOOGLE_VERIFY_ID_TOKEN=true

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

After successfully setting up Google OAuth:

1. Test the integration thoroughly in development
2. Set up similar OAuth for other providers (Facebook, Apple, etc.) if needed
3. Configure email verification as a backup authentication method
4. Implement account linking UI for users with multiple auth methods
5. Set up monitoring and alerting for OAuth metrics
6. Document OAuth flow for your team

For questions or issues, refer to the main [README.md](../../README.md) or check the [SECURITY_ARCHITECTURE.md](../../SECURITY_ARCHITECTURE.md) for security details.
