package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.RefundStatusDTO;
import org.clickenrent.paymentservice.entity.RefundStatus;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.RefundStatusMapper;
import org.clickenrent.paymentservice.repository.RefundStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for RefundStatus management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundStatusService {

    private final RefundStatusRepository refundStatusRepository;
    private final RefundStatusMapper refundStatusMapper;

    @Transactional(readOnly = true)
    public List<RefundStatusDTO> findAll() {
        List<RefundStatus> statuses = refundStatusRepository.findAll();
        return refundStatusMapper.toDTOList(statuses);
    }

    @Transactional(readOnly = true)
    public RefundStatusDTO findById(Long id) {
        RefundStatus status = refundStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RefundStatus", "id", id));
        return refundStatusMapper.toDTO(status);
    }

    @Transactional(readOnly = true)
    public RefundStatusDTO findByCode(String code) {
        RefundStatus status = refundStatusRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("RefundStatus", "code", code));
        return refundStatusMapper.toDTO(status);
    }
}
