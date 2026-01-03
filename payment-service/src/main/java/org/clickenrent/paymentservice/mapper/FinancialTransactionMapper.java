package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.FinancialTransactionDTO;
import org.clickenrent.paymentservice.entity.FinancialTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for FinancialTransaction entity and DTO
 */
@Component
@RequiredArgsConstructor
public class FinancialTransactionMapper {

    private final CurrencyMapper currencyMapper;
    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentStatusMapper paymentStatusMapper;
    private final ServiceProviderMapper serviceProviderMapper;

    public FinancialTransactionDTO toDTO(FinancialTransaction entity) {
        if (entity == null) {
            return null;
        }
        
        return FinancialTransactionDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .payerExternalId(entity.getPayerExternalId())
                .recipientExternalId(entity.getRecipientExternalId())
                .amount(entity.getAmount())
                .currency(currencyMapper.toDTO(entity.getCurrency()))
                .dateTime(entity.getDateTime())
                .paymentMethod(paymentMethodMapper.toDTO(entity.getPaymentMethod()))
                .paymentStatus(paymentStatusMapper.toDTO(entity.getPaymentStatus()))
                .serviceProvider(serviceProviderMapper.toDTO(entity.getServiceProvider()))
                .stripePaymentIntentId(entity.getStripePaymentIntentId())
                .stripeChargeId(entity.getStripeChargeId())
                .stripeRefundId(entity.getStripeRefundId())
                .originalTransactionId(entity.getOriginalTransactionId())
                .dateCreated(entity.getDateCreated())
                .lastDateModified(entity.getLastDateModified())
                .createdBy(entity.getCreatedBy())
                .lastModifiedBy(entity.getLastModifiedBy())
                .build();
    }

    public FinancialTransaction toEntity(FinancialTransactionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return FinancialTransaction.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .payerExternalId(dto.getPayerExternalId())
                .recipientExternalId(dto.getRecipientExternalId())
                .amount(dto.getAmount())
                .currency(currencyMapper.toEntity(dto.getCurrency()))
                .dateTime(dto.getDateTime())
                .paymentMethod(paymentMethodMapper.toEntity(dto.getPaymentMethod()))
                .paymentStatus(paymentStatusMapper.toEntity(dto.getPaymentStatus()))
                .serviceProvider(serviceProviderMapper.toEntity(dto.getServiceProvider()))
                .stripePaymentIntentId(dto.getStripePaymentIntentId())
                .stripeChargeId(dto.getStripeChargeId())
                .stripeRefundId(dto.getStripeRefundId())
                .originalTransactionId(dto.getOriginalTransactionId())
                .build();
    }

    public List<FinancialTransactionDTO> toDTOList(List<FinancialTransaction> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}




