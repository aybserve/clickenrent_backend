package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PaymentStatusDTO;
import org.clickenrent.paymentservice.entity.PaymentStatus;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.PaymentStatusMapper;
import org.clickenrent.paymentservice.repository.PaymentStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for PaymentStatus management
 */
@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentStatusMapper paymentStatusMapper;

    @Transactional(readOnly = true)
    public List<PaymentStatusDTO> findAll() {
        return paymentStatusMapper.toDTOList(paymentStatusRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PaymentStatusDTO findById(Long id) {
        PaymentStatus status = paymentStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "id", id));
        return paymentStatusMapper.toDTO(status);
    }

    @Transactional(readOnly = true)
    public PaymentStatusDTO findByExternalId(String externalId) {
        PaymentStatus status = paymentStatusRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "externalId", externalId));
        return paymentStatusMapper.toDTO(status);
    }

    @Transactional(readOnly = true)
    public PaymentStatusDTO findByCode(String code) {
        PaymentStatus status = paymentStatusRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "code", code));
        return paymentStatusMapper.toDTO(status);
    }

    @Transactional
    public PaymentStatusDTO create(PaymentStatusDTO dto) {
        if (paymentStatusRepository.findByCode(dto.getCode()).isPresent()) {
            throw new DuplicateResourceException("PaymentStatus", "code", dto.getCode());
        }

        PaymentStatus status = paymentStatusMapper.toEntity(dto);
        PaymentStatus savedStatus = paymentStatusRepository.save(status);
        return paymentStatusMapper.toDTO(savedStatus);
    }

    @Transactional
    public PaymentStatusDTO update(Long id, PaymentStatusDTO dto) {
        PaymentStatus existingStatus = paymentStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentStatus", "id", id));

        if (!existingStatus.getCode().equals(dto.getCode())) {
            if (paymentStatusRepository.findByCode(dto.getCode()).isPresent()) {
                throw new DuplicateResourceException("PaymentStatus", "code", dto.getCode());
            }
        }

        existingStatus.setCode(dto.getCode());
        existingStatus.setName(dto.getName());

        PaymentStatus updatedStatus = paymentStatusRepository.save(existingStatus);
        return paymentStatusMapper.toDTO(updatedStatus);
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("PaymentStatus", "id", id);
        }
        paymentStatusRepository.deleteById(id);
    }
}


