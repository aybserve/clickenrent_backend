package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.StockMovementDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.StockMovement;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.StockMovementMapper;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing StockMovement entities.
 * Tracks product movements between hubs.
 */
@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final HubRepository hubRepository;
    private final StockMovementMapper stockMovementMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<StockMovementDTO> getAllStockMovements(Pageable pageable) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view all stock movements");
        }

        return stockMovementRepository.findAll(pageable)
                .map(stockMovementMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getStockMovementsByProduct(Long productId) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view stock movements");
        }

        return stockMovementRepository.findByProductId(productId).stream()
                .map(stockMovementMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockMovementDTO getStockMovementById(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can view stock movements");
        }

        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));
        return stockMovementMapper.toDto(stockMovement);
    }

    @Transactional
    public StockMovementDTO createStockMovement(StockMovementDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can create stock movements");
        }

        // Validate hubs exist
        Hub fromHub = hubRepository.findById(dto.getFromHubId())
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", dto.getFromHubId()));
        Hub toHub = hubRepository.findById(dto.getToHubId())
                .orElseThrow(() -> new ResourceNotFoundException("Hub", "id", dto.getToHubId()));

        StockMovement stockMovement = stockMovementMapper.toEntity(dto);
        if (stockMovement.getDateTime() == null) {
            stockMovement.setDateTime(LocalDateTime.now());
        }
        
        stockMovement = stockMovementRepository.save(stockMovement);
        return stockMovementMapper.toDto(stockMovement);
    }

    @Transactional
    public void deleteStockMovement(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete stock movements");
        }

        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));
        stockMovementRepository.delete(stockMovement);
    }
}






