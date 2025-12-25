package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.ServiceProviderDTO;
import org.clickenrent.paymentservice.entity.ServiceProvider;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.ServiceProviderMapper;
import org.clickenrent.paymentservice.repository.ServiceProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceProviderServiceTest {

    @Mock
    private ServiceProviderRepository serviceProviderRepository;

    @Mock
    private ServiceProviderMapper serviceProviderMapper;

    @InjectMocks
    private ServiceProviderService serviceProviderService;

    private ServiceProvider testServiceProvider;
    private ServiceProviderDTO testServiceProviderDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();
        
        testServiceProvider = ServiceProvider.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("STRIPE")
                .name("Stripe Payment Gateway")
                .build();

        testServiceProviderDTO = ServiceProviderDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("STRIPE")
                .name("Stripe Payment Gateway")
                .build();
    }

    @Test
    void findAll_ReturnsAllProviders() {
        when(serviceProviderRepository.findAll()).thenReturn(Arrays.asList(testServiceProvider));
        when(serviceProviderMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testServiceProviderDTO));

        List<ServiceProviderDTO> result = serviceProviderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("STRIPE", result.get(0).getCode());
        verify(serviceProviderRepository, times(1)).findAll();
    }

    @Test
    void findById_Success() {
        when(serviceProviderRepository.findById(1L)).thenReturn(Optional.of(testServiceProvider));
        when(serviceProviderMapper.toDTO(testServiceProvider)).thenReturn(testServiceProviderDTO);

        ServiceProviderDTO result = serviceProviderService.findById(1L);

        assertNotNull(result);
        assertEquals("STRIPE", result.getCode());
        assertEquals("Stripe Payment Gateway", result.getName());
        verify(serviceProviderRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(serviceProviderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceProviderService.findById(999L));
        verify(serviceProviderRepository, times(1)).findById(999L);
    }

    @Test
    void findByExternalId_Success() {
        when(serviceProviderRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testServiceProvider));
        when(serviceProviderMapper.toDTO(testServiceProvider)).thenReturn(testServiceProviderDTO);

        ServiceProviderDTO result = serviceProviderService.findByExternalId(testExternalId);

        assertNotNull(result);
        assertEquals("STRIPE", result.getCode());
        verify(serviceProviderRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        String randomId = UUID.randomUUID().toString();
        when(serviceProviderRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceProviderService.findByExternalId(randomId));
    }

    @Test
    void findByCode_Success() {
        when(serviceProviderRepository.findByCode("STRIPE")).thenReturn(Optional.of(testServiceProvider));
        when(serviceProviderMapper.toDTO(testServiceProvider)).thenReturn(testServiceProviderDTO);

        ServiceProviderDTO result = serviceProviderService.findByCode("STRIPE");

        assertNotNull(result);
        assertEquals("STRIPE", result.getCode());
        verify(serviceProviderRepository, times(1)).findByCode("STRIPE");
    }

    @Test
    void findByCode_NotFound() {
        when(serviceProviderRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceProviderService.findByCode("UNKNOWN"));
    }

    @Test
    void create_Success() {
        when(serviceProviderRepository.findByCode("STRIPE")).thenReturn(Optional.empty());
        when(serviceProviderMapper.toEntity(testServiceProviderDTO)).thenReturn(testServiceProvider);
        when(serviceProviderRepository.save(any(ServiceProvider.class))).thenReturn(testServiceProvider);
        when(serviceProviderMapper.toDTO(testServiceProvider)).thenReturn(testServiceProviderDTO);

        ServiceProviderDTO result = serviceProviderService.create(testServiceProviderDTO);

        assertNotNull(result);
        assertEquals("STRIPE", result.getCode());
        verify(serviceProviderRepository, times(1)).save(any(ServiceProvider.class));
    }

    @Test
    void create_DuplicateCode_ThrowsException() {
        when(serviceProviderRepository.findByCode("STRIPE")).thenReturn(Optional.of(testServiceProvider));

        assertThrows(DuplicateResourceException.class, () -> serviceProviderService.create(testServiceProviderDTO));
        verify(serviceProviderRepository, never()).save(any(ServiceProvider.class));
    }

    @Test
    void update_Success() {
        when(serviceProviderRepository.findById(1L)).thenReturn(Optional.of(testServiceProvider));
        when(serviceProviderRepository.save(any(ServiceProvider.class))).thenReturn(testServiceProvider);
        when(serviceProviderMapper.toDTO(testServiceProvider)).thenReturn(testServiceProviderDTO);

        ServiceProviderDTO result = serviceProviderService.update(1L, testServiceProviderDTO);

        assertNotNull(result);
        assertEquals("STRIPE", result.getCode());
        verify(serviceProviderRepository, times(1)).findById(1L);
        verify(serviceProviderRepository, times(1)).save(any(ServiceProvider.class));
    }

    @Test
    void update_NotFound() {
        when(serviceProviderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceProviderService.update(999L, testServiceProviderDTO));
        verify(serviceProviderRepository, never()).save(any(ServiceProvider.class));
    }

    @Test
    void update_DuplicateCode_ThrowsException() {
        ServiceProvider existingProvider = ServiceProvider.builder()
                .id(1L)
                .code("PAYPAL")
                .name("PayPal")
                .build();
        
        when(serviceProviderRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        when(serviceProviderRepository.findByCode("STRIPE")).thenReturn(Optional.of(testServiceProvider));

        assertThrows(DuplicateResourceException.class, () -> serviceProviderService.update(1L, testServiceProviderDTO));
        verify(serviceProviderRepository, never()).save(any(ServiceProvider.class));
    }

    @Test
    void delete_Success() {
        when(serviceProviderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(serviceProviderRepository).deleteById(1L);

        serviceProviderService.delete(1L);

        verify(serviceProviderRepository, times(1)).existsById(1L);
        verify(serviceProviderRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(serviceProviderRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> serviceProviderService.delete(999L));
        verify(serviceProviderRepository, never()).deleteById(anyLong());
    }
}






