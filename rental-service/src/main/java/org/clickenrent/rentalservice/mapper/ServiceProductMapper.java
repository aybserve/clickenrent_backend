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
    private final org.clickenrent.rentalservice.repository.ProductRepository productRepository;

    public ServiceProductDTO toDto(ServiceProduct serviceProduct) {
        if (serviceProduct == null) {
            return null;
        }

        return ServiceProductDTO.builder()
                .id(serviceProduct.getId())
                .externalId(serviceProduct.getExternalId())
                .serviceId(serviceProduct.getService() != null ? serviceProduct.getService().getId() : null)
                .relatedProductId(serviceProduct.getRelatedProduct() != null ? serviceProduct.getRelatedProduct().getId() : null)
                .isB2BRentable(serviceProduct.getIsB2BRentable())
                .b2bSubscriptionPrice(serviceProduct.getB2bSubscriptionPrice())
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
                .isB2BRentable(dto.getIsB2BRentable())
                .b2bSubscriptionPrice(dto.getB2bSubscriptionPrice());

        if (dto.getServiceId() != null) {
            builder.service(serviceRepository.findById(dto.getServiceId()).orElse(null));
        }
        if (dto.getRelatedProductId() != null) {
            builder.relatedProduct(productRepository.findById(dto.getRelatedProductId()).orElse(null));
        }

        return builder.build();
    }
}








