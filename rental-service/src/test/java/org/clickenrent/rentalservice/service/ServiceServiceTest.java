package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.ServiceDTO;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.ServiceMapper;
import org.clickenrent.rentalservice.repository.ServiceRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceService serviceService;

    private org.clickenrent.rentalservice.entity.Service testService;
    private ServiceDTO testServiceDTO;

    @BeforeEach
    void setUp() {
        testService = org.clickenrent.rentalservice.entity.Service.builder()
                .id(1L)
                .name("Maintenance")
                .b2bSubscriptionPrice(new BigDecimal("50.00"))
                .build();

        testServiceDTO = ServiceDTO.builder()
                .id(1L)
                .name("Maintenance")
                .b2bSubscriptionPrice(new BigDecimal("50.00"))
                .build();
    }

    @Test
    void getAllServices_ReturnsAll() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<org.clickenrent.rentalservice.entity.Service> servicePage = new PageImpl<>(Collections.singletonList(testService));
        when(serviceRepository.findAll(pageable)).thenReturn(servicePage);
        when(serviceMapper.toDto(testService)).thenReturn(testServiceDTO);

        Page<ServiceDTO> result = serviceService.getAllServices(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getServiceById_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(serviceMapper.toDto(testService)).thenReturn(testServiceDTO);

        ServiceDTO result = serviceService.getServiceById(1L);

        assertNotNull(result);
        assertEquals("Maintenance", result.getName());
    }

    @Test
    void getServiceById_NotFound() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceService.getServiceById(999L));
    }

    @Test
    void createService_Success() {
        when(serviceMapper.toEntity(testServiceDTO)).thenReturn(testService);
        when(serviceRepository.save(any())).thenReturn(testService);
        when(serviceMapper.toDto(testService)).thenReturn(testServiceDTO);

        ServiceDTO result = serviceService.createService(testServiceDTO);

        assertNotNull(result);
    }

    @Test
    void updateService_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any())).thenReturn(testService);
        when(serviceMapper.toDto(testService)).thenReturn(testServiceDTO);

        ServiceDTO result = serviceService.updateService(1L, testServiceDTO);

        assertNotNull(result);
    }

    @Test
    void deleteService_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        doNothing().when(serviceRepository).delete(testService);

        serviceService.deleteService(1L);

        verify(serviceRepository, times(1)).delete(testService);
    }
}
