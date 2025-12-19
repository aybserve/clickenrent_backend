package org.clickenrent.paymentservice.service;

import org.clickenrent.contracts.auth.UserDTO;
import org.clickenrent.paymentservice.client.AuthServiceClient;
import org.clickenrent.paymentservice.dto.UserPaymentProfileDTO;
import org.clickenrent.paymentservice.entity.UserPaymentProfile;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.UserPaymentProfileMapper;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserPaymentProfileServiceTest {

    @Mock
    private UserPaymentProfileRepository userPaymentProfileRepository;

    @Mock
    private UserPaymentProfileMapper userPaymentProfileMapper;

    @Mock
    private SecurityService securityService;

    @Mock
    private StripeService stripeService;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private UserPaymentProfileService userPaymentProfileService;

    private UserPaymentProfile testProfile;
    private UserPaymentProfileDTO testProfileDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();

        testProfile = UserPaymentProfile.builder()
                .id(1L)
                .externalId(testExternalId)
                .userId(1L)
                .stripeCustomerId("cus_test123")
                .isActive(true)
                .build();

        testProfileDTO = UserPaymentProfileDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .userId(1L)
                .stripeCustomerId("cus_test123")
                .isActive(true)
                .build();

        lenient().when(securityService.isAdmin()).thenReturn(true);
        lenient().when(securityService.hasAccessToUser(anyLong())).thenReturn(true);
        lenient().when(securityService.getCurrentUserId()).thenReturn(1L);
    }

    @Test
    void findAll_AsAdmin_ReturnsAllProfiles() {
        when(userPaymentProfileRepository.findAll()).thenReturn(Arrays.asList(testProfile));
        when(userPaymentProfileMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testProfileDTO));

        List<UserPaymentProfileDTO> result = userPaymentProfileService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userPaymentProfileRepository, times(1)).findAll();
    }

    @Test
    void findAll_AsNonAdmin_ThrowsUnauthorized() {
        when(securityService.isAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userPaymentProfileService.findAll());
    }

    @Test
    void findById_Success() {
        when(userPaymentProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(userPaymentProfileMapper.toDTO(testProfile)).thenReturn(testProfileDTO);

        UserPaymentProfileDTO result = userPaymentProfileService.findById(1L);

        assertNotNull(result);
        assertEquals("cus_test123", result.getStripeCustomerId());
        verify(userPaymentProfileRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(userPaymentProfileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPaymentProfileService.findById(999L));
    }

    @Test
    void findByUserId_Success() {
        when(userPaymentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userPaymentProfileMapper.toDTO(testProfile)).thenReturn(testProfileDTO);

        UserPaymentProfileDTO result = userPaymentProfileService.findByUserId(1L);

        assertNotNull(result);
        verify(userPaymentProfileRepository, times(1)).findByUserId(1L);
    }

    @Test
    void findByUserId_NotFound() {
        when(userPaymentProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPaymentProfileService.findByUserId(999L));
    }

    @Test
    void createOrGetProfile_ExistingProfile_ReturnsExisting() {
        when(userPaymentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userPaymentProfileMapper.toDTO(testProfile)).thenReturn(testProfileDTO);

        UserPaymentProfileDTO result = userPaymentProfileService.createOrGetProfile(1L);

        assertNotNull(result);
        verify(stripeService, never()).createCustomer(anyLong(), anyString());
    }

    @Test
    void createOrGetProfile_NewProfile_CreatesProfile() {
        UserDTO userDTO = UserDTO.builder().id(1L).email("test@example.com").build();
        
        when(userPaymentProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(authServiceClient.getUserById(1L)).thenReturn(userDTO);
        when(stripeService.createCustomer(1L, "test@example.com")).thenReturn("cus_new123");
        when(userPaymentProfileRepository.save(any(UserPaymentProfile.class))).thenReturn(testProfile);
        when(userPaymentProfileMapper.toDTO(testProfile)).thenReturn(testProfileDTO);

        UserPaymentProfileDTO result = userPaymentProfileService.createOrGetProfile(1L);

        assertNotNull(result);
        verify(stripeService, times(1)).createCustomer(1L, "test@example.com");
        verify(userPaymentProfileRepository, times(1)).save(any(UserPaymentProfile.class));
    }

    @Test
    void create_Success() {
        UserDTO userDTO = UserDTO.builder().id(1L).email("test@example.com").build();
        
        when(authServiceClient.getUserById(1L)).thenReturn(userDTO);
        when(userPaymentProfileMapper.toEntity(testProfileDTO)).thenReturn(testProfile);
        when(userPaymentProfileRepository.save(any(UserPaymentProfile.class))).thenReturn(testProfile);
        when(userPaymentProfileMapper.toDTO(testProfile)).thenReturn(testProfileDTO);

        UserPaymentProfileDTO result = userPaymentProfileService.create(testProfileDTO);

        assertNotNull(result);
        verify(authServiceClient, times(1)).getUserById(1L);
        verify(userPaymentProfileRepository, times(1)).save(any(UserPaymentProfile.class));
    }

    @Test
    void update_Success() {
        when(userPaymentProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(userPaymentProfileRepository.save(any(UserPaymentProfile.class))).thenReturn(testProfile);
        when(userPaymentProfileMapper.toDTO(testProfile)).thenReturn(testProfileDTO);

        UserPaymentProfileDTO result = userPaymentProfileService.update(1L, testProfileDTO);

        assertNotNull(result);
        verify(userPaymentProfileRepository, times(1)).save(any(UserPaymentProfile.class));
    }

    @Test
    void delete_Success() {
        when(userPaymentProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        doNothing().when(userPaymentProfileRepository).deleteById(1L);

        userPaymentProfileService.delete(1L);

        verify(userPaymentProfileRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_Unauthorized() {
        when(securityService.isAdmin()).thenReturn(false);
        when(userPaymentProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        assertThrows(UnauthorizedException.class, () -> userPaymentProfileService.delete(1L));
    }
}
