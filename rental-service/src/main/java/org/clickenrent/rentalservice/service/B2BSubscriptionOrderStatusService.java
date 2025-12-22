package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionOrderStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionOrderStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionOrderStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionOrderStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSubscriptionOrderStatusService {

    private final B2BSubscriptionOrderStatusRepository b2bSubscriptionOrderStatusRepository;
    private final B2BSubscriptionOrderStatusMapper b2bSubscriptionOrderStatusMapper;

    @Transactional(readOnly = true)
    public List<B2BSubscriptionOrderStatusDTO> getAllStatuses() {
        return b2bSubscriptionOrderStatusRepository.findAll().stream()
                .map(b2bSubscriptionOrderStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionOrderStatusDTO getStatusById(Long id) {
        B2BSubscriptionOrderStatus status = b2bSubscriptionOrderStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionOrderStatus", "id", id));
        return b2bSubscriptionOrderStatusMapper.toDto(status);
    }
}




