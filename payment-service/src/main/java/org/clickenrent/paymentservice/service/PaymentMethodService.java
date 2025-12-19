package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.PaymentMethodDTO;
import org.clickenrent.paymentservice.entity.PaymentMethod;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.PaymentMethodMapper;
import org.clickenrent.paymentservice.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for PaymentMethod management
 */
@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> findAll() {
        return paymentMethodMapper.toDTOList(paymentMethodRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> findActivePaymentMethods() {
        return paymentMethodMapper.toDTOList(paymentMethodRepository.findByIsActive(true));
    }

    @Transactional(readOnly = true)
    public PaymentMethodDTO findById(Long id) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));
        return paymentMethodMapper.toDTO(method);
    }

    @Transactional(readOnly = true)
    public PaymentMethodDTO findByExternalId(String externalId) {
        PaymentMethod method = paymentMethodRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "externalId", externalId));
        return paymentMethodMapper.toDTO(method);
    }

    @Transactional(readOnly = true)
    public PaymentMethodDTO findByCode(String code) {
        PaymentMethod method = paymentMethodRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "code", code));
        return paymentMethodMapper.toDTO(method);
    }

    @Transactional
    public PaymentMethodDTO create(PaymentMethodDTO dto) {
        if (paymentMethodRepository.findByCode(dto.getCode()).isPresent()) {
            throw new DuplicateResourceException("PaymentMethod", "code", dto.getCode());
        }

        PaymentMethod method = paymentMethodMapper.toEntity(dto);
        PaymentMethod savedMethod = paymentMethodRepository.save(method);
        return paymentMethodMapper.toDTO(savedMethod);
    }

    @Transactional
    public PaymentMethodDTO update(Long id, PaymentMethodDTO dto) {
        PaymentMethod existingMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", id));

        if (!existingMethod.getCode().equals(dto.getCode())) {
            if (paymentMethodRepository.findByCode(dto.getCode()).isPresent()) {
                throw new DuplicateResourceException("PaymentMethod", "code", dto.getCode());
            }
        }

        existingMethod.setCode(dto.getCode());
        existingMethod.setName(dto.getName());
        existingMethod.setIsActive(dto.getIsActive());

        PaymentMethod updatedMethod = paymentMethodRepository.save(existingMethod);
        return paymentMethodMapper.toDTO(updatedMethod);
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new ResourceNotFoundException("PaymentMethod", "id", id);
        }
        paymentMethodRepository.deleteById(id);
    }
}


