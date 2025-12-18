package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PayoutFinTransactionDTO;
import org.clickenrent.paymentservice.entity.PayoutFinTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PayoutFinTransaction entity and DTO
 */
@Component
@RequiredArgsConstructor
public class PayoutFinTransactionMapper {

    private final B2BRevenueSharePayoutMapper b2bRevenueSharePayoutMapper;
    private final FinancialTransactionMapper financialTransactionMapper;

    public PayoutFinTransactionDTO toDTO(PayoutFinTransaction entity) {
        if (entity == null) {
            return null;
        }
        
        return PayoutFinTransactionDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .b2bRevenueSharePayout(b2bRevenueSharePayoutMapper.toDTO(entity.getB2bRevenueSharePayout()))
                .financialTransaction(financialTransactionMapper.toDTO(entity.getFinancialTransaction()))
                .build();
    }

    public PayoutFinTransaction toEntity(PayoutFinTransactionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return PayoutFinTransaction.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .b2bRevenueSharePayout(b2bRevenueSharePayoutMapper.toEntity(dto.getB2bRevenueSharePayout()))
                .financialTransaction(financialTransactionMapper.toEntity(dto.getFinancialTransaction()))
                .build();
    }

    public List<PayoutFinTransactionDTO> toDTOList(List<PayoutFinTransaction> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

