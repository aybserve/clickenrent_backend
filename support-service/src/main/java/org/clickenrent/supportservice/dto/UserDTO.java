package org.clickenrent.supportservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity from auth-service.
 * Used for Feign client responses.
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

