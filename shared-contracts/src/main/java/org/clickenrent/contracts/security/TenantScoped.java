package org.clickenrent.contracts.security;

/**
 * Marker interface for entities that are scoped to a specific tenant (company).
 * Used for runtime validation to detect cross-tenant data leaks.
 * 
 * Entities implementing this interface can be validated by TenantValidationAspect
 * to ensure they belong to the current user's accessible companies.
 * 
 * Example usage:
 * <pre>
 * {@code
 * @Entity
 * public class Rental extends BaseAuditEntity implements TenantScoped {
 *     @Column(name = "company_external_id")
 *     private String companyExternalId;
 *     
 *     @Override
 *     public String getCompanyExternalId() {
 *         return this.companyExternalId;
 *     }
 * }
 * }
 * </pre>
 */
public interface TenantScoped {
    /**
     * Get the company external ID this entity belongs to.
     * 
     * @return Company external ID (UUID string)
     */
    String getCompanyExternalId();
}
