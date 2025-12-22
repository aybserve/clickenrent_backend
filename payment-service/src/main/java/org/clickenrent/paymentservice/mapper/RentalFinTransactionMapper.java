package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.RentalFinTransactionDTO;
import org.clickenrent.paymentservice.entity.RentalFinTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for RentalFinTransaction entity and DTO
 */
@Component
@RequiredArgsConstructor
public class RentalFinTransactionMapper {

    private final FinancialTransactionMapper financialTransactionMapper;

    public RentalFinTransactionDTO toDTO(RentalFinTransaction entity) {
        if (entity == null) {
            return null;
        }
        
        return RentalFinTransactionDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .rentalId(entity.getRentalId())
                .bikeRentalId(entity.getBikeRentalId())
                .rentalExternalId(entity.getRentalExternalId())
                .bikeRentalExternalId(entity.getBikeRentalExternalId())
                .financialTransaction(financialTransactionMapper.toDTO(entity.getFinancialTransaction()))
                .build();
    }

    public RentalFinTransaction toEntity(RentalFinTransactionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return RentalFinTransaction.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .rentalId(dto.getRentalId())
                .bikeRentalId(dto.getBikeRentalId())
                .financialTransaction(financialTransactionMapper.toEntity(dto.getFinancialTransaction()))
                .build();
    }

    public List<RentalFinTransactionDTO> toDTOList(List<RentalFinTransaction> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}




