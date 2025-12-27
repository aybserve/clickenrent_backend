package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.ResponsiblePersonDTO;
import org.clickenrent.supportservice.entity.ResponsiblePerson;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.ResponsiblePersonMapper;
import org.clickenrent.supportservice.repository.ResponsiblePersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsiblePersonServiceTest {

    @Mock
    private ResponsiblePersonRepository responsiblePersonRepository;

    @Mock
    private ResponsiblePersonMapper responsiblePersonMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ResponsiblePersonService responsiblePersonService;

    private ResponsiblePerson testPerson;
    private ResponsiblePersonDTO testPersonDTO;

    @BeforeEach
    void setUp() {
        testPerson = ResponsiblePerson.builder()
                .id(1L)
                .name("John Mechanic")
                .build();

        testPersonDTO = ResponsiblePersonDTO.builder()
                .id(1L)
                .name("John Mechanic")
                .build();
    }

    @Test
    void getAll_ReturnsAllPersons() {
        when(responsiblePersonRepository.findAll()).thenReturn(Arrays.asList(testPerson));
        when(responsiblePersonMapper.toDto(testPerson)).thenReturn(testPersonDTO);

        List<ResponsiblePersonDTO> result = responsiblePersonService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Mechanic", result.get(0).getName());
        verify(responsiblePersonRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(responsiblePersonRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(responsiblePersonMapper.toDto(testPerson)).thenReturn(testPersonDTO);

        ResponsiblePersonDTO result = responsiblePersonService.getById(1L);

        assertNotNull(result);
        assertEquals("John Mechanic", result.getName());
        verify(responsiblePersonRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(responsiblePersonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> responsiblePersonService.getById(999L));
        verify(responsiblePersonRepository, times(1)).findById(999L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(responsiblePersonMapper.toEntity(testPersonDTO)).thenReturn(testPerson);
        when(responsiblePersonRepository.save(any(ResponsiblePerson.class))).thenReturn(testPerson);
        when(responsiblePersonMapper.toDto(testPerson)).thenReturn(testPersonDTO);

        ResponsiblePersonDTO result = responsiblePersonService.create(testPersonDTO);

        assertNotNull(result);
        assertEquals("John Mechanic", result.getName());
        verify(responsiblePersonRepository, times(1)).save(any(ResponsiblePerson.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> responsiblePersonService.create(testPersonDTO));
        verify(responsiblePersonRepository, never()).save(any(ResponsiblePerson.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(responsiblePersonRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        doNothing().when(responsiblePersonMapper).updateEntityFromDto(testPersonDTO, testPerson);
        when(responsiblePersonRepository.save(any(ResponsiblePerson.class))).thenReturn(testPerson);
        when(responsiblePersonMapper.toDto(testPerson)).thenReturn(testPersonDTO);

        ResponsiblePersonDTO result = responsiblePersonService.update(1L, testPersonDTO);

        assertNotNull(result);
        assertEquals("John Mechanic", result.getName());
        verify(responsiblePersonRepository, times(1)).save(any(ResponsiblePerson.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(responsiblePersonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> responsiblePersonService.update(999L, testPersonDTO));
        verify(responsiblePersonRepository, never()).save(any(ResponsiblePerson.class));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> responsiblePersonService.update(1L, testPersonDTO));
        verify(responsiblePersonRepository, never()).findById(anyLong());
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(responsiblePersonRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        doNothing().when(responsiblePersonRepository).delete(testPerson);

        responsiblePersonService.delete(1L);

        verify(responsiblePersonRepository, times(1)).findById(1L);
        verify(responsiblePersonRepository, times(1)).delete(testPerson);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(responsiblePersonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> responsiblePersonService.delete(999L));
        verify(responsiblePersonRepository, never()).delete(any(ResponsiblePerson.class));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> responsiblePersonService.delete(1L));
        verify(responsiblePersonRepository, never()).findById(anyLong());
    }
}







