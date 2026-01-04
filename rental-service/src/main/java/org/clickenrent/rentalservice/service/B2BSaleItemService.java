package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleItemDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.entity.B2BSaleItem;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSaleItemMapper;
import org.clickenrent.rentalservice.repository.B2BSaleItemRepository;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleItemService {

    private final B2BSaleItemRepository b2bSaleItemRepository;
    private final B2BSaleRepository b2bSaleRepository;
    private final B2BSaleItemMapper b2bSaleItemMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<B2BSaleItemDTO> getItemsBySale(Long b2bSaleId) {
        B2BSale sale = b2bSaleRepository.findById(b2bSaleId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", b2bSaleId));

        if (!securityService.isAdmin() && 
            !securityService.hasAccessToCompanyByExternalId(sale.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view items for this sale");
        }

        return b2bSaleItemRepository.findByB2bSale(sale).stream()
                .map(b2bSaleItemMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleItemDTO getItemById(Long id) {
        B2BSaleItem item = b2bSaleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleItem", "id", id));
        return b2bSaleItemMapper.toDto(item);
    }

    @Transactional
    public B2BSaleItemDTO createItem(B2BSaleItemDTO dto) {
        B2BSale sale = b2bSaleRepository.findById(dto.getB2bSaleId())
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", dto.getB2bSaleId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(sale.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to add items to this sale");
        }

        B2BSaleItem item = b2bSaleItemMapper.toEntity(dto);
        item = b2bSaleItemRepository.save(item);
        return b2bSaleItemMapper.toDto(item);
    }

    @Transactional
    public B2BSaleItemDTO updateItem(Long id, B2BSaleItemDTO dto) {
        B2BSaleItem item = b2bSaleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleItem", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(item.getB2bSale().getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this item");
        }

        b2bSaleItemMapper.updateEntityFromDto(dto, item);
        item = b2bSaleItemRepository.save(item);
        return b2bSaleItemMapper.toDto(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        B2BSaleItem item = b2bSaleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleItem", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete items");
        }

        b2bSaleItemRepository.delete(item);
    }
}

