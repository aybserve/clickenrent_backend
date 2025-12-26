package org.clickenrent.contracts.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for notification history endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHistoryResponse {

    /**
     * List of notifications
     */
    private List<NotificationDTO> notifications;

    /**
     * Total number of notifications
     */
    private Long total;

    /**
     * Number of unread notifications
     */
    private Long unreadCount;

    /**
     * Current page number (0-based)
     */
    private Integer page;

    /**
     * Page size
     */
    private Integer size;

    /**
     * Total number of pages
     */
    private Integer totalPages;
}

