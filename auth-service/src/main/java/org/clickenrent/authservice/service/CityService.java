package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.dto.CityDTO;
import org.clickenrent.authservice.entity.City;
import org.clickenrent.authservice.entity.Country;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.mapper.CityMapper;
import org.clickenrent.authservice.repository.CityRepository;
import org.clickenrent.authservice.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing City entities.
 */
@Service
@RequiredArgsConstructor
public class CityService {
    
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CountryRepository countryRepository;
    
    @Transactional(readOnly = true)
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        return cityMapper.toDto(city);
    }
    
    @Transactional(readOnly = true)
    public List<CityDTO> getCitiesByCountryId(Long countryId) {
        return cityRepository.findByCountryId(countryId).stream()
                .map(cityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CityDTO createCity(CityDTO cityDTO) {
        City city = cityMapper.toEntity(cityDTO);
        
        // Set the country relationship
        if (cityDTO.getCountryId() != null) {
            Country country = countryRepository.findById(cityDTO.getCountryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Country", "id", cityDTO.getCountryId()));
            city.setCountry(country);
        }
        
        city = cityRepository.save(city);
        return cityMapper.toDto(city);
    }
    
    @Transactional
    public CityDTO updateCity(Long id, CityDTO cityDTO) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        
        cityMapper.updateEntityFromDto(cityDTO, city);
        
        // Update country relationship if provided
        if (cityDTO.getCountryId() != null) {
            Country country = countryRepository.findById(cityDTO.getCountryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Country", "id", cityDTO.getCountryId()));
            city.setCountry(country);
        }
        
        city = cityRepository.save(city);
        return cityMapper.toDto(city);
    }
    
    @Transactional
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        cityRepository.delete(city);
    }
}

