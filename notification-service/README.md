# Notification Service

Push notification service for Click & Rent platform using Expo Push Notifications with official Expo Server SDK.

## Overview

The notification-service handles push notifications to mobile devices via Expo Push Notification Service. It manages:
- Push token registration and management with device tracking
- Notification delivery via Expo Server SDK
- Granular user notification preferences
- Notification history with read tracking
- Error handling with automatic token deactivation
- Multi-device support with device ID tracking

## Architecture

```
Mobile App (Expo) → API Gateway → notification-service → Expo Push API (via SDK)
rental-service ────────────────→ notification-service (via Feign)
```

## Features

### Core Features
- **Push Token Management**: Register and manage Expo Push Tokens with device tracking
- **Device Identification**: Track devices by unique device ID to prevent duplicates
- **Notification Delivery**: Send push notifications via official Expo Server SDK
- **Read Tracking**: Track which notifications users have read
- **Granular Preferences**: Fine-grained control over notification types
- **Notification History**: Complete audit trail with unread counts
- **Error Handling**: Automatic token deactivation for invalid tokens
- **Multi-device Support**: Users can have multiple devices registered
- **Batch Processing**: Efficient batch sending with automatic chunking

### New in v2.0
- ✨ **Official Expo Server SDK**: Migrated from WebClient to official SDK
- ✨ **Device Tracking**: Track app version, device model, OS version
- ✨ **Read Tracking**: Mark notifications as read, track unread count
- ✨ **Granular Preferences**: Separate controls for rental start, end reminders, completion
- ✨ **Enhanced History**: Includes unread count and delivery status
- ✨ **Improved Token Management**: Device ID-based token updates

## Database Schema

### Tables

1. **push_tokens**: Stores Expo Push Tokens per user/device
   - New fields: `device_id`, `app_version`, `device_model`, `os_version`
   
2. **notification_logs**: Audit trail of all notifications sent
   - New fields: `is_read`, `read_at`, `delivery_status`
   
3. **notification_preferences**: User preferences for notification types
   - New fields: `rental_start_enabled`, `rental_end_reminders_enabled`, `rental_completion_enabled`

See `notification-service.sql` for initial schema and `migration-v2.sql` for upgrade script.

## API Endpoints

### Public Endpoints (Require JWT)

#### Token Management

**POST `/api/notifications/register-token`** - Register push token from mobile app

Request:
```json
{
  "expoPushToken": "ExponentPushToken[xxx]",
  "platform": "ios",
  "deviceId": "device-uuid",
  "appVersion": "1.0.0",
  "deviceName": "iPhone 14 Pro",
  "deviceModel": "iPhone 15 Pro",
  "osVersion": "iOS 17.2"
}
```

Response:
```json
{
  "success": true,
  "notificationId": "123",
  "deviceId": "device-uuid",
  "registeredAt": "2024-12-27T10:00:00Z"
}
```

**DELETE `/api/notifications/tokens/{token}`** - Delete a push token

#### Notification History

**GET `/api/notifications/history`** - Get notification history with pagination

Query params: `page`, `size`, `sort`

Response:
```json
{
  "notifications": [
    {
      "id": "123",
      "type": "BIKE_UNLOCKED",
      "title": "Bike Unlocked 🚴",
      "body": "Your bike has been unlocked. Have a great ride!",
      "data": {"bikeRentalId": "456"},
      "sentAt": "2024-12-27T10:00:00Z",
      "readAt": null,
      "isRead": false,
      "deliveryStatus": "sent"
    }
  ],
  "total": 15,
  "unreadCount": 3,
  "page": 0,
  "size": 20,
  "totalPages": 1
}
```

**POST `/api/notifications/{id}/read`** - Mark notification as read

**POST `/api/notifications/read-all`** - Mark all notifications as read

#### Preferences

**GET `/api/notifications/preferences`** - Get notification preferences

**PUT `/api/notifications/preferences`** - Update notification preferences

Request:
```json
{
  "rentalUpdatesEnabled": true,
  "rentalStartEnabled": true,
  "rentalEndRemindersEnabled": true,
  "rentalCompletionEnabled": true,
  "paymentUpdatesEnabled": true,
  "supportMessagesEnabled": true,
  "marketingEnabled": false
}
```

### Internal Endpoints (For Microservices)

**POST `/api/notifications/internal/send`** - Send a notification (called by other services)

Request:
```json
{
  "userExternalId": "user-123",
  "notificationType": "BIKE_UNLOCKED",
  "title": "Bike Unlocked 🚴",
  "body": "Your bike has been unlocked. Have a great ride!",
  "data": {"bikeRentalId": "456"},
  "priority": "high"
}
```

## Configuration

### application.properties

```properties
spring.application.name=notification-service
server.port=8084

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/notification_db
spring.datasource.username=postgres
spring.datasource.password=password

# JWT Secret (must match auth-service)
jwt.secret=${JWT_SECRET}
```

### Environment Variables

- `JWT_SECRET`: JWT secret key (must match auth-service)

## Setup Instructions

### 1. Create Database

```bash
psql -U postgres
CREATE DATABASE notification_db;
\c notification_db
\i notification-service.sql
```

### 2. Upgrade Existing Database (v1 → v2)

If you already have the notification-service running, apply the migration:

```bash
psql -U postgres -d notification_db
\i migration-v2.sql
```

### 3. Build and Run

```bash
# From project root
mvn clean install

# Run notification-service
cd notification-service
mvn spring-boot:run
```

### 4. Verify Service

- Service: http://localhost:8084
- Swagger UI: http://localhost:8084/swagger-ui.html
- Health: http://localhost:8084/actuator/health

## Mobile App Integration

### 1. Install Expo Notifications

```bash
npx expo install expo-notifications expo-device expo-constants
```

### 2. Request Permissions and Get Token

```javascript
import * as Notifications from 'expo-notifications';
import * as Device from 'expo-device';
import Constants from 'expo-constants';

async function registerForPushNotifications() {
  if (Device.isDevice) {
    const { status } = await Notifications.requestPermissionsAsync();
    if (status !== 'granted') {
      alert('Permission not granted!');
      return;
    }
    
    const token = await Notifications.getExpoPushTokenAsync({
      projectId: Constants.expoConfig.extra.eas.projectId
    });
    
    return {
      token: token.data,
      deviceId: Constants.deviceId,
      platform: Platform.OS,
      appVersion: Constants.expoConfig.version,
      deviceModel: Device.modelName,
      osVersion: Device.osVersion
    };
  }
}
```

### 3. Register Token with Backend

```javascript
const deviceInfo = await registerForPushNotifications();

const response = await fetch('https://api.yourapp.com/api/notifications/register-token', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${jwtToken}`,
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    expoPushToken: deviceInfo.token,
    platform: deviceInfo.platform,
    deviceId: deviceInfo.deviceId,
    appVersion: deviceInfo.appVersion,
    deviceModel: deviceInfo.deviceModel,
    osVersion: deviceInfo.osVersion
  }),
});

const result = await response.json();
console.log('Registered:', result.notificationId);
```

### 4. Handle Notifications

```javascript
// Handle foreground notifications
Notifications.addNotificationReceivedListener(notification => {
  console.log('Notification received:', notification);
  // Optionally mark as read
  markNotificationAsRead(notification.request.content.data.notificationId);
});

// Handle notification taps
Notifications.addNotificationResponseReceivedListener(response => {
  const data = response.notification.request.content.data;
  const notificationId = data.notificationId;
  
  // Mark as read
  markNotificationAsRead(notificationId);
  
  // Navigate based on notification type
  if (data.notificationType === 'BIKE_UNLOCKED') {
    navigation.navigate('RideScreen', { bikeRentalId: data.bikeRentalId });
  }
});

async function markNotificationAsRead(notificationId) {
  await fetch(`https://api.yourapp.com/api/notifications/${notificationId}/read`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${jwtToken}`,
    },
  });
}
```

### 5. Display Notification History

```javascript
async function fetchNotificationHistory(page = 0, size = 20) {
  const response = await fetch(
    `https://api.yourapp.com/api/notifications/history?page=${page}&size=${size}`,
    {
      headers: {
        'Authorization': `Bearer ${jwtToken}`,
      },
    }
  );
  
  const history = await response.json();
  console.log(`Total: ${history.total}, Unread: ${history.unreadCount}`);
  return history;
}
```

## Notification Types

### Rental Updates

#### Rental Start
- `BIKE_UNLOCKED`: Bike has been unlocked
- `RIDE_STARTED`: Ride has begun

#### Rental End Reminders
- `RENTAL_ENDING_SOON`: Rental ending soon (generic)
- `RENTAL_ENDING_30MIN`: 30 minutes remaining
- `RENTAL_ENDING_10MIN`: 10 minutes remaining

#### Rental Completion
- `BIKE_LOCKED`: Bike has been locked
- `RIDE_ENDED`: Ride has completed

### Payment Updates
- `PAYMENT_SUCCESS`: Payment successful
- `PAYMENT_FAILED`: Payment failed
- `REFUND_PROCESSED`: Refund processed

### Support Messages
- `SUPPORT_MESSAGE`: New support message
- `TICKET_RESOLVED`: Support ticket resolved

### Marketing
- `MARKETING`: Marketing message
- `PROMOTION`: Promotional offer

## Integration with Other Services

### rental-service Integration

The rental-service sends notifications for bike rental events:

```java
@Autowired
private NotificationClient notificationClient;

// Send notification
notificationClient.sendNotification(SendNotificationRequest.builder()
    .userExternalId(userId)
    .notificationType("BIKE_UNLOCKED")
    .title("Bike Unlocked 🚴")
    .body("Your bike has been unlocked. Have a great ride!")
    .data(Map.of("bikeRentalId", bikeRentalId))
    .priority("high")
    .build());
```

## Expo Server SDK

### Benefits

The service now uses the official Expo Server SDK for Java (v3.1.6) from [hlspablo/expo-server-sdk-java](https://github.com/hlspablo/expo-server-sdk-java) which provides:

- ✅ Automatic message chunking (max 100 per request)
- ✅ Built-in retry logic
- ✅ Proper error handling with typed responses
- ✅ Receipt checking support
- ✅ Async/CompletableFuture support
- ✅ Token validation

### Batch Sending

```java
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;

List<PushNotification> messages = buildMessages(notifications);
List<TicketResponse.Ticket> tickets = expoPushService.sendBatch(messages);

// Tickets contain the receipt IDs for tracking delivery
for (TicketResponse.Ticket ticket : tickets) {
    log.info("Notification sent with ID: {}, Status: {}", ticket.getId(), ticket.getStatus());
}
```

## Error Handling

### Expo Error Codes

The SDK automatically handles these errors:

- `DeviceNotRegistered`: Token is invalid → Automatically deactivated
- `MessageTooBig`: Notification payload too large → Logged
- `MessageRateExceeded`: Too many notifications → Logged
- `InvalidCredentials`: Invalid Expo access token → Logged

### Token Deactivation

When Expo reports `DeviceNotRegistered`, the token is automatically deactivated:

```java
if (ticket.getDetails() != null && 
    "DeviceNotRegistered".equals(ticket.getDetails().getError())) {
    tokenManagementService.deactivateToken(expoPushToken);
}
```

## Monitoring

### Key Metrics

- Notification success rate
- Failed notifications by error type
- Active tokens per user
- Unread notification count
- Notifications sent per hour
- Device distribution (iOS vs Android)
- App version distribution

### Logs

All notifications are logged to `notification_logs` table with:
- User ID
- Notification type
- Status (sent/failed)
- Error message (if failed)
- Expo receipt ID
- Read status and timestamp
- Delivery status

## Testing

### Manual Testing with curl

```bash
# Register token (requires JWT)
curl -X POST http://localhost:8084/api/notifications/register-token \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "expoPushToken": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
    "platform": "ios",
    "deviceId": "550e8400-e29b-41d4-a716-446655440000",
    "appVersion": "1.0.0",
    "deviceName": "iPhone 14 Pro",
    "deviceModel": "iPhone 15 Pro",
    "osVersion": "iOS 17.2"
  }'

# Get notification history
curl -X GET "http://localhost:8084/api/notifications/history?page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Mark notification as read
curl -X POST http://localhost:8084/api/notifications/123/read \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Mark all as read
curl -X POST http://localhost:8084/api/notifications/read-all \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Send notification (internal endpoint)
curl -X POST http://localhost:8084/api/notifications/internal/send \
  -H "Content-Type: application/json" \
  -d '{
    "userExternalId": "user-123",
    "notificationType": "BIKE_UNLOCKED",
    "title": "Test",
    "body": "Test notification",
    "priority": "high"
  }'
```

## Important Notes

### User External ID Usage

The notification-service uses `userExternalId` (String) for all user references, which is consistent with the microservices architecture:

- **Push tokens** are stored with `userExternalId`
- **Notification logs** reference `userExternalId`
- **Notification preferences** are keyed by `userExternalId`

This approach:
- ✅ Maintains consistency with other services (rental-service, support-service)
- ✅ Avoids the need to resolve external IDs to internal IDs
- ✅ Works with any ID format (UUID, numeric, etc.)
- ✅ Keeps services decoupled

### Device ID Tracking

Device IDs are used to:
- Prevent duplicate tokens for the same device
- Update tokens when app is reinstalled
- Track app version for debugging
- Identify device-specific issues

### Expo Account

- No Expo account required for development
- For production, create an Expo account at https://expo.dev
- Expo handles both iOS (APNS) and Android (FCM) automatically

## Migration Guide (v1 → v2)

### Database Changes

1. Run `migration-v2.sql` to add new columns
2. Existing data will be preserved
3. New fields will have default values

### API Changes

#### Breaking Changes
- `RegisterTokenRequest` now requires `platform`, `deviceId`, `appVersion`
- `GET /history` returns `NotificationHistoryResponse` instead of `Page<NotificationLog>`
- `POST /register-token` returns `RegisterTokenResponse` instead of `Void`

#### New Endpoints
- `POST /api/notifications/{id}/read`
- `POST /api/notifications/read-all`

#### New Fields
- `UpdatePreferencesRequest`: `rentalStartEnabled`, `rentalEndRemindersEnabled`, `rentalCompletionEnabled`

### Code Changes

Update mobile app to send device information:

```javascript
// Old
{
  "expoPushToken": "...",
  "deviceType": "ios",
  "deviceName": "iPhone"
}

// New
{
  "expoPushToken": "...",
  "platform": "ios",
  "deviceId": "uuid",
  "appVersion": "1.0.0",
  "deviceName": "iPhone",
  "deviceModel": "iPhone 15 Pro",
  "osVersion": "iOS 17.2"
}
```

## Troubleshooting

### Notifications not received

1. Check token is registered: `GET /api/notifications/history`
2. Verify token format: Must start with `ExponentPushToken[`
3. Check notification preferences: User may have disabled notifications
4. Check Expo API response in logs
5. Verify mobile app has notification permissions
6. Check device is active and not in Do Not Disturb mode

### Token registration fails

1. Verify JWT token is valid
2. Check token format is correct
3. Ensure all required fields are provided (platform, deviceId, appVersion)
4. Review logs for validation errors

### Read tracking not working

1. Ensure notification ID is correct
2. Verify user owns the notification
3. Check database for `is_read` and `read_at` fields

## Future Enhancements

1. **Scheduled Notifications**: "Your rental ends in 10 minutes"
2. **Rich Notifications**: Images, action buttons
3. **Notification Templates**: Centralized template management
4. **Multi-language Support**: Based on user language preference
5. **Email/SMS Fallback**: If push notification fails
6. **Admin Dashboard**: View notification analytics
7. **A/B Testing**: Test different notification messages
8. **Notification Channels**: Android notification channels support

## Support

For issues or questions, contact the development team.

## License

Apache 2.0
