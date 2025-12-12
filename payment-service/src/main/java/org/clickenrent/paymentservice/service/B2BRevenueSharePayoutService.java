package org.clickenrent.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clickenrent.paymentservice.dto.B2BRevenueSharePayoutDTO;
import org.clickenrent.paymentservice.entity.B2BRevenueSharePayout;
import org.clickenrent.paymentservice.exception.ResourceNotFoundException;
import org.clickenrent.paymentservice.exception.UnauthorizedException;
import org.clickenrent.paymentservice.mapper.B2BRevenueSharePayoutMapper;
import org.clickenrent.paymentservice.repository.B2BRevenueSharePayoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for B2B Revenue Share Payout management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2BRevenueSharePayoutService {

    private final B2BRevenueSharePayoutRepository b2bRevenueSharePayoutRepository;
    private final B2BRevenueSharePayoutMapper b2bRevenueSharePayoutMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<B2BRevenueSharePayoutDTO> findAll() {
        List<B2BRevenueSharePayout> payouts = b2bRevenueSharePayoutRepository.findAll();
        
        // Filter by permissions
        if (!securityService.isAdmin()) {
            List<Long> companyIds = securityService.getCurrentUserCompanyIds();
            payouts = payouts.stream()
                    .filter(p -> companyIds.contains(p.getCompanyId()))
                    .collect(Collectors.toList());
        }
        
        return b2bRevenueSharePayoutMapper.toDTOList(payouts);
    }

    @Transactional(readOnly = true)
    public B2BRevenueSharePayoutDTO findById(Long id) {
        B2BRevenueSharePayout payout = b2bRevenueSharePayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayout", "id", id));
        
        checkPayoutAccess(payout);
        
        return b2bRevenueSharePayoutMapper.toDTO(payout);
    }

    @Transactional(readOnly = true)
    public B2BRevenueSharePayoutDTO findByExternalId(UUID externalId) {
        B2BRevenueSharePayout payout = b2bRevenueSharePayoutRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayout", "externalId", externalId));
        
        checkPayoutAccess(payout);
        
        return b2bRevenueSharePayoutMapper.toDTO(payout);
    }

    @Transactional(readOnly = true)
    public List<B2BRevenueSharePayoutDTO> findByCompanyId(Long companyId) {
        // Check permission
        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(companyId)) {
            throw new UnauthorizedException("You don't have permission to access payouts for this company");
        }
        
        List<B2BRevenueSharePayout> payouts = b2bRevenueSharePayoutRepository.findByCompanyId(companyId);
        return b2bRevenueSharePayoutMapper.toDTOList(payouts);
    }

    @Transactional
    public B2BRevenueSharePayoutDTO create(B2BRevenueSharePayoutDTO dto) {
        // Check permission
        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(dto.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to create payouts for this company");
        }
        
        B2BRevenueSharePayout payout = b2bRevenueSharePayoutMapper.toEntity(dto);
        B2BRevenueSharePayout savedPayout = b2bRevenueSharePayoutRepository.save(payout);
        return b2bRevenueSharePayoutMapper.toDTO(savedPayout);
    }

    @Transactional
    public B2BRevenueSharePayoutDTO calculatePayout(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Check permission
        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(companyId)) {
            throw new UnauthorizedException("You don't have permission to calculate payouts for this company");
        }
        
        log.info("Calculating payout for company: {} from {} to {}", companyId, startDate, endDate);
        
        // TODO: Implement actual payout calculation logic
        // This would typically:
        // 1. Query bike rentals for the company in the date range
        // 2. Calculate revenue share amounts
        // 3. Create payout items for each rental
        // 4. Sum up total amounts
        
        // For now, return a placeholder implementation
        throw new UnsupportedOperationException("Payout calculation not yet implemented");
    }

    @Transactional
    public B2BRevenueSharePayoutDTO processPayout(Long payoutId) {
        B2BRevenueSharePayout payout = b2bRevenueSharePayoutRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayout", "id", payoutId));
        
        // Only admins can process payouts
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can process payouts");
        }
        
        log.info("Processing payout: {}", payoutId);
        
        // TODO: Implement actual payout processing logic
        // This would typically:
        // 1. Create a financial transaction for the payout
        // 2. Initiate payment via Stripe or other provider
        // 3. Update payout status
        // 4. Update paidAmount and remainingAmount
        
        throw new UnsupportedOperationException("Payout processing not yet implemented");
    }

    @Transactional
    public B2BRevenueSharePayoutDTO update(Long id, B2BRevenueSharePayoutDTO dto) {
        B2BRevenueSharePayout existingPayout = b2bRevenueSharePayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayout", "id", id));
        
        checkPayoutAccess(existingPayout);
        
        existingPayout.setPaymentStatus(b2bRevenueSharePayoutMapper.toEntity(dto).getPaymentStatus());
        existingPayout.setDueDate(dto.getDueDate());
        existingPayout.setTotalAmount(dto.getTotalAmount());
        existingPayout.setPaidAmount(dto.getPaidAmount());
        existingPayout.setRemainingAmount(dto.getRemainingAmount());
        
        B2BRevenueSharePayout updatedPayout = b2bRevenueSharePayoutRepository.save(existingPayout);
        return b2bRevenueSharePayoutMapper.toDTO(updatedPayout);
    }

    @Transactional
    public void delete(Long id) {
        B2BRevenueSharePayout payout = b2bRevenueSharePayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("B2BRevenueSharePayout", "id", id));
        
        if (!securityService.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete payouts");
        }
        
        b2bRevenueSharePayoutRepository.deleteById(id);
    }

    private void checkPayoutAccess(B2BRevenueSharePayout payout) {
        if (!securityService.isAdmin() && !securityService.hasAccessToCompany(payout.getCompanyId())) {
            throw new UnauthorizedException("You don't have permission to access this payout");
        }
    }
}
