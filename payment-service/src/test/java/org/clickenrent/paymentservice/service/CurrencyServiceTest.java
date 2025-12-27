package org.clickenrent.paymentservice.service;

import org.clickenrent.paymentservice.dto.CurrencyDTO;
import org.clickenrent.paymentservice.entity.Currency;
import org.clickenrent.paymentservice.exception.DuplicateResourceException;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.mapper.CurrencyMapper;
import org.clickenrent.paymentservice.repository.CurrencyRepository;
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
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyMapper currencyMapper;

    @InjectMocks
    private CurrencyService currencyService;

    private Currency testCurrency;
    private CurrencyDTO testCurrencyDTO;
    private String testExternalId;

    @BeforeEach
    void setUp() {
        testExternalId = UUID.randomUUID().toString();
        
        testCurrency = Currency.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("USD")
                .name("US Dollar")
                .build();

        testCurrencyDTO = CurrencyDTO.builder()
                .id(1L)
                .externalId(testExternalId)
                .code("USD")
                .name("US Dollar")
                .build();
    }

    @Test
    void findAll_ReturnsAllCurrencies() {
        when(currencyRepository.findAll()).thenReturn(Arrays.asList(testCurrency));
        when(currencyMapper.toDTOList(anyList())).thenReturn(Arrays.asList(testCurrencyDTO));

        List<CurrencyDTO> result = currencyService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).getCode());
        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    void findById_Success() {
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(testCurrency));
        when(currencyMapper.toDTO(testCurrency)).thenReturn(testCurrencyDTO);

        CurrencyDTO result = currencyService.findById(1L);

        assertNotNull(result);
        assertEquals("USD", result.getCode());
        assertEquals("US Dollar", result.getName());
        verify(currencyRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currencyService.findById(999L));
        verify(currencyRepository, times(1)).findById(999L);
    }

    @Test
    void findByExternalId_Success() {
        when(currencyRepository.findByExternalId(testExternalId)).thenReturn(Optional.of(testCurrency));
        when(currencyMapper.toDTO(testCurrency)).thenReturn(testCurrencyDTO);

        CurrencyDTO result = currencyService.findByExternalId(testExternalId);

        assertNotNull(result);
        assertEquals("USD", result.getCode());
        verify(currencyRepository, times(1)).findByExternalId(testExternalId);
    }

    @Test
    void findByExternalId_NotFound() {
        String randomId = UUID.randomUUID().toString();
        when(currencyRepository.findByExternalId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currencyService.findByExternalId(randomId));
    }

    @Test
    void findByCode_Success() {
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(testCurrency));
        when(currencyMapper.toDTO(testCurrency)).thenReturn(testCurrencyDTO);

        CurrencyDTO result = currencyService.findByCode("USD");

        assertNotNull(result);
        assertEquals("USD", result.getCode());
        verify(currencyRepository, times(1)).findByCode("USD");
    }

    @Test
    void findByCode_NotFound() {
        when(currencyRepository.findByCode("XXX")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currencyService.findByCode("XXX"));
    }

    @Test
    void create_Success() {
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.empty());
        when(currencyMapper.toEntity(testCurrencyDTO)).thenReturn(testCurrency);
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);
        when(currencyMapper.toDTO(testCurrency)).thenReturn(testCurrencyDTO);

        CurrencyDTO result = currencyService.create(testCurrencyDTO);

        assertNotNull(result);
        assertEquals("USD", result.getCode());
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void create_DuplicateCode_ThrowsException() {
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(testCurrency));

        assertThrows(DuplicateResourceException.class, () -> currencyService.create(testCurrencyDTO));
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void update_Success() {
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(testCurrency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);
        when(currencyMapper.toDTO(testCurrency)).thenReturn(testCurrencyDTO);

        CurrencyDTO result = currencyService.update(1L, testCurrencyDTO);

        assertNotNull(result);
        assertEquals("USD", result.getCode());
        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void update_NotFound() {
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> currencyService.update(999L, testCurrencyDTO));
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void update_DuplicateCode_ThrowsException() {
        Currency existingCurrency = Currency.builder()
                .id(1L)
                .code("EUR")
                .name("Euro")
                .build();
        
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(existingCurrency));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(testCurrency));

        assertThrows(DuplicateResourceException.class, () -> currencyService.update(1L, testCurrencyDTO));
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void delete_Success() {
        when(currencyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(currencyRepository).deleteById(1L);

        currencyService.delete(1L);

        verify(currencyRepository, times(1)).existsById(1L);
        verify(currencyRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(currencyRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> currencyService.delete(999L));
        verify(currencyRepository, never()).deleteById(anyLong());
    }
}







