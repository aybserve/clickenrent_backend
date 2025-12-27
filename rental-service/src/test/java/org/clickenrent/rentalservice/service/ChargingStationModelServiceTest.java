package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.ChargingStationModelDTO;
import org.clickenrent.rentalservice.entity.ChargingStationModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.ChargingStationModelMapper;
import org.clickenrent.rentalservice.repository.ChargingStationModelRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargingStationModelServiceTest {

    @Mock
    private ChargingStationModelRepository chargingStationModelRepository;

    @Mock
    private ChargingStationModelMapper chargingStationModelMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ChargingStationModelService chargingStationModelService;

    private ChargingStationModel testModel;
    private ChargingStationModelDTO testModelDTO;

    @BeforeEach
    void setUp() {
        testModel = ChargingStationModel.builder()
        .id(1L)
        .externalId("CSM001")
        .name("Wall Connector")
        .build();

        testModelDTO = ChargingStationModelDTO.builder()
        .id(1L)
        .externalId("CSM001")
        .name("Wall Connector")
        .chargingStationBrandId(1L)
        .build();

    }

    @Test
    void getAllModels_ReturnsAllModels() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ChargingStationModel> modelPage = new PageImpl<>(Collections.singletonList(testModel));
        when(chargingStationModelRepository.findAll(pageable)).thenReturn(modelPage);
        when(chargingStationModelMapper.toDto(testModel)).thenReturn(testModelDTO);

        Page<ChargingStationModelDTO> result = chargingStationModelService.getAllModels(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(chargingStationModelRepository, times(1)).findAll(pageable);
    }

    @Test
    void getModelById_Success() {
        when(chargingStationModelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(chargingStationModelMapper.toDto(testModel)).thenReturn(testModelDTO);

        ChargingStationModelDTO result = chargingStationModelService.getModelById(1L);

        assertNotNull(result);
        assertEquals("Wall Connector", result.getName());
        verify(chargingStationModelRepository, times(1)).findById(1L);
    }

    @Test
    void getModelById_NotFound() {
        when(chargingStationModelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chargingStationModelService.getModelById(999L));
    }

    @Test
    void createModel_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationModelMapper.toEntity(testModelDTO)).thenReturn(testModel);
        when(chargingStationModelRepository.save(any())).thenReturn(testModel);
        when(chargingStationModelMapper.toDto(testModel)).thenReturn(testModelDTO);

        ChargingStationModelDTO result = chargingStationModelService.createModel(testModelDTO);

        assertNotNull(result);
        verify(chargingStationModelRepository, times(1)).save(any());
    }

    @Test
    void updateModel_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationModelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        doNothing().when(chargingStationModelMapper).updateEntityFromDto(testModelDTO, testModel);
        when(chargingStationModelRepository.save(any())).thenReturn(testModel);
        when(chargingStationModelMapper.toDto(testModel)).thenReturn(testModelDTO);

        ChargingStationModelDTO result = chargingStationModelService.updateModel(1L, testModelDTO);

        assertNotNull(result);
        verify(chargingStationModelMapper, times(1)).updateEntityFromDto(testModelDTO, testModel);
        verify(chargingStationModelRepository, times(1)).save(any());
    }

    @Test
    void deleteModel_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationModelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        doNothing().when(chargingStationModelRepository).delete(testModel);

        chargingStationModelService.deleteModel(1L);

        verify(chargingStationModelRepository, times(1)).delete(testModel);
    }

    @Test
    void deleteModel_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(chargingStationModelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chargingStationModelService.deleteModel(999L));
    }
}







