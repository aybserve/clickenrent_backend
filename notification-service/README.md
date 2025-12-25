# Notification Service

Push notification service for Click & Rent platform using Expo Push Notifications.

## Overview

The notification-service handles push notifications to mobile devices via Expo Push Notification Service. It manages:
- Push token registration and management
- Notification delivery via Expo API
- User notification preferences
- Notification history and audit logs

## Architecture

```
Mobile App (Expo) → API Gateway → notification-service → Expo Push API
rental-service ────────────────→ notification-service (via Feign)
```

## Features

- **Push Token Management**: Register and manage Expo Push Tokens for user devices
- **Notification Delivery**: Send push notifications via Expo API
- **User Preferences**: Users can enable/disable notification types
- **Notification History**: Track all sent notifications
- **Error Handling**: Automatic token deactivation for invalid tokens
- **Multi-device Support**: Users can have multiple devices registered

## Database Schema

### Tables

1. **push_tokens**: Stores Expo Push Tokens per user/device
2. **notification_logs**: Audit trail of all notifications sent
3. **notification_preferences**: User preferences for notification types

See `notification-service.sql` for full schema.

## API Endpoints

### Public Endpoints (Require JWT)

- `POST /api/notifications/register-token` - Register push token from mobile app
- `GET /api/notifications/history` - Get notification history
- `DELETE /api/notifications/tokens/{token}` - Delete a push token
- `GET /api/notifications/preferences` - Get notification preferences
- `PUT /api/notifications/preferences` - Update notification preferences

### Internal Endpoints (For Microservices)

- `POST /api/notifications/internal/send` - Send a notification (called by other services)

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

# Expo API
expo.api.url=https://exp.host/--/api/v2/push/send
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

### 2. Build and Run

```bash
# From project root
mvn clean install

# Run notification-service
cd notification-service
mvn spring-boot:run
```

### 3. Verify Service

- Service: http://localhost:8084
- Swagger UI: http://localhost:8084/swagger-ui.html
- Health: http://localhost:8084/actuator/health

## Mobile App Integration

### 1. Install Expo Notifications

```bash
npx expo install expo-notifications expo-device
```

### 2. Request Permissions and Get Token

```javascript
import * as Notifications from 'expo-notifications';
import * as Device from 'expo-device';

async function registerForPushNotifications() {
  if (Device.isDevice) {
    const { status } = await Notifications.requestPermissionsAsync();
    if (status !== 'granted') {
      alert('Permission not granted!');
      return;
    }
    const token = await Notifications.getExpoPushTokenAsync();
    return token.data;
  }
}
```

### 3. Register Token with Backend

```javascript
const expoPushToken = await registerForPushNotifications();

await fetch('https://api.yourapp.com/api/notifications/register-token', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${jwtToken}`,
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    expoPushToken: expoPushToken,
    deviceType: Platform.OS,
    deviceName: Device.deviceName,
  }),
});
```

### 4. Handle Notifications

```javascript
// Handle foreground notifications
Notifications.addNotificationReceivedListener(notification => {
  console.log('Notification received:', notification);
});

// Handle notification taps
Notifications.addNotificationResponseReceivedListener(response => {
  const data = response.notification.request.content.data;
  // Navigate based on notification type
  if (data.notificationType === 'BIKE_UNLOCKED') {
    navigation.navigate('RideScreen', { bikeRentalId: data.bikeRentalId });
  }
});
```

## Notification Types

### Rental Updates
- `BIKE_UNLOCKED`: Bike has been unlocked
- `BIKE_LOCKED`: Bike has been locked
- `RIDE_STARTED`: Ride has begun
- `RIDE_ENDED`: Ride has completed

### Payment Updates
- `PAYMENT_SUCCESS`: Payment successful
- `PAYMENT_FAILED`: Payment failed
- `REFUND_PROCESSED`: Refund processed

### Support Messages
- `SUPPORT_MESSAGE`: New support message
- `TICKET_RESOLVED`: Support ticket resolved

## Integration with Other Services

### rental-service Integration

The rental-service sends notifications for bike rental events:

```java
@Autowired
private NotificationClient notificationClient;

// Send notification
notificationClient.sendNotification(SendNotificationRequest.builder()
    .userId(userId)
    .notificationType("BIKE_UNLOCKED")
    .title("Bike Unlocked 🚴")
    .body("Your bike has been unlocked. Have a great ride!")
    .data(Map.of("bikeRentalId", bikeRentalId))
    .priority("high")
    .build());
```

## Error Handling

### Expo Error Codes

- `DeviceNotRegistered`: Token is invalid → Automatically deactivated
- `MessageTooBig`: Notification payload too large → Truncate message
- `MessageRateExceeded`: Too many notifications → Implement backoff
- `InvalidCredentials`: Invalid Expo access token → Check configuration

## Monitoring

### Key Metrics

- Notification success rate
- Failed notifications by error type
- Active tokens per user
- Notifications sent per hour

### Logs

All notifications are logged to `notification_logs` table with:
- User ID
- Notification type
- Status (sent/failed)
- Error message (if failed)
- Expo receipt ID

## Testing

### Manual Testing with curl

```bash
# Test Expo API directly
curl -H "Content-Type: application/json" -X POST https://exp.host/--/api/v2/push/send -d '{
  "to": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
  "title": "Test Notification",
  "body": "Hello from Click & Rent!"
}'
```

### Test notification-service

```bash
# Register token (requires JWT)
curl -X POST http://localhost:8084/api/notifications/register-token \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "expoPushToken": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
    "deviceType": "ios",
    "deviceName": "iPhone 14 Pro"
  }'

# Send notification (internal endpoint)
curl -X POST http://localhost:8084/api/notifications/internal/send \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
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

### Expo Account

- No Expo account required for development
- For production, create an Expo account at https://expo.dev
- Expo handles both iOS (APNS) and Android (FCM) automatically

## Future Enhancements

1. **Scheduled Notifications**: "Your rental ends in 10 minutes"
2. **Rich Notifications**: Images, action buttons
3. **Notification Templates**: Centralized template management
4. **Multi-language Support**: Based on user language preference
5. **Email/SMS Fallback**: If push notification fails
6. **Admin Dashboard**: View notification analytics

## Troubleshooting

### Notifications not received

1. Check token is registered: `GET /api/notifications/history`
2. Verify token format: Must start with `ExponentPushToken[`
3. Check notification preferences: User may have disabled notifications
4. Check Expo API response in logs
5. Verify mobile app has notification permissions

### Token registration fails

1. Verify JWT token is valid
2. Check token format is correct
3. Review logs for validation errors

## Support

For issues or questions, contact the development team.

## License

Apache 2.0

