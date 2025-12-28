package org.clickenrent.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for EmailVerification entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDTO {

    private Long id;
    private Long userId;
    private String email;
    private String code;
    private LocalDateTime expiresAt;
    private Integer attempts;
    private Boolean isUsed;
    private LocalDateTime usedAt;
}








