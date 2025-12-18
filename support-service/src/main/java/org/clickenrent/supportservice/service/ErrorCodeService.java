package org.clickenrent.supportservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.supportservice.dto.ErrorCodeDTO;
import org.clickenrent.supportservice.entity.ErrorCode;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.ErrorCodeMapper;
import org.clickenrent.supportservice.repository.ErrorCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing ErrorCode entities.
 */
@Service
@RequiredArgsConstructor
public class ErrorCodeService {

    private final ErrorCodeRepository errorCodeRepository;
    private final ErrorCodeMapper errorCodeMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<ErrorCodeDTO> getAll() {
        return errorCodeRepository.findAll().stream()
                .map(errorCodeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ErrorCodeDTO getById(Long id) {
        ErrorCode entity = errorCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ErrorCode", "id", id));
        return errorCodeMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public ErrorCodeDTO getByExternalId(String externalId) {
        ErrorCode entity = errorCodeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("ErrorCode", "externalId", externalId));
        return errorCodeMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ErrorCodeDTO> getByBikeEngineId(Long bikeEngineId) {
        return errorCodeRepository.findByBikeEngineId(bikeEngineId).stream()
                .map(errorCodeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ErrorCodeDTO create(ErrorCodeDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create error codes");
        }

        ErrorCode entity = errorCodeMapper.toEntity(dto);
        entity = errorCodeRepository.save(entity);
        return errorCodeMapper.toDto(entity);
    }

    @Transactional
    public ErrorCodeDTO update(Long id, ErrorCodeDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can update error codes");
        }

        ErrorCode entity = errorCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ErrorCode", "id", id));

        errorCodeMapper.updateEntityFromDto(dto, entity);
        entity = errorCodeRepository.save(entity);
        return errorCodeMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete error codes");
        }

        ErrorCode entity = errorCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ErrorCode", "id", id));
        errorCodeRepository.delete(entity);
    }
}

