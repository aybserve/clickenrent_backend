package org.clickenrent.analyticsservice;

import org.clickenrent.analyticsservice.client.AuthServiceClient;
import org.clickenrent.analyticsservice.client.PaymentServiceClient;
import org.clickenrent.analyticsservice.client.RentalServiceClient;
import org.clickenrent.analyticsservice.client.SupportServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AnalyticsServiceApplicationTests {

	@MockBean
	private RentalServiceClient rentalServiceClient;

	@MockBean
	private AuthServiceClient authServiceClient;

	@MockBean
	private PaymentServiceClient paymentServiceClient;

	@MockBean
	private SupportServiceClient supportServiceClient;

	@Test
	void contextLoads() {
	}

}
