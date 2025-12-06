package org.clickenrent.rentalservice.mapper;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.ServiceProductDTO;
import org.clickenrent.rentalservice.entity.ServiceProduct;
import org.clickenrent.rentalservice.repository.ServiceRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ServiceProduct entity and ServiceProductDTO.
 */
@Component
@RequiredArgsConstructor
public class ServiceProductMapper {

    private final ServiceRepository serviceRepository;

    public ServiceProductDTO toDto(ServiceProduct serviceProduct) {
        if (serviceProduct == null) {
            return null;
        }

        return ServiceProductDTO.builder()
                .id(serviceProduct.getId())
                .externalId(serviceProduct.getExternalId())
                .serviceId(serviceProduct.getService() != null ? serviceProduct.getService().getId() : null)
                .productId(serviceProduct.getProductId())
                .isB2BRentable(serviceProduct.getIsB2BRentable())
                .dateCreated(serviceProduct.getDateCreated())
                .lastDateModified(serviceProduct.getLastDateModified())
                .createdBy(serviceProduct.getCreatedBy())
                .lastModifiedBy(serviceProduct.getLastModifiedBy())
                .build();
    }

    public ServiceProduct toEntity(ServiceProductDTO dto) {
        if (dto == null) {
            return null;
        }

        ServiceProduct.ServiceProductBuilder<?, ?> builder = ServiceProduct.builder()
                .id(dto.getId())
                .externalId(dto.getExternalId())
                .productId(dto.getProductId())
                .isB2BRentable(dto.getIsB2BRentable());

        if (dto.getServiceId() != null) {
            builder.service(serviceRepository.findById(dto.getServiceId()).orElse(null));
        }

        return builder.build();
    }
}
