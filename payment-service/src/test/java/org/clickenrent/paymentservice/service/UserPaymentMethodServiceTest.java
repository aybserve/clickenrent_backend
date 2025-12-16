package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.UserPaymentMethodDTO;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.entity.UserPaymentMethod;
import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.UserPaymentMethodMapper;
import org.clickenrent.paymentservice.repository.UserPaymentMethodRepository;
import org.clickenrent.paymentservice.repository.UserPaymentProfileRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserPaymentMethodServiceTest {

    @Mock
    private UserPaymentMethodRepository userPaymentMethodRepository;

    @Mock
    private UserPaymentProfileRepository userPaymentProfileRepository;

    @Mock
    private UserPaymentMethodMapper userPaymentMethodMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private UserPaymentMethodService userPaymentMethodService;

    private UserPaymentMethod testMethod;
    private UserPaymentMethodDTO testMethodDTO;
    private UserPaymentProfile testProfile;
    private UUID testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID();

        testProfile = UserPaymentProfile.builder()
                .id(1L)
                .userId(1L)
                .stripeCustomerId("cus_test123")
                .build();

        testMethod = UserPaymentMethod.builder()
                .id(1L)
                .externalId(testExternalId)
                .userPaymentProfile(testProfile)
                .stripePaymentMethodId("pm_test123")
                .isDefault(false)
                .isActive(true)
                .build();

        testMethodDTO = UserPaymentMethodDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .userPaymentProfile(UserPaymentProfileDTO.builder().id(1L).build())
                .stripePaymentMethodId("pm_test123")
                .isDefault(false)
                .isActive(true)
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
        lenient().when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        lenient().when(securityService.getCurrentUserId()).thenReturn(1L);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllMethods() {
        when(userPaymentMethodRepository.findAll()).thenReturn(Arrays.asList(testMethod));
        when(userPaymentMethodMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testMethodDTO));

        List<UserPaymentMethodDTO> result = userPaymentMethodService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userPaymentMethodRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsNonAdmin_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userPaymentMethodService.findAll());
    }

    @Test
    void findById_Success() {
        when(userPaymentMethodRepository.findById(1L)).thenReturn(Optional.of(testMethod));
        when(userPaymentMethodMapper.toDTO(testMethod)).thenReturn(testMethodDTO);

        UserPaymentMethodDTO result = userPaymentMethodService.findById(1L);

        assertNotNull(result);
        assertEquals("pm_test123", result.getStripePaymentMethodId());
        verify(userPaymentMethodRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(userPaymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPaymentMethodService.findById(999L));
    }

    @Test
    void findByUserId_Success() {
        when(userPaymentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userPaymentMethodRepository.findByUserPaymentProfileId(1L)).thenReturn(Arrays.asList(testMethod));
        when(userPaymentMethodMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testMethodDTO));

        List<UserPaymentMethodDTO> result = userPaymentMethodService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void attachPaymentMethod_Success() {
        when(userPaymentProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(stripeService.attachPaymentMethod("pm_test123", "cus_test123")).thenReturn("pm_test123");
        when(userPaymentMethodRepository.save(any(UserPaymentMethod.class))).thenReturn(testMethod);
        when(userPaymentMethodMapper.toDTO(testMethod)).thenReturn(testMethodDTO);

        UserPaymentMethodDTO result = userPaymentMethodService.attachPaymentMethod(1L, "pm_test123");

        assertNotNull(result);
        verify(stripeService, times(1)).attachPaymentMethod("pm_test123", "cus_test123");
        verify(userPaymentMethodRepository, times(1)).save(any(UserPaymentMethod.class));
    }

    @Test
    void setDefaultPaymentMethod_Success() {
        when(userPaymentMethodRepository.findById(1L)).thenReturn(Optional.of(testMethod));
        when(userPaymentMethodRepository.findByUserPaymentProfileId(1L)).thenReturn(Arrays.asList(testMethod));
        when(userPaymentMethodRepository.save(any(UserPaymentMethod.class))).thenReturn(testMethod);
        when(userPaymentMethodMapper.toDTO(testMethod)).thenReturn(testMethodDTO);

        UserPaymentMethodDTO result = userPaymentMethodService.setDefaultPaymentMethod(1L);

        assertNotNull(result);
        verify(userPaymentMethodRepository, atLeast(1)).save(any(UserPaymentMethod.class));
    }

    @Test
    void delete_Success() {
        when(userPaymentMethodRepository.findById(1L)).thenReturn(Optional.of(testMethod));
        doNothing().when(userPaymentMethodRepository).deleteById(1L);

        userPaymentMethodService.delete(1L);

        verify(userPaymentMethodRepository, times(1)).deleteById(1L);
    }
}
