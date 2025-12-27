package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderStatusDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderStatus;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderStatusMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleOrderStatusService {

    private final B2BSaleOrderStatusRepository b2bSaleOrderStatusRepository;
    private final B2BSaleOrderStatusMapper b2bSaleOrderStatusMapper;

    @Transactional(readOnly = true)
    public List<B2BSaleOrderStatusDTO> getAllStatuses() {
        return b2bSaleOrderStatusRepository.findAll().stream()
                .map(b2bSaleOrderStatusMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleOrderStatusDTO getStatusById(Long id) {
        B2BSaleOrderStatus status = b2bSaleOrderStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderStatus", "id", id));
        return b2bSaleOrderStatusMapper.toDto(status);
    }
}







