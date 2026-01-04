package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderItemDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderItem;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderItemMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleOrderItemService {

    private final B2BSaleOrderItemRepository b2bSaleOrderItemRepository;
    private final B2BSaleOrderItemMapper b2bSaleOrderItemMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSaleOrderItemDTO> getAllItems(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSaleOrderItemRepository.findAll(pageable)
                    .map(b2bSaleOrderItemMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all items");
    }

    @Transactional(readOnly = true)
    public List<B2BSaleOrderItemDTO> getItemsByOrderId(Long orderId) {
        // Access control handled at order level
        return b2bSaleOrderItemRepository.findByB2bSaleOrderId(orderId).stream()
                .map(b2bSaleOrderItemMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleOrderItemDTO getItemById(Long id) {
        B2BSaleOrderItem item = b2bSaleOrderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderItem", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this item");
        }

        return b2bSaleOrderItemMapper.toDto(item);
    }

    @Transactional
    public B2BSaleOrderItemDTO createItem(B2BSaleOrderItemDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create items");
        }

        B2BSaleOrderItem item = b2bSaleOrderItemMapper.toEntity(dto);
        item = b2bSaleOrderItemRepository.save(item);
        return b2bSaleOrderItemMapper.toDto(item);
    }

    @Transactional
    public B2BSaleOrderItemDTO updateItem(Long id, B2BSaleOrderItemDTO dto) {
        B2BSaleOrderItem item = b2bSaleOrderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderItem", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this item");
        }

        b2bSaleOrderItemMapper.updateEntityFromDto(dto, item);
        item = b2bSaleOrderItemRepository.save(item);
        return b2bSaleOrderItemMapper.toDto(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete items");
        }

        B2BSaleOrderItem item = b2bSaleOrderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderItem", "id", id));
        b2bSaleOrderItemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public B2BSaleOrderItemDTO findByExternalId(String externalId) {
        B2BSaleOrderItem item = b2bSaleOrderItemRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderItem", "externalId", externalId));
        return b2bSaleOrderItemMapper.toDto(item);
    }
}

