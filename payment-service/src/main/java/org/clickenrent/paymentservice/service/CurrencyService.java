package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.paymentservice.dto.CurrencyDTO;
import org.clickenrent.paymentservice.entity.Currency;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.CurrencyMapper;
import org.clickenrent.paymentservice.repository.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for Currency management
 */
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Transactional(readOnly = true)
    public List<CurrencyDTO> findAll() {
        return currencyMapper.toDTOList(currencyRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CurrencyDTO findById(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", id));
        return currencyMapper.toDTO(currency);
    }

    @Transactional(readOnly = true)
    public CurrencyDTO findByExternalId(UUID externalId) {
        Currency currency = currencyRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "externalId", externalId));
        return currencyMapper.toDTO(currency);
    }

    @Transactional(readOnly = true)
    public CurrencyDTO findByCode(String code) {
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "code", code));
        return currencyMapper.toDTO(currency);
    }

    @Transactional
    public CurrencyDTO create(CurrencyDTO dto) {
        // Check for duplicate code
        if (currencyRepository.findByCode(dto.getCode()).isPresent()) {
            throw new DuplicateResourceException("Currency", "code", dto.getCode());
        }

        Currency currency = currencyMapper.toEntity(dto);
        Currency savedCurrency = currencyRepository.save(currency);
        return currencyMapper.toDTO(savedCurrency);
    }

    @Transactional
    public CurrencyDTO update(Long id, CurrencyDTO dto) {
        Currency existingCurrency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", id));

        // Check for duplicate code if code is being changed
        if (!existingCurrency.getCode().equals(dto.getCode())) {
            if (currencyRepository.findByCode(dto.getCode()).isPresent()) {
                throw new DuplicateResourceException("Currency", "code", dto.getCode());
            }
        }

        existingCurrency.setCode(dto.getCode());
        existingCurrency.setName(dto.getName());

        Currency updatedCurrency = currencyRepository.save(existingCurrency);
        return currencyMapper.toDTO(updatedCurrency);
    }

    @Transactional
    public void delete(Long id) {
        if (!currencyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Currency", "id", id);
        }
        currencyRepository.deleteById(id);
    }
}
