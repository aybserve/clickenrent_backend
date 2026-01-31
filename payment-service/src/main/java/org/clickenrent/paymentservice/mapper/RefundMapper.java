package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.RefundDTO;
import org.clickenrent.paymentservice.entity.Refund;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Refund entity and DTO
 */
@Component
@RequiredArgsConstructor
public class RefundMapper {

    private final CurrencyMapper currencyMapper;
    private final RefundStatusMapper refundStatusMapper;
    private final RefundReasonMapper refundReasonMapper;

    public RefundDTO toDTO(Refund entity) {
        if (entity == null) {
            return null;
        }
        
        return RefundDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .financialTransactionId(entity.getFinancialTransaction() != null ? 
                        entity.getFinancialTransaction().getId() : null)
                .amount(entity.getAmount())
                .currency(currencyMapper.toDTO(entity.getCurrency()))
                .refundStatus(refundStatusMapper.toDTO(entity.getRefundStatus()))
                .refundReason(refundReasonMapper.toDTO(entity.getRefundReason()))
                .description(entity.getDescription())
                .initiatedByExternalId(entity.getInitiatedByExternalId())
                .processedAt(entity.getProcessedAt())
                .stripeRefundId(entity.getStripeRefundId())
                .multisafepayRefundId(entity.getMultisafepayRefundId())
                .companyExternalId(entity.getCompanyExternalId())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public Refund toEntity(RefundDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Refund.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .amount(dto.getAmount())
                .currency(currencyMapper.toEntity(dto.getCurrency()))
                .refundStatus(refundStatusMapper.toEntity(dto.getRefundStatus()))
                .refundReason(refundReasonMapper.toEntity(dto.getRefundReason()))
                .description(dto.getDescription())
                .initiatedByExternalId(dto.getInitiatedByExternalId())
                .processedAt(dto.getProcessedAt())
                .stripeRefundId(dto.getStripeRefundId())
                .multisafepayRefundId(dto.getMultisafepayRefundId())
                .companyExternalId(dto.getCompanyExternalId())
                .build();
    }

    public List<RefundDTO> toDTOList(List<Refund> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
