package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrder;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleOrderService {

    private final B2BSaleOrderRepository b2bSaleOrderRepository;
    private final B2BSaleOrderMapper b2bSaleOrderMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSaleOrderDTO> getAllOrders(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSaleOrderRepository.findAll(pageable)
                    .map(b2bSaleOrderMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all B2B sale orders");
    }

    @Transactional(readOnly = true)
    public List<B2BSaleOrderDTO> getOrdersBySellerCompanyExternalId(String sellerCompanyExternalId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(sellerCompanyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view orders for this company");
        }

        return b2bSaleOrderRepository.findBySellerCompanyExternalId(sellerCompanyExternalId).stream()
                .map(b2bSaleOrderMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<B2BSaleOrderDTO> getOrdersByBuyerCompanyExternalId(String buyerCompanyExternalId) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(buyerCompanyExternalId)) {
            throw new UnauthorizedException("You don't have permission to view orders for this company");
        }

        return b2bSaleOrderRepository.findByBuyerCompanyExternalId(buyerCompanyExternalId).stream()
                .map(b2bSaleOrderMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleOrderDTO getOrderById(Long id) {
        B2BSaleOrder order = b2bSaleOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrder", "id", id));

        // Check access
        if (!securityService.isAdmin() && 
            !securityService.hasAccessToCompanyByExternalId(order.getSellerCompanyExternalId()) &&
            !securityService.hasAccessToCompanyByExternalId(order.getBuyerCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view this order");
        }

        return b2bSaleOrderMapper.toDto(order);
    }

    @Transactional
    public B2BSaleOrderDTO createOrder(B2BSaleOrderDTO dto) {
        // Validate user has access to seller company
        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(dto.getSellerCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to create orders for this company");
        }

        B2BSaleOrder order = b2bSaleOrderMapper.toEntity(dto);
        order = b2bSaleOrderRepository.save(order);
        return b2bSaleOrderMapper.toDto(order);
    }

    @Transactional
    public B2BSaleOrderDTO updateOrder(Long id, B2BSaleOrderDTO dto) {
        B2BSaleOrder order = b2bSaleOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrder", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(order.getSellerCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this order");
        }

        b2bSaleOrderMapper.updateEntityFromDto(dto, order);
        order = b2bSaleOrderRepository.save(order);
        return b2bSaleOrderMapper.toDto(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete B2B sale orders");
        }

        B2BSaleOrder order = b2bSaleOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrder", "id", id));
        b2bSaleOrderRepository.delete(order);
    }
}




