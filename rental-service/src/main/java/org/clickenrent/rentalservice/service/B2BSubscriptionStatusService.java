package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSubscriptionStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSubscriptionStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSubscriptionStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSubscriptionStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSubscriptionStatusService {

    private final B2BSubscriptionStatusRepository b2bSubscriptionStatusRepository;
    private final B2BSubscriptionStatusMapper b2bSubscriptionStatusMapper;

    @Transactional(readOnly = true)
    public List<B2BSubscriptionStatusDTO> getAllStatuses() {
        return b2bSubscriptionStatusRepository.findAll().stream()
                .map(b2bSubscriptionStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSubscriptionStatusDTO getStatusById(Long id) {
        B2BSubscriptionStatus status = b2bSubscriptionStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSubscriptionStatus", "id", id));
        return b2bSubscriptionStatusMapper.toDto(status);
    }
}








