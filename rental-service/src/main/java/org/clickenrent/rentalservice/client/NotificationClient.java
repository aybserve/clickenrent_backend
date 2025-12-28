package org.clickenrent.rentalservice.client;

import org.clickenrent.contracts.notification.SendNotificationRequest;
import org.clickenrent.contracts.notification.SendNotificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communication with notification-service.
 */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    /**
     * Send a push notification to a user.
     *
     * @param request Notification request
     * @return Response with success status and receipt ID
     */
    @PostMapping("/api/notifications/internal/send")
    SendNotificationResponse sendNotification(@RequestBody SendNotificationRequest request);
}



