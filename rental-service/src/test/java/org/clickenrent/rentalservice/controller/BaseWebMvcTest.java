package org.clickenrent.rentalservice.controller;

import org.clickenrent.rentalservice.service.MapboxService;
import org.clickenrent.rentalservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.lenient;

/**
 * Base for WebMvcTest controller tests. Stubs SecurityService so TenantInterceptor and controllers work.
 * Mocks MapboxService for controllers that depend on it (Hub, Location).
 */
abstract class BaseWebMvcTest {

    @MockBean
    protected SecurityService securityService;

    @MockBean
    protected MapboxService mapboxService;

    @BeforeEach
    void stubSecurityService() {
        lenient().when(securityService.getCurrentUserId()).thenReturn(1L);
        lenient().when(securityService.isAdmin()).thenReturn(true);
    }
}
