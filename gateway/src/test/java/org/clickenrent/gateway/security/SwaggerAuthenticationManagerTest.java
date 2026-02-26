package org.clickenrent.gateway.security;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for SwaggerAuthenticationManager using MockWebServer to emulate auth-service.
 */
class SwaggerAuthenticationManagerTest {

    private MockWebServer mockWebServer;
    private SwaggerAuthenticationManager manager;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private void createManager() {
        String baseUrl = mockWebServer.url("/").toString();
        WebClient.Builder builder = WebClient.builder().baseUrl(baseUrl);
        manager = new SwaggerAuthenticationManager(baseUrl, builder);
    }

    @Test
    void authenticate_whenAuthServiceReturnsHasAccessTrue_returnsAuthenticationWithAuthorities() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"hasAccess\":true,\"roles\":[\"ADMIN\",\"USER\"],\"username\":\"admin\"}")
                .addHeader("Content-Type", "application/json"));
        createManager();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("admin", "password");

        Authentication result = manager.authenticate(token).block();

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("admin");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void authenticate_whenAuthServiceReturnsHasAccessFalse_throwsBadCredentialsException() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"hasAccess\":false,\"roles\":[],\"username\":null}")
                .addHeader("Content-Type", "application/json"));
        createManager();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("user", "pass");

        assertThatThrownBy(() -> manager.authenticate(token).block())
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void authenticate_whenAuthServiceReturnsServerError_throwsBadCredentialsException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        createManager();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("user", "pass");

        assertThatThrownBy(() -> manager.authenticate(token).block())
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Authentication failed");
    }

    @Test
    void authenticate_whenSameCredentialsCalledTwice_usesCacheAndMakesSingleRequest() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"hasAccess\":true,\"roles\":[\"ADMIN\"],\"username\":\"cached\"}")
                .addHeader("Content-Type", "application/json"));
        createManager();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("cached", "secret");

        Authentication first = manager.authenticate(token).block();
        Authentication second = manager.authenticate(token).block();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.getName()).isEqualTo("cached");
        assertThat(second.getName()).isEqualTo("cached");
        // Only one HTTP request should have been made (second call served from cache)
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }
}
