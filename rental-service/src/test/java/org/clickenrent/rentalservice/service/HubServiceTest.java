package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.HubDTO;
import org.clickenrent.rentalservice.entity.Hub;
import org.clickenrent.rentalservice.entity.Location;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.HubMapper;
import org.clickenrent.rentalservice.repository.HubRepository;
import org.clickenrent.rentalservice.repository.LocationRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private HubMapper hubMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private HubService hubService;

    private Hub testHub;
    private HubDTO testHubDTO;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder().id(1L).companyId(1L).build();
        
        testHub = Hub.builder()
        .id(1L)
        .externalId("HUB001")
        .name("Main Hub")
        .location(testLocation)
        .build();

        testHubDTO = HubDTO.builder()
        .id(1L)
        .externalId("HUB001")
        .name("Main Hub")
        .locationId(1L)
        .build();
        
            }

    @Test
    void getAllHubs_ReturnsAllHubs() {
        when(securityService.isAdmin()).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Hub> hubPage = new PageImpl<>(Collections.singletonList(testHub));
        when(hubRepository.findAll(pageable)).thenReturn(hubPage);
        when(hubMapper.toDto(testHub)).thenReturn(testHubDTO);

        Page<HubDTO> result = hubService.getAllHubs(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(hubRepository, times(1)).findAll(pageable);
    }

    @Test
    void getHubsByLocation_ReturnsLocationHubs() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(hubRepository.findByLocation(testLocation)).thenReturn(Arrays.asList(testHub));
        when(hubMapper.toDto(testHub)).thenReturn(testHubDTO);

        List<HubDTO> result = hubService.getHubsByLocation(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Main Hub", result.get(0).getName());
        verify(locationRepository, times(1)).findById(1L);
        verify(hubRepository, times(1)).findByLocation(testLocation);
    }

    @Test
    void getHubById_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubRepository.findById(1L)).thenReturn(Optional.of(testHub));
        when(hubMapper.toDto(testHub)).thenReturn(testHubDTO);

        HubDTO result = hubService.getHubById(1L);

        assertNotNull(result);
        assertEquals("Main Hub", result.getName());
        verify(hubRepository, times(1)).findById(1L);
    }

    @Test
    void getHubById_NotFound() {
        when(hubRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hubService.getHubById(999L));
    }

    @Test
    void createHub_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(hubMapper.toEntity(testHubDTO)).thenReturn(testHub);
        when(hubRepository.save(any(Hub.class))).thenReturn(testHub);
        when(hubMapper.toDto(testHub)).thenReturn(testHubDTO);

        HubDTO result = hubService.createHub(testHubDTO);

        assertNotNull(result);
        assertEquals("Main Hub", result.getName());
        verify(locationRepository, times(1)).findById(1L);
        verify(hubRepository, times(1)).save(any(Hub.class));
    }

    @Test
    void updateHub_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubRepository.findById(1L)).thenReturn(Optional.of(testHub));
        doNothing().when(hubMapper).updateEntityFromDto(testHubDTO, testHub);
        when(hubRepository.save(any(Hub.class))).thenReturn(testHub);
        when(hubMapper.toDto(testHub)).thenReturn(testHubDTO);

        HubDTO result = hubService.updateHub(1L, testHubDTO);

        assertNotNull(result);
        assertEquals("Main Hub", result.getName());
        verify(hubRepository, times(1)).findById(1L);
        verify(hubMapper, times(1)).updateEntityFromDto(testHubDTO, testHub);
        verify(hubRepository, times(1)).save(any(Hub.class));
    }

    @Test
    void updateHub_NotFound() {
        when(hubRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hubService.updateHub(999L, testHubDTO));
    }

    @Test
    void deleteHub_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(hubRepository.findById(1L)).thenReturn(Optional.of(testHub));
        doNothing().when(hubRepository).delete(testHub);

        hubService.deleteHub(1L);

        verify(hubRepository, times(1)).findById(1L);
        verify(hubRepository, times(1)).delete(testHub);
    }

    @Test
    void deleteHub_NotFound() {
        when(hubRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> hubService.deleteHub(999L));
    }
}


