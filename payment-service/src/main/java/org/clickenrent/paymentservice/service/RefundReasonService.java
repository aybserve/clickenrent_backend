package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.RefundReasonDTO;
import org.clickenrent.paymentservice.entity.RefundReason;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.RefundReasonMapper;
import org.clickenrent.paymentservice.repository.RefundReasonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for RefundReason management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundReasonService {

    private final RefundReasonRepository refundReasonRepository;
    private final RefundReasonMapper refundReasonMapper;

    @Transactional(readOnly = true)
    public List<RefundReasonDTO> findAll() {
        List<RefundReason> reasons = refundReasonRepository.findAll();
        return refundReasonMapper.toDTOList(reasons);
    }

    @Transactional(readOnly = true)
    public RefundReasonDTO findById(Long id) {
        RefundReason reason = refundReasonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RefundReason", "id", id));
        return refundReasonMapper.toDTO(reason);
    }

    @Transactional(readOnly = true)
    public RefundReasonDTO findByCode(String code) {
        RefundReason reason = refundReasonRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("RefundReason", "code", code));
        return refundReasonMapper.toDTO(reason);
    }
}
