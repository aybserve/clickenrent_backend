package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.client.RentalServiceClient;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutItemDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayoutItem;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BRevenueSharePayoutItemMapper;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for B2B Revenue Share Payout Item management
 */
@Service
@RequiredArgsConstructor
public class B2BRevenueSharePayoutItemService {

    private final B2BRevenueSharePayoutItemRepository payoutItemRepository;
    private final B2BRevenueSharePayoutItemMapper payoutItemMapper;
    private final SecurityService securityService;
    private final RentalServiceClient rentalServiceClient;

    @Transactional(readOnly = true)
    public List<B2BRevenueSharePayoutItemDTO> findAll() {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout items");
        }
        return payoutItemMapper.toDTOList(payoutItemRepository.findAll());
    }

    @Transactional(readOnly = true)
    public B2BRevenueSharePayoutItemDTO findById(Long id) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout items");
        }
        
        B2BRevenueSharePayoutItem item = payoutItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayoutItem", "id", id));
        
        return payoutItemMapper.toDTO(item);
    }

    @Transactional(readOnly = true)
    public B2BRevenueSharePayoutItemDTO findByExternalId(UUID externalId) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout items");
        }
        
        B2BRevenueSharePayoutItem item = payoutItemRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayoutItem", "externalId", externalId));
        
        return payoutItemMapper.toDTO(item);
    }

    @Transactional(readOnly = true)
    public List<B2BRevenueSharePayoutItemDTO> findByPayoutId(Long payoutId) {
        if (!securityService.isAdmin() && !securityService.isB2B()) {
            throw new UnauthorizedException("You don't have permission to view payout items");
        }
        
        List<B2BRevenueSharePayoutItem> items = payoutItemRepository.findByB2bRevenueSharePayoutId(payoutId);
        return payoutItemMapper.toDTOList(items);
    }

    @Transactional
    public B2BRevenueSharePayoutItemDTO create(B2BRevenueSharePayoutItemDTO dto) {
        // Validate bike rental exists
        rentalServiceClient.checkBikeRentalExists(dto.getBikeRentalId());
        
        B2BRevenueSharePayoutItem item = payoutItemMapper.toEntity(dto);
        B2BRevenueSharePayoutItem savedItem = payoutItemRepository.save(item);
        return payoutItemMapper.toDTO(savedItem);
    }

    @Transactional
    public B2BRevenueSharePayoutItemDTO update(Long id, B2BRevenueSharePayoutItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can update payout items");
        }
        
        B2BRevenueSharePayoutItem existingItem = payoutItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayoutItem", "id", id));
        
        existingItem.setBikeRentalId(dto.getBikeRentalId());
        existingItem.setAmount(dto.getAmount());
        
        B2BRevenueSharePayoutItem updatedItem = payoutItemRepository.save(existingItem);
        return payoutItemMapper.toDTO(updatedItem);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete payout items");
        }
        
        if (!payoutItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("B2BRevenueSharePayoutItem", "id", id);
        }
        payoutItemRepository.deleteById(id);
    }
}

