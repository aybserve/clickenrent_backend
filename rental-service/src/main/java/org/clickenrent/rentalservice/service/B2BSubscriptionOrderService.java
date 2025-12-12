package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrder;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionOrderMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class B2BSubscriptionOrderService {

    private final B2BSubscriptionOrderRepository b2bSubscriptionOrderRepository;
    private final B2BSubscriptionOrderMapper b2bSubscriptionOrderMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSubscriptionOrderDTO> getAllOrders(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSubscriptionOrderRepository.findAll(pageable)
                    .map(b2bSubscriptionOrderMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all B2B subscription orders");
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionOrderDTO getOrderById(Long id) {
        B2BSubscriptionOrder order = b2bSubscriptionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrder", "id", id));

        // Check access - admin only for now (could be enhanced with location-based access)
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this order");
        }

        return b2bSubscriptionOrderMapper.toDto(order);
    }

    @Transactional
    public B2BSubscriptionOrderDTO createOrder(B2BSubscriptionOrderDTO dto) {
        // Validate user has admin access
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create subscription orders");
        }

        B2BSubscriptionOrder order = b2bSubscriptionOrderMapper.toEntity(dto);
        order = b2bSubscriptionOrderRepository.save(order);
        return b2bSubscriptionOrderMapper.toDto(order);
    }

    @Transactional
    public B2BSubscriptionOrderDTO updateOrder(Long id, B2BSubscriptionOrderDTO dto) {
        B2BSubscriptionOrder order = b2bSubscriptionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrder", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this order");
        }

        b2bSubscriptionOrderMapper.updateEntityFromDto(dto, order);
        order = b2bSubscriptionOrderRepository.save(order);
        return b2bSubscriptionOrderMapper.toDto(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete B2B subscription orders");
        }

        B2BSubscriptionOrder order = b2bSubscriptionOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrder", "id", id));
        b2bSubscriptionOrderRepository.delete(order);
    }
}
