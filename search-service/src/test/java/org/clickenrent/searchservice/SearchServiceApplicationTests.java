package org.clickenrent.searchservice;

import org.clickenrent.searchservice.event.IndexEventConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test for Search Service.
 *
 * @author Vitaliy Shvetsov
 */
@SpringBootTest
@ActiveProfiles("test")
class SearchServiceApplicationTests {

    @MockBean
    private IndexEventConsumer indexEventConsumer;

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
    }
}
