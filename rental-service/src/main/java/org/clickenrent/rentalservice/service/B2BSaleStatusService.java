package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSaleStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleStatusService {

    private final B2BSaleStatusRepository b2bSaleStatusRepository;
    private final B2BSaleStatusMapper b2bSaleStatusMapper;

    @Transactional(readOnly = true)
    public List<B2BSaleStatusDTO> getAllStatuses() {
        return b2bSaleStatusRepository.findAll().stream()
                .map(b2bSaleStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleStatusDTO getStatusById(Long id) {
        B2BSaleStatus status = b2bSaleStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleStatus", "id", id));
        return b2bSaleStatusMapper.toDto(status);
    }
}

