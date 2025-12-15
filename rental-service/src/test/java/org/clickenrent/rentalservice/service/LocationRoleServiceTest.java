package org.clickenrent.rentalservice.service;

import org.clickenrent.rentalservice.dto.LocationRoleDTO;
import org.clickenrent.rentalservice.entity.LocationRole;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.mapper.LocationRoleMapper;
import org.clickenrent.rentalservice.repository.LocationRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationRoleServiceTest {

    @Mock
    private LocationRoleRepository locationRoleRepository;

    @Mock
    private LocationRoleMapper locationRoleMapper;

    @InjectMocks
    private LocationRoleService locationRoleService;

    private LocationRole testRole;
    private LocationRoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        testRole = LocationRole.builder()
                .id(1L)
                .name("Admin")
                .build();

        testRoleDTO = LocationRoleDTO.builder()
                .id(1L)
                .name("Admin")
                .build();
    }

    @Test
    void getAllLocationRoles_ReturnsAllRoles() {
        when(locationRoleRepository.findAll()).thenReturn(Arrays.asList(testRole));
        when(locationRoleMapper.toDto(testRole)).thenReturn(testRoleDTO);

        var result = locationRoleService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(locationRoleRepository, times(1)).findAll();
    }

    @Test
    void getLocationRoleById_Success() {
        when(locationRoleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(locationRoleMapper.toDto(testRole)).thenReturn(testRoleDTO);

        LocationRoleDTO result = locationRoleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals("Admin", result.getName());
        verify(locationRoleRepository, times(1)).findById(1L);
    }

    @Test
    void getLocationRoleById_NotFound() {
        when(locationRoleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationRoleService.getRoleById(999L));
    }

    @Test
    void createLocationRole_Success() {
        when(locationRoleMapper.toEntity(testRoleDTO)).thenReturn(testRole);
        when(locationRoleRepository.save(any())).thenReturn(testRole);
        when(locationRoleMapper.toDto(testRole)).thenReturn(testRoleDTO);

        LocationRoleDTO result = locationRoleService.createLocationRole(testRoleDTO);

        assertNotNull(result);
        verify(locationRoleRepository, times(1)).save(any());
    }

    @Test
    void updateLocationRole_Success() {
        when(locationRoleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(locationRoleRepository.save(any())).thenReturn(testRole);
        when(locationRoleMapper.toDto(testRole)).thenReturn(testRoleDTO);

        LocationRoleDTO result = locationRoleService.updateLocationRole(1L, testRoleDTO);

        assertNotNull(result);
        verify(locationRoleRepository, times(1)).save(any());
    }

    @Test
    void deleteLocationRole_Success() {
        when(locationRoleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        doNothing().when(locationRoleRepository).delete(testRole);

        locationRoleService.deleteLocationRole(1L);

        verify(locationRoleRepository, times(1)).delete(testRole);
    }
}
