package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CountryDTO;
import org.clickenrent.authservice.entity.Country;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.CountryMapper;
import org.clickenrent.authservice.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing Country entities.
 */
@Service
@RequiredArgsConstructor
public class CountryService {
    
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    
    @Transactional(readOnly = true)
    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CountryDTO getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
        return countryMapper.toDto(country);
    }
    
    @Transactional(readOnly = true)
    public CountryDTO getCountryByName(String name) {
        Country country = countryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "name", name));
        return countryMapper.toDto(country);
    }
    
    @Transactional
    public CountryDTO createCountry(CountryDTO countryDTO) {
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        return countryMapper.toDto(country);
    }
    
    @Transactional
    public CountryDTO updateCountry(Long id, CountryDTO countryDTO) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
        
        countryMapper.updateEntityFromDto(countryDTO, country);
        country = countryRepository.save(country);
        return countryMapper.toDto(country);
    }
    
    @Transactional
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
        countryRepository.delete(country);
    }
}

