package org.clickenrent.contracts.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared contract DTO for User entity.
 * Used for cross-service communication.
 * 
 * Source: auth-service
 * Consumers: rental-service, payment-service, support-service
 * 
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String externalId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private String address;
    private String zipcode;
    private String imageUrl;
    private Long languageId;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isEmailVerified;
    private Boolean isAcceptedTerms;
    private Boolean isAcceptedPrivacyPolicy;

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}






