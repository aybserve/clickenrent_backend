package org.clickenrent.rentalservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.rentalservice.dto.B2BSaleProductDTO;
import org.clickenrent.rentalservice.entity.B2BSale;
import org.clickenrent.rentalservice.entity.B2BSaleProduct;
import org.clickenrent.rentalservice.exception.ResourceNotFoundException;
import org.clickenrent.rentalservice.exception.UnauthorizedException;
import org.clickenrent.rentalservice.mapper.B2BSaleProductMapper;
import org.clickenrent.rentalservice.repository.B2BSaleProductRepository;
import org.clickenrent.rentalservice.repository.B2BSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class B2BSaleProductService {

    private final B2BSaleProductRepository b2bSaleProductRepository;
    private final B2BSaleRepository b2bSaleRepository;
    private final B2BSaleProductMapper b2bSaleProductMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<B2BSaleProductDTO> getProductsBySale(Long b2bSaleId) {
        B2BSale sale = b2bSaleRepository.findById(b2bSaleId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", b2bSaleId));

        if (!securityService.isAdmin() && 
            !securityService.hasAccessToCompanyByExternalId(sale.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to view products for this sale");
        }

        return b2bSaleProductRepository.findByB2bSale(sale).stream()
                .map(b2bSaleProductMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public B2BSaleProductDTO getProductById(Long id) {
        B2BSaleProduct product = b2bSaleProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleProduct", "id", id));
        return b2bSaleProductMapper.toDto(product);
    }

    @Transactional
    public B2BSaleProductDTO createProduct(B2BSaleProductDTO dto) {
        B2BSale sale = b2bSaleRepository.findById(dto.getB2bSaleId())
                .orElseThrow(() -> new ResourceNotFoundException("B2BSale", "id", dto.getB2bSaleId()));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(sale.getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to add products to this sale");
        }

        B2BSaleProduct product = b2bSaleProductMapper.toEntity(dto);
        product = b2bSaleProductRepository.save(product);
        return b2bSaleProductMapper.toDto(product);
    }

    @Transactional
    public B2BSaleProductDTO updateProduct(Long id, B2BSaleProductDTO dto) {
        B2BSaleProduct product = b2bSaleProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleProduct", "id", id));

        if (!securityService.isAdmin() && !securityService.hasAccessToCompanyByExternalId(product.getB2bSale().getLocation().getCompanyExternalId())) {
            throw new UnauthorizedException("You don't have permission to update this product");
        }

        b2bSaleProductMapper.updateEntityFromDto(dto, product);
        product = b2bSaleProductRepository.save(product);
        return b2bSaleProductMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        B2BSaleProduct product = b2bSaleProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BSaleProduct", "id", id));

        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only administrators can delete products");
        }

        b2bSaleProductRepository.delete(product);
    }
}
