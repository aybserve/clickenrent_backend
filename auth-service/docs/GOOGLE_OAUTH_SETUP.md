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

## Frontend Integration (Web)

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

## Mobile Integration (React Native / Expo)

The auth-service supports mobile Google Sign-In by accepting ID tokens directly. This eliminates the need for authorization code exchange and simplifies the mobile authentication flow.

### Flow Comparison

**Web Flow:**
```
Mobile App → Google OAuth → Authorization Code → Backend → Token Exchange → User Info
```

**Mobile Flow (Recommended):**
```
Mobile App → Google Sign-In SDK → ID Token → Backend → Verify Token → User Info
```

### Benefits of Mobile Flow

- ✅ **Simpler** - No redirect URIs or authorization code handling
- ✅ **Faster** - One less API call (no token exchange needed)
- ✅ **More Secure** - ID token verified directly by backend
- ✅ **Better UX** - Native Google Sign-In experience

### Prerequisites

1. **Google Cloud Console Setup** - Complete the setup from above
2. **Additional Client IDs** - You'll need client IDs for each platform:
   - Web Client ID (already created)
   - Android Client ID (auto-created by Firebase)
   - iOS Client ID (auto-created by Firebase)

### Step 1: Firebase Configuration (Recommended for React Native)

If using React Native with Expo, Firebase makes Google Sign-In easier:

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Link your Google Cloud project
4. Add Android and/or iOS apps to Firebase project
5. Download configuration files:
   - `google-services.json` (Android)
   - `GoogleService-Info.plist` (iOS)

### Step 2: Install Dependencies

#### For React Native (Expo)

```bash
npx expo install @react-native-google-signin/google-signin
```

#### For React Native (without Expo)

```bash
npm install @react-native-google-signin/google-signin
```

### Step 3: Configure Google Sign-In

#### React Native with Expo

```javascript
// app.json or app.config.js
{
  "expo": {
    "plugins": [
      [
        "@react-native-google-signin/google-signin",
        {
          "iosUrlScheme": "com.googleusercontent.apps.YOUR_IOS_CLIENT_ID"
        }
      ]
    ],
    "android": {
      "googleServicesFile": "./google-services.json"
    },
    "ios": {
      "googleServicesFile": "./GoogleService-Info.plist"
    }
  }
}
```

#### Initialize in App

```javascript
import { GoogleSignin } from '@react-native-google-signin/google-signin';

// Configure Google Sign-In
GoogleSignin.configure({
  webClientId: 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com', // From Google Cloud Console
  offlineAccess: false, // We don't need offline access
  forceCodeForRefreshToken: false,
});
```

### Step 4: Implement Sign-In

```javascript
import { GoogleSignin } from '@react-native-google-signin/google-signin';
import axios from 'axios';

const API_URL = 'https://your-api.com/api/v1/auth/google/login';

export const signInWithGoogle = async () => {
  try {
    // Check if device supports Google Play Services (Android)
    await GoogleSignin.hasPlayServices();
    
    // Sign in with Google
    const userInfo = await GoogleSignin.signIn();
    
    // Get ID token
    const idToken = userInfo.idToken;
    
    if (!idToken) {
      throw new Error('No ID token received from Google');
    }
    
    // Send ID token to your backend
    const response = await axios.post(API_URL, {
      idToken: idToken
    });
    
    // Store JWT tokens
    const { accessToken, refreshToken, user } = response.data;
    await AsyncStorage.setItem('accessToken', accessToken);
    await AsyncStorage.setItem('refreshToken', refreshToken);
    
    console.log('Logged in as:', user.email);
    return response.data;
    
  } catch (error) {
    if (error.code === statusCodes.SIGN_IN_CANCELLED) {
      console.log('User cancelled the login');
    } else if (error.code === statusCodes.IN_PROGRESS) {
      console.log('Sign in is in progress');
    } else if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
      console.log('Google Play Services not available');
    } else {
      console.error('Google Sign-In Error:', error);
    }
    throw error;
  }
};

// Sign out
export const signOutGoogle = async () => {
  try {
    await GoogleSignin.signOut();
    await AsyncStorage.removeItem('accessToken');
    await AsyncStorage.removeItem('refreshToken');
  } catch (error) {
    console.error('Sign out error:', error);
  }
};

// Check if user is signed in
export const isSignedIn = async () => {
  const isSignedIn = await GoogleSignin.isSignedIn();
  return isSignedIn;
};
```

### Step 5: UI Component Example

```javascript
import React, { useState } from 'react';
import { View, TouchableOpacity, Text, ActivityIndicator, Alert } from 'react-native';
import { GoogleSignin } from '@react-native-google-signin/google-signin';
import { signInWithGoogle } from './auth-service';

const GoogleSignInButton = ({ onSuccess }) => {
  const [loading, setLoading] = useState(false);
  
  const handleGoogleSignIn = async () => {
    setLoading(true);
    try {
      const result = await signInWithGoogle();
      onSuccess(result);
    } catch (error) {
      Alert.alert('Sign In Failed', error.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <TouchableOpacity
      onPress={handleGoogleSignIn}
      disabled={loading}
      style={styles.googleButton}
    >
      {loading ? (
        <ActivityIndicator color="#fff" />
      ) : (
        <>
          <GoogleIcon />
          <Text style={styles.buttonText}>Sign in with Google</Text>
        </>
      )}
    </TouchableOpacity>
  );
};

const styles = {
  googleButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#4285F4',
    padding: 12,
    borderRadius: 8,
    minHeight: 50,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
    marginLeft: 12,
  },
};

export default GoogleSignInButton;
```

### API Request Format (Mobile Flow)

**Endpoint:** `POST /api/v1/auth/google/login`

**Request Body:**
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (Success - 200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
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

**Response (Error - 401 Unauthorized):**
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid Google ID token",
  "path": "/api/v1/auth/google/login"
}
```

### Testing Mobile Flow

#### 1. Test with cURL

```bash
# Get a real ID token from Google Sign-In in your mobile app
# Then test with:
curl -X POST https://your-api.com/api/v1/auth/google/login \
  -H "Content-Type: application/json" \
  -d '{
    "idToken": "YOUR_ACTUAL_ID_TOKEN_FROM_MOBILE_APP"
  }'
```

#### 2. Test with Postman

1. Sign in to your mobile app (development build)
2. Add logging to capture the ID token
3. Copy the ID token
4. Send POST request to `/api/v1/auth/google/login`:
   ```json
   {
     "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

### Security Considerations (Mobile)

1. **ID Token Verification**
   - Backend automatically verifies token signature using Google's public keys
   - Checks token audience matches your client ID
   - Validates token issuer is Google
   - Ensures email is verified by Google
   - Confirms token hasn't expired

2. **Client ID Configuration**
   - Use the **Web Client ID** in your mobile app configuration
   - This is the same client ID used for web authentication
   - Backend verifies ID tokens against this client ID

3. **Package Name / Bundle ID**
   - Android: Add your package name (e.g., `com.yourapp`) in Google Cloud Console
   - iOS: Add your bundle ID (e.g., `com.yourapp.ios`) in Google Cloud Console
   - Required for mobile app verification

4. **SHA-1 Certificate (Android)**
   - Add your app's SHA-1 fingerprint in Firebase/Google Cloud Console
   - Development: Use debug keystore SHA-1
   - Production: Use release keystore SHA-1
   - Get SHA-1: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey`

### Troubleshooting Mobile Flow

#### Error: "No ID token received from Google"

**Cause:** Google Sign-In didn't return an ID token

**Solution:**
- Ensure you're using the correct `webClientId` in configuration
- Verify the web client ID matches the one in Google Cloud Console
- Check that OAuth consent screen is properly configured

#### Error: "Invalid Google ID token"

**Cause:** ID token failed verification on backend

**Solution:**
- Verify backend has correct `GOOGLE_CLIENT_ID` configured
- Ensure ID token hasn't expired (valid for 1 hour)
- Check that `oauth2.google.verify-id-token=true` in backend config

#### Error: "DEVELOPER_ERROR" on Android

**Cause:** SHA-1 certificate fingerprint mismatch

**Solution:**
- Add correct SHA-1 to Firebase Console
- Debug build uses debug keystore SHA-1
- Release build uses release keystore SHA-1
- Run: `./gradlew signingReport` to see all SHA-1 fingerprints

#### Error: "SIGN_IN_REQUIRED" on iOS

**Cause:** URL scheme not configured correctly

**Solution:**
- Verify `iosUrlScheme` in app.json matches your reversed client ID
- Format: `com.googleusercontent.apps.YOUR_IOS_CLIENT_ID`
- Rebuild app after configuration changes

#### Error: "Play Services not available" on Android

**Cause:** Google Play Services not installed or outdated

**Solution:**
- Test on device with Google Play Services
- Emulator must have Google APIs
- Update Google Play Services on test device

### Web vs Mobile Flow Support

The `/api/v1/auth/google/login` endpoint supports **both flows automatically**:

**Mobile Flow (ID Token):**
```json
{
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Web Flow (Authorization Code):**
```json
{
  "code": "4/0AY0e-g7...",
  "redirectUri": "http://localhost:3000/auth/google/callback"
}
```

The backend automatically detects which flow to use based on the request body. If `idToken` is present, it uses the mobile flow. Otherwise, it uses the web flow.

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

**Web Flow:**
- ✅ New user registration via Google
- ✅ Existing user login via Google
- ✅ Auto-linking Google account to existing verified email
- ✅ Rejecting auto-link to unverified email
- ✅ Invalid authorization code handling
- ✅ Network error retry mechanism
- ✅ ID token verification

**Mobile Flow:**
- ✅ New user registration via ID token
- ✅ Existing user login via ID token
- ✅ Auto-linking via ID token
- ✅ Invalid ID token handling
- ✅ Expired ID token handling
- ✅ Unverified email rejection
- ✅ Audience mismatch detection
- ✅ Issuer validation

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

**General Metrics (all flows):**
- `oauth.user.registration{provider="google"}` - New user registrations
- `oauth.user.autolinking{provider="google"}` - Auto-linking events

**Flow-Specific Metrics:**
- `oauth.login.attempts{provider="google",flow="web|mobile"}` - Login attempts by flow
- `oauth.login.success{provider="google",flow="web|mobile"}` - Successful logins by flow
- `oauth.login.failure{provider="google",flow="web|mobile",reason="..."}` - Failed logins by flow and reason
- `oauth.flow.duration{provider="google",flow="web|mobile",outcome="success|failure"}` - Flow duration by type

**Example Queries:**
- Total mobile login attempts: `oauth.login.attempts{provider="google",flow="mobile"}`
- Web flow success rate: `oauth.login.success{provider="google",flow="web"} / oauth.login.attempts{provider="google",flow="web"}`
- Mobile vs Web comparison: Compare metrics with `flow="mobile"` vs `flow="web"`

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
