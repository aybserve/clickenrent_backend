package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.StockMovementDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.Product;
import org.clickenrent.rentalservice.entity.StockMovement;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.StockMovementMapper;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private StockMovementMapper stockMovementMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private StockMovementService stockMovementService;

    private StockMovement testMovement;
    private StockMovementDTO testMovementDTO;
    private Hub testFromHub;
    private Hub testToHub;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testFromHub = Hub.builder()
        .id(1L)
        .build();

        testToHub = Hub.builder()
        .id(2L)
        .build();

        // Note: Product is abstract, so in a real test you'd use a concrete subclass
        // For this test, we'll use a mock or assume a concrete implementation
        testProduct = mock(Product.class);
        when(testProduct.getId()).thenReturn(1L);

        testMovement = StockMovement.builder()
        .id(1L)
        .externalId("SM001")
        .product(testProduct)
        .fromHub(testFromHub)
        .toHub(testToHub)
        .build();

        testMovementDTO = StockMovementDTO.builder()
        .id(1L)
        .externalId("SM001")
        .productId(1L)
        .fromHubId(1L)
        .toHubId(2L)
        .build();

    }

    @Test
    void getAllStockMovements_ReturnsAll() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<StockMovement> movementPage = new PageImpl<>(Collections.singletonList(testMovement));
        when(stockMovementRepository.findAll(pageable)).thenReturn(movementPage);
        when(stockMovementMapper.toDto(testMovement)).thenReturn(testMovementDTO);

        Page<StockMovementDTO> result = stockMovementService.getAllStockMovements(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(stockMovementRepository, times(1)).findAll(pageable);
    }

    @Test
    void getStockMovementsByProduct_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(stockMovementRepository.findByProductId(1L)).thenReturn(Collections.singletonList(testMovement));
        when(stockMovementMapper.toDto(testMovement)).thenReturn(testMovementDTO);

        List<StockMovementDTO> result = stockMovementService.getStockMovementsByProduct(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getProductId());
        verify(stockMovementRepository, times(1)).findByProductId(1L);
    }

    @Test
    void getStockMovementById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(testMovement));
        when(stockMovementMapper.toDto(testMovement)).thenReturn(testMovementDTO);

        StockMovementDTO result = stockMovementService.getStockMovementById(1L);

        assertNotNull(result);
        assertEquals("SM001", result.getExternalId());
        verify(stockMovementRepository, times(1)).findById(1L);
    }

    @Test
    void getStockMovementById_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(stockMovementRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockMovementService.getStockMovementById(999L));
    }

    @Test
    void createStockMovement_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubRepository.findById(1L)).thenReturn(Optional.of(testFromHub));
        when(hubRepository.findById(2L)).thenReturn(Optional.of(testToHub));
        when(stockMovementMapper.toEntity(testMovementDTO)).thenReturn(testMovement);
        when(stockMovementRepository.save(any())).thenReturn(testMovement);
        when(stockMovementMapper.toDto(testMovement)).thenReturn(testMovementDTO);

        StockMovementDTO result = stockMovementService.createStockMovement(testMovementDTO);

        assertNotNull(result);
        verify(hubRepository, times(1)).findById(1L);
        verify(hubRepository, times(1)).findById(2L);
        verify(stockMovementRepository, times(1)).save(any());
    }

    @Test
    void deleteStockMovement_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(stockMovementRepository.findById(1L)).thenReturn(Optional.of(testMovement));
        doNothing().when(stockMovementRepository).delete(testMovement);

        stockMovementService.deleteStockMovement(1L);

        verify(stockMovementRepository, times(1)).delete(testMovement);
    }

    @Test
    void deleteStockMovement_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(stockMovementRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> stockMovementService.deleteStockMovement(999L));
    }
}








