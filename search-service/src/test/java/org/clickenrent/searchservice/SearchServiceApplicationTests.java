package org.clickenrent.searchservice;

import org.clickenrent.searchservice.event.IndexEventConsumer;
import org.clickenrent.searchservice.repository.BikeDocumentRepository;
import org.clickenrent.searchservice.repository.HubDocumentRepository;
import org.clickenrent.searchservice.repository.LocationDocumentRepository;
import org.clickenrent.searchservice.repository.UserDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic application context test for Search Service.
 * Mocks Elasticsearch and repositories so context loads without a real ES instance.
 *
 * @author Vitaliy Shvetsov
 */
@SpringBootTest
@ActiveProfiles("test")
class SearchServiceApplicationTests {

    @MockBean
    private IndexEventConsumer indexEventConsumer;

    @MockBean(name = "elasticsearchTemplate")
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private UserDocumentRepository userDocumentRepository;

    @MockBean
    private BikeDocumentRepository bikeDocumentRepository;

    @MockBean
    private LocationDocumentRepository locationDocumentRepository;

    @MockBean
    private HubDocumentRepository hubDocumentRepository;

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
    }
}
