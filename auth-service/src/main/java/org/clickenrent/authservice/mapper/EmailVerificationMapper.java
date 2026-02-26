package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.EmailVerificationDTO;
import org.clickenrent.authservice.entity.EmailVerification;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between EmailVerification entity and EmailVerificationDTO.
 */
@Component
public class EmailVerificationMapper {

    public EmailVerificationDTO toDto(EmailVerification entity) {
        if (entity == null) {
            return null;
        }

        return EmailVerificationDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .email(entity.getEmail())
                .code(entity.getCode())
                .expiresAt(entity.getExpiresAt())
                .attempts(entity.getAttempts())
                .isUsed(entity.getIsUsed())
                .usedAt(entity.getUsedAt())
                .build();
    }

    public EmailVerification toEntity(EmailVerificationDTO dto) {
        if (dto == null) {
            return null;
        }

        return EmailVerification.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .email(dto.getEmail())
                .code(dto.getCode())
                .expiresAt(dto.getExpiresAt())
                .attempts(dto.getAttempts())
                .isUsed(dto.getIsUsed())
                .usedAt(dto.getUsedAt())
                .build();
    }

    public void updateEntityFromDto(EmailVerificationDTO dto, EmailVerification entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getExpiresAt() != null) {
            entity.setExpiresAt(dto.getExpiresAt());
        }
        if (dto.getAttempts() != null) {
            entity.setAttempts(dto.getAttempts());
        }
        if (dto.getIsUsed() != null) {
            entity.setIsUsed(dto.getIsUsed());
        }
        if (dto.getUsedAt() != null) {
            entity.setUsedAt(dto.getUsedAt());
        }
    }
}








