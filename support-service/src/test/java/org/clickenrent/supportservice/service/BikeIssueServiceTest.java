package org.clickenrent.supportservice.service;

import org.clickenrent.supportservice.dto.BikeIssueDTO;
import org.clickenrent.supportservice.entity.BikeIssue;
import org.clickenrent.supportservice.entity.ResponsiblePerson;
import org.clickenrent.supportservice.exception.ResourceNotFoundException;
import org.clickenrent.supportservice.exception.UnauthorizedException;
import org.clickenrent.supportservice.mapper.BikeIssueMapper;
import org.clickenrent.supportservice.repository.BikeIssueRepository;
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
class BikeIssueServiceTest {

    @Mock
    private BikeIssueRepository bikeIssueRepository;

    @Mock
    private BikeIssueMapper bikeIssueMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private BikeIssueService bikeIssueService;

    private BikeIssue testRootIssue;
    private BikeIssue testSubIssue;
    private BikeIssueDTO testRootIssueDTO;
    private BikeIssueDTO testSubIssueDTO;
    private ResponsiblePerson testPerson;

    @BeforeEach
    void setUp() {
        testPerson = ResponsiblePerson.builder()
                .id(1L)
                .name("John Mechanic")
                .build();

        testRootIssue = BikeIssue.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440101")
                .name("Battery Issues")
                .description("Problems related to bike battery")
                .parentBikeIssue(null)
                .isFixableByClient(false)
                .responsiblePerson(testPerson)
                .build();

        testSubIssue = BikeIssue.builder()
                .id(4L)
                .externalId("550e8400-e29b-41d4-a716-446655440104")
                .name("Battery Dead")
                .description("Battery completely discharged")
                .parentBikeIssue(testRootIssue)
                .isFixableByClient(true)
                .responsiblePerson(testPerson)
                .build();

        testRootIssueDTO = BikeIssueDTO.builder()
                .id(1L)
                .externalId("550e8400-e29b-41d4-a716-446655440101")
                .name("Battery Issues")
                .description("Problems related to bike battery")
                .isFixableByClient(false)
                .build();

        testSubIssueDTO = BikeIssueDTO.builder()
                .id(4L)
                .externalId("550e8400-e29b-41d4-a716-446655440104")
                .name("Battery Dead")
                .description("Battery completely discharged")
                .isFixableByClient(true)
                .build();
    }

    @Test
    void getAll_ReturnsAllIssues() {
        when(bikeIssueRepository.findAll()).thenReturn(Arrays.asList(testRootIssue, testSubIssue));
        when(bikeIssueMapper.toDto(testRootIssue)).thenReturn(testRootIssueDTO);
        when(bikeIssueMapper.toDto(testSubIssue)).thenReturn(testSubIssueDTO);

        List<BikeIssueDTO> result = bikeIssueService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bikeIssueRepository, times(1)).findAll();
    }

    @Test
    void getById_Success() {
        when(bikeIssueRepository.findById(1L)).thenReturn(Optional.of(testRootIssue));
        when(bikeIssueMapper.toDto(testRootIssue)).thenReturn(testRootIssueDTO);

        BikeIssueDTO result = bikeIssueService.getById(1L);

        assertNotNull(result);
        assertEquals("Battery Issues", result.getName());
        verify(bikeIssueRepository, times(1)).findById(1L);
    }

    @Test
    void getById_NotFound() {
        when(bikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeIssueService.getById(999L));
    }

    @Test
    void getByExternalId_Success() {
        when(bikeIssueRepository.findByExternalId("550e8400-e29b-41d4-a716-446655440101"))
                .thenReturn(Optional.of(testRootIssue));
        when(bikeIssueMapper.toDto(testRootIssue)).thenReturn(testRootIssueDTO);

        BikeIssueDTO result = bikeIssueService.getByExternalId("550e8400-e29b-41d4-a716-446655440101");

        assertNotNull(result);
        assertEquals("Battery Issues", result.getName());
    }

    @Test
    void getByExternalId_NotFound() {
        when(bikeIssueRepository.findByExternalId("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeIssueService.getByExternalId("invalid"));
    }

    @Test
    void getRootIssues_ReturnsOnlyRootIssues() {
        when(bikeIssueRepository.findByParentBikeIssueIsNull()).thenReturn(Arrays.asList(testRootIssue));
        when(bikeIssueMapper.toDto(testRootIssue)).thenReturn(testRootIssueDTO);

        List<BikeIssueDTO> result = bikeIssueService.getRootIssues();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Battery Issues", result.get(0).getName());
        verify(bikeIssueRepository, times(1)).findByParentBikeIssueIsNull();
    }

    @Test
    void getSubIssues_ReturnsSubIssuesForParent() {
        when(bikeIssueRepository.findByParentBikeIssueId(1L)).thenReturn(Arrays.asList(testSubIssue));
        when(bikeIssueMapper.toDto(testSubIssue)).thenReturn(testSubIssueDTO);

        List<BikeIssueDTO> result = bikeIssueService.getSubIssues(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Battery Dead", result.get(0).getName());
        verify(bikeIssueRepository, times(1)).findByParentBikeIssueId(1L);
    }

    @Test
    void create_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeIssueMapper.toEntity(testRootIssueDTO)).thenReturn(testRootIssue);
        when(bikeIssueRepository.save(any(BikeIssue.class))).thenReturn(testRootIssue);
        when(bikeIssueMapper.toDto(testRootIssue)).thenReturn(testRootIssueDTO);

        BikeIssueDTO result = bikeIssueService.create(testRootIssueDTO);

        assertNotNull(result);
        assertEquals("Battery Issues", result.getName());
        verify(bikeIssueRepository, times(1)).save(any(BikeIssue.class));
    }

    @Test
    void create_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeIssueService.create(testRootIssueDTO));
        verify(bikeIssueRepository, never()).save(any(BikeIssue.class));
    }

    @Test
    void update_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeIssueRepository.findById(1L)).thenReturn(Optional.of(testRootIssue));
        doNothing().when(bikeIssueMapper).updateEntityFromDto(testRootIssueDTO, testRootIssue);
        when(bikeIssueRepository.save(any(BikeIssue.class))).thenReturn(testRootIssue);
        when(bikeIssueMapper.toDto(testRootIssue)).thenReturn(testRootIssueDTO);

        BikeIssueDTO result = bikeIssueService.update(1L, testRootIssueDTO);

        assertNotNull(result);
        verify(bikeIssueRepository, times(1)).save(any(BikeIssue.class));
    }

    @Test
    void update_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeIssueService.update(999L, testRootIssueDTO));
    }

    @Test
    void update_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeIssueService.update(1L, testRootIssueDTO));
    }

    @Test
    void delete_Success() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeIssueRepository.findById(1L)).thenReturn(Optional.of(testRootIssue));
        doNothing().when(bikeIssueRepository).delete(testRootIssue);

        bikeIssueService.delete(1L);

        verify(bikeIssueRepository, times(1)).delete(testRootIssue);
    }

    @Test
    void delete_NotFound() {
        when(securityService.isAdmin()).thenReturn(true);
        when(bikeIssueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bikeIssueService.delete(999L));
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> bikeIssueService.delete(1L));
    }
}








