package org.clickenrent.gateway;

import org.clickenrent.gateway.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GatewayApplicationTests {

	@Autowired(required = false)
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Test
	void contextLoads() {
	}

	@Test
	void jwtAuthenticationFilterBeanPresent() {
		assertThat(jwtAuthenticationFilter).isNotNull();
	}
}
