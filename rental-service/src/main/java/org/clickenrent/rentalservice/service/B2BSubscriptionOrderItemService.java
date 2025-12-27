package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderItemDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderItem;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionOrderItemMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSubscriptionOrderItemService {

    private final B2BSubscriptionOrderItemRepository b2bSubscriptionOrderItemRepository;
    private final B2BSubscriptionOrderItemMapper b2bSubscriptionOrderItemMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSubscriptionOrderItemDTO> getAllItems(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSubscriptionOrderItemRepository.findAll(pageable)
                    .map(b2bSubscriptionOrderItemMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all items");
    }

    @Transactional(readOnly = true)
    public List<B2BSubscriptionOrderItemDTO> getItemsByOrderId(Long orderId) {
        // Access control handled at order level
        return b2bSubscriptionOrderItemRepository.findByB2bSubscriptionOrderId(orderId).stream()
                .map(b2bSubscriptionOrderItemMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionOrderItemDTO getItemById(Long id) {
        B2BSubscriptionOrderItem item = b2bSubscriptionOrderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrderItem", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this item");
        }

        return b2bSubscriptionOrderItemMapper.toDto(item);
    }

    @Transactional
    public B2BSubscriptionOrderItemDTO createItem(B2BSubscriptionOrderItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create items");
        }

        B2BSubscriptionOrderItem item = b2bSubscriptionOrderItemMapper.toEntity(dto);
        item = b2bSubscriptionOrderItemRepository.save(item);
        return b2bSubscriptionOrderItemMapper.toDto(item);
    }

    @Transactional
    public B2BSubscriptionOrderItemDTO updateItem(Long id, B2BSubscriptionOrderItemDTO dto) {
        B2BSubscriptionOrderItem item = b2bSubscriptionOrderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrderItem", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this item");
        }

        b2bSubscriptionOrderItemMapper.updateEntityFromDto(dto, item);
        item = b2bSubscriptionOrderItemRepository.save(item);
        return b2bSubscriptionOrderItemMapper.toDto(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete items");
        }

        B2BSubscriptionOrderItem item = b2bSubscriptionOrderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrderItem", "id", id));
        b2bSubscriptionOrderItemRepository.delete(item);
    }
}







