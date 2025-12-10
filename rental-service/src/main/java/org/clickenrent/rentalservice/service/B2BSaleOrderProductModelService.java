package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleOrderProductModelDTO;
import org.clickenrent.rentalservice.entity.B2BSaleOrderProductModel;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSaleOrderProductModelMapper;
import org.clickenrent.rentalservice.repository.B2BSaleOrderProductModelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleOrderProductModelService {

    private final B2BSaleOrderProductModelRepository b2bSaleOrderProductModelRepository;
    private final B2BSaleOrderProductModelMapper b2bSaleOrderProductModelMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public Page<B2BSaleOrderProductModelDTO> getAllProductModels(Pageable pageable) {
        if (securityService.isAdmin()) {
            return b2bSaleOrderProductModelRepository.findAll(pageable)
                    .map(b2bSaleOrderProductModelMapper::toDto);
        }

        throw new UnauthorizedException("You don't have permission to view all product models");
    }

    @Transactional(readOnly = true)
    public List<B2BSaleOrderProductModelDTO> getProductModelsByOrderId(Long orderId) {
        // Access control handled at order level
        return b2bSaleOrderProductModelRepository.findByB2bSaleOrderId(orderId).stream()
                .map(b2bSaleOrderProductModelMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleOrderProductModelDTO getProductModelById(Long id) {
        B2BSaleOrderProductModel productModel = b2bSaleOrderProductModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderProductModel", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to view this product model");
        }

        return b2bSaleOrderProductModelMapper.toDto(productModel);
    }

    @Transactional
    public B2BSaleOrderProductModelDTO createProductModel(B2BSaleOrderProductModelDTO dto) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to create product models");
        }

        B2BSaleOrderProductModel productModel = b2bSaleOrderProductModelMapper.toEntity(dto);
        productModel = b2bSaleOrderProductModelRepository.save(productModel);
        return b2bSaleOrderProductModelMapper.toDto(productModel);
    }

    @Transactional
    public B2BSaleOrderProductModelDTO updateProductModel(Long id, B2BSaleOrderProductModelDTO dto) {
        B2BSaleOrderProductModel productModel = b2bSaleOrderProductModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderProductModel", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("You don't have permission to update this product model");
        }

        b2bSaleOrderProductModelMapper.updateEntityFromDto(dto, productModel);
        productModel = b2bSaleOrderProductModelRepository.save(productModel);
        return b2bSaleOrderProductModelMapper.toDto(productModel);
    }

    @Transactional
    public void deleteProductModel(Long id) {
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete product models");
        }

        B2BSaleOrderProductModel productModel = b2bSaleOrderProductModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleOrderProductModel", "id", id));
        b2bSaleOrderProductModelRepository.delete(productModel);
    }
}
