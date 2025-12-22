package org.clickenrent.authservice.mapper;

import org.clickenrent.authservice.dto.AddressDTO;
import org.clickenrent.authservice.entity.Address;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Address entity and AddressDTO.
 */
@Component
public class AddressMapper {
    
    public AddressDTO toDto(Address address) {
        if (address == null) {
            return null;
        }
        
        return AddressDTO.builder()
                .id(address.getId())
                .city(address.getCity())
                .countryId(address.getCountry() != null ? address.getCountry().getId() : null)
                .street(address.getStreet())
                .postcode(address.getPostcode())
                .dateCreated(address.getDateCreated())
                .lastDateModified(address.getLastDateModified())
                .createdBy(address.getCreatedBy())
                .lastModifiedBy(address.getLastModifiedBy())
                .build();
    }
    
    public Address toEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Address.builder()
                .id(dto.getId())
                .city(dto.getCity())
                .street(dto.getStreet())
                .postcode(dto.getPostcode())
                .build();
    }
    
    public void updateEntityFromDto(AddressDTO dto, Address address) {
        if (dto == null || address == null) {
            return;
        }
        
        if (dto.getCity() != null) {
            address.setCity(dto.getCity());
        }
        if (dto.getStreet() != null) {
            address.setStreet(dto.getStreet());
        }
        if (dto.getPostcode() != null) {
            address.setPostcode(dto.getPostcode());
        }
    }
}





