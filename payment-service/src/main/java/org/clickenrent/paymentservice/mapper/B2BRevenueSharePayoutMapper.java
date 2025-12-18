package org.clickenrent.paymentservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayoutItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for B2BRevenueSharePayout entity and DTO
 */
@Component
@RequiredArgsConstructor
public class B2BRevenueSharePayoutMapper {

    private final PaymentStatusMapper paymentStatusMapper;
    private final B2BRevenueSharePayoutItemMapper payoutItemMapper;

    public B2BRevenueSharePayoutDTO toDTO(B2BRevenueSharePayout entity) {
        if (entity == null) {
            return null;
        }
        
        return B2BRevenueSharePayoutDTO.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .companyId(entity.getCompanyId())
                .paymentStatus(paymentStatusMapper.toDTO(entity.getPaymentStatus()))
                .dueDate(entity.getDueDate())
                .totalAmount(entity.getTotalAmount())
                .paidAmount(entity.getPaidAmount())
                .remainingAmount(entity.getRemainingAmount())
                .payoutItems(payoutItemMapper.toDTOList(entity.getPayoutItems()))
                .build();
    }

    public B2BRevenueSharePayout toEntity(B2BRevenueSharePayoutDTO dto) {
        if (dto == null) {
            return null;
        }
        
        B2BRevenueSharePayout entity = B2BRevenueSharePayout.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .companyId(dto.getCompanyId())
                .paymentStatus(paymentStatusMapper.toEntity(dto.getPaymentStatus()))
                .dueDate(dto.getDueDate())
                .totalAmount(dto.getTotalAmount())
                .paidAmount(dto.getPaidAmount())
                .remainingAmount(dto.getRemainingAmount())
                .build();
        
        // Map payout items and set parent reference
        if (dto.getPayoutItems() != null && !dto.getPayoutItems().isEmpty()) {
            List<B2BRevenueSharePayoutItem> items = dto.getPayoutItems().stream()
                    .map(itemDto -> {
                        B2BRevenueSharePayoutItem item = payoutItemMapper.toEntity(itemDto);
                        item.setB2bRevenueSharePayout(entity);
                        return item;
                    })
                    .collect(Collectors.toList());
            entity.setPayoutItems(items);
        }
        
        return entity;
    }

    public List<B2BRevenueSharePayoutDTO> toDTOList(List<B2BRevenueSharePayout> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

