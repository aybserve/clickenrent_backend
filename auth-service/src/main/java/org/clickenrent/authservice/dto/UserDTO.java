package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * Contains user profile information and audit fields.
 * Password is excluded for security reasons.
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

    // Audit fields
    private LocalDateTime dateCreated;
    private LocalDateTime lastDateModified;
    private String createdBy;
    private String lastModifiedBy;
}

