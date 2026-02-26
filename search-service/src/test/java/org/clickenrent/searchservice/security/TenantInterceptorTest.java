package org.clickenrent.searchservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.clickenrent.contracts.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TenantInterceptor.
 *
 * @author Vitaliy Shvetsov
 */
@ExtendWith(MockitoExtension.class)
class TenantInterceptorTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private TenantInterceptor tenantInterceptor;

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void preHandle_whenAdmin_setsSuperAdminAndReturnsTrue() throws Exception {
        tenantInterceptor = new TenantInterceptor(securityService);
        when(securityService.isAdmin()).thenReturn(true);

        boolean result = tenantInterceptor.preHandle(request, response, null);

        assertTrue(result);
        assertTrue(TenantContext.isSuperAdmin());
        assertTrue(TenantContext.getCurrentCompanies().isEmpty());
        verify(securityService).isAdmin();
    }

    @Test
    void preHandle_whenB2B_setsCurrentCompanies() throws Exception {
        tenantInterceptor = new TenantInterceptor(securityService);
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(true);
        when(securityService.getCurrentUserCompanyExternalIds()).thenReturn(List.of("company-1", "company-2"));

        boolean result = tenantInterceptor.preHandle(request, response, null);

        assertTrue(result);
        assertFalse(TenantContext.isSuperAdmin());
        assertEquals(List.of("company-1", "company-2"), TenantContext.getCurrentCompanies());
    }

    @Test
    void preHandle_whenCustomer_setsEmptyCompanies() throws Exception {
        tenantInterceptor = new TenantInterceptor(securityService);
        when(securityService.isAdmin()).thenReturn(false);
        when(securityService.isB2B()).thenReturn(false);

        boolean result = tenantInterceptor.preHandle(request, response, null);

        assertTrue(result);
        assertTrue(TenantContext.getCurrentCompanies().isEmpty());
    }

    @Test
    void afterCompletion_clearsContext() throws Exception {
        tenantInterceptor = new TenantInterceptor(securityService);
        when(securityService.isAdmin()).thenReturn(true);
        tenantInterceptor.preHandle(request, response, null);
        assertTrue(TenantContext.isSuperAdmin());

        tenantInterceptor.afterCompletion(request, response, null, null);

        assertFalse(TenantContext.isSuperAdmin());
        assertTrue(TenantContext.getCurrentCompanies().isEmpty());
    }
}
