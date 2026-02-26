package org.clickenrent.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "JWT_SECRET=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLTI1Ni1iaXQ=",
    "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLTI1Ni1iaXQ=",
    "stripe.api.key=sk_test_dummy",
    "stripe.webhook.secret=whsec_test_dummy",
    "payment.provider=stripe"
})
class PaymentServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
