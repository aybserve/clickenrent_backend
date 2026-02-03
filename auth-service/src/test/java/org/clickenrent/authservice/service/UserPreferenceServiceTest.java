package org.clickenrent.authservice.service;

import org.clickenrent.authservice.dto.UpdateUserPreferenceRequest;
import org.clickenrent.authservice.dto.UserPreferenceDTO;
import org.clickenrent.authservice.entity.Language;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserPreference;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.UserPreferenceMapper;
import org.clickenrent.authservice.repository.LanguageRepository;
import org.clickenrent.authservice.repository.UserPreferenceRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserPreferenceService.
 */
@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPreferenceMapper userPreferenceMapper;

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private UserPreferenceService userPreferenceService;

    private User user;
    private UserPreference userPreference;
    private UserPreferenceDTO userPreferenceDTO;
    private Language language;

    @BeforeEach
    void setUp() {
        language = Language.builder()
                .id(1L)
                .name("English")
                .build();

        user = User.builder()
                .id(1L)
                .externalId("usr-ext-00001")
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .language(language)
                .build();

        Map<String, List<String>> navigationOrder = new HashMap<>();
        navigationOrder.put("customer", List.of("rentals", "bikes", "profile"));

        userPreference = UserPreference.builder()
                .id(1L)
                .user(user)
                .navigationOrder(navigationOrder)
                .theme(UserPreference.Theme.SYSTEM)
                .language(language)
                .timezone("UTC")
                .dateFormat("YYYY-MM-DD")
                .timeFormat(UserPreference.TimeFormat.TWENTY_FOUR_HOUR)
                .currency("USD")
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .notificationFrequency(UserPreference.NotificationFrequency.IMMEDIATE)
                .itemsPerPage(20)
                .dashboardLayout(new HashMap<>())
                .tablePreferences(new HashMap<>())
                .defaultFilters(new HashMap<>())
                .build();

        userPreferenceDTO = UserPreferenceDTO.builder()
                .id(1L)
                .userId(1L)
                .userExternalId("usr-ext-00001")
                .navigationOrder(navigationOrder)
                .theme("system")
                .languageId(1L)
                .languageName("English")
                .timezone("UTC")
                .dateFormat("YYYY-MM-DD")
                .timeFormat("24h")
                .currency("USD")
                .emailNotifications(true)
                .pushNotifications(true)
                .smsNotifications(false)
                .notificationFrequency("immediate")
                .itemsPerPage(20)
                .dashboardLayout(new HashMap<>())
                .tablePreferences(new HashMap<>())
                .defaultFilters(new HashMap<>())
                .build();
    }

    @Test
    void getUserPreferences_WhenPreferencesExist_ShouldReturnPreferences() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(userPreference));
        when(userPreferenceMapper.toDto(userPreference)).thenReturn(userPreferenceDTO);

        // Act
        UserPreferenceDTO result = userPreferenceService.getUserPreferences(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTheme()).isEqualTo("system");
        verify(userRepository).findById(1L);
        verify(userPreferenceRepository).findByUserId(1L);
        verify(userPreferenceMapper).toDto(userPreference);
    }

    @Test
    void getUserPreferences_WhenPreferencesDoNotExist_ShouldCreateDefault() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(userPreference);
        when(userPreferenceMapper.toDto(userPreference)).thenReturn(userPreferenceDTO);

        // Act
        UserPreferenceDTO result = userPreferenceService.getUserPreferences(1L);

        // Assert
        assertThat(result).isNotNull();
        verify(userPreferenceRepository).save(any(UserPreference.class));
        verify(userPreferenceMapper).toDto(userPreference);
    }

    @Test
    void getUserPreferences_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userPreferenceService.getUserPreferences(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with ID: 1");
    }

    @Test
    void getUserPreferencesByExternalId_WhenUserExists_ShouldReturnPreferences() {
        // Arrange
        when(userRepository.findByExternalId("usr-ext-00001")).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(userPreference));
        when(userPreferenceMapper.toDto(userPreference)).thenReturn(userPreferenceDTO);

        // Act
        UserPreferenceDTO result = userPreferenceService.getUserPreferencesByExternalId("usr-ext-00001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserExternalId()).isEqualTo("usr-ext-00001");
        verify(userRepository).findByExternalId("usr-ext-00001");
    }

    @Test
    void updateUserPreferences_WhenValidRequest_ShouldUpdateAndReturn() {
        // Arrange
        Language spanish = Language.builder().id(4L).name("Spanish").build();
        UpdateUserPreferenceRequest request = UpdateUserPreferenceRequest.builder()
                .theme("dark")
                .languageId(4L)
                .itemsPerPage(50)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(userPreference));
        when(languageRepository.findById(4L)).thenReturn(Optional.of(spanish));
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(userPreference);
        when(userPreferenceMapper.toDto(userPreference)).thenReturn(userPreferenceDTO);

        // Act
        UserPreferenceDTO result = userPreferenceService.updateUserPreferences(1L, request);

        // Assert
        assertThat(result).isNotNull();
        verify(userPreferenceMapper).updateEntityFromRequest(request, userPreference);
        verify(languageRepository).findById(4L);
        verify(userPreferenceRepository).save(userPreference);
    }

    @Test
    void resetToDefaults_WhenCalled_ShouldDeleteAndRecreate() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findByUserId(1L)).thenReturn(Optional.of(userPreference));
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(userPreference);
        when(userPreferenceMapper.toDto(any(UserPreference.class))).thenReturn(userPreferenceDTO);

        // Act
        UserPreferenceDTO result = userPreferenceService.resetToDefaults(1L);

        // Assert
        assertThat(result).isNotNull();
        verify(userPreferenceRepository).delete(userPreference);
        verify(userPreferenceRepository).save(any(UserPreference.class));
    }

    @Test
    void createDefaultPreferences_ShouldCreateWithDefaultValues() {
        // Arrange
        when(languageRepository.findById(1L)).thenReturn(Optional.of(language));
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(userPreference);

        // Act
        UserPreference result = userPreferenceService.createDefaultPreferences(user);

        // Assert
        assertThat(result).isNotNull();
        verify(userPreferenceRepository).save(any(UserPreference.class));
    }

    @Test
    void preferencesExist_WhenPreferencesExist_ShouldReturnTrue() {
        // Arrange
        when(userPreferenceRepository.existsByUserId(1L)).thenReturn(true);

        // Act
        boolean result = userPreferenceService.preferencesExist(1L);

        // Assert
        assertThat(result).isTrue();
        verify(userPreferenceRepository).existsByUserId(1L);
    }

    @Test
    void preferencesExist_WhenPreferencesDoNotExist_ShouldReturnFalse() {
        // Arrange
        when(userPreferenceRepository.existsByUserId(1L)).thenReturn(false);

        // Act
        boolean result = userPreferenceService.preferencesExist(1L);

        // Assert
        assertThat(result).isFalse();
        verify(userPreferenceRepository).existsByUserId(1L);
    }
}
