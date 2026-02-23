package org.clickenrent.paymentservice.controller;

import org.clickenrent.paymentservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.lenient;

/**
 * Base for WebMvcTest controller tests. Stubs SecurityService so TenantInterceptor and controllers work.
 */
abstract class BaseWebMvcTest {

    @MockBean
    protected SecurityService securityService;

    @BeforeEach
    void stubSecurityService() {
        lenient().when(securityService.getCurrentUserId()).thenReturn(1L);
        lenient().when(securityService.isAdmin()).thenReturn(true);
    }
}
