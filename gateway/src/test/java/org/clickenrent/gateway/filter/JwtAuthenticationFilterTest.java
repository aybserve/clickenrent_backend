package org.clickenrent.gateway.filter;

import org.clickenrent.gateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JwtAuthenticationFilter.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "jwtUtil", jwtUtil);
        lenient().when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void filter_whenNoAuthorizationHeader_returns401AndDoesNotCallChain() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/bikes").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void filter_whenAuthorizationNotBearer_returns401AndDoesNotCallChain() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/bikes")
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void filter_whenTokenInvalid_returns401AndDoesNotCallChain() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/bikes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer bad-token").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.extractTokenFromHeader("Bearer bad-token")).thenReturn("bad-token");
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void filter_whenTokenExtractionReturnsNull_returns401AndDoesNotCallChain() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/bikes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.extractTokenFromHeader("Bearer ")).thenReturn(null);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void filter_whenValidToken_callsChainWithModifiedRequestHeaders() {
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/bikes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.extractTokenFromHeader("Bearer " + token)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(42L);
        when(jwtUtil.extractEmail(token)).thenReturn("user@example.com");
        when(jwtUtil.extractRoles(token)).thenReturn(List.of("ADMIN", "USER"));
        when(jwtUtil.extractUserExternalId(token)).thenReturn("ext-42");
        when(jwtUtil.extractCompanyExternalIds(token)).thenReturn(List.of("c1", "c2"));

        filter.filter(exchange, chain).block();

        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(exchangeCaptor.capture());

        ServerWebExchange captured = exchangeCaptor.getValue();
        HttpHeaders headers = captured.getRequest().getHeaders();

        assertThat(headers.getFirst("X-User-Id")).isEqualTo("42");
        assertThat(headers.getFirst("X-User-Email")).isEqualTo("user@example.com");
        assertThat(headers.getFirst("X-User-Roles")).isEqualTo("ADMIN,USER");
        assertThat(headers.getFirst("X-User-External-Id")).isEqualTo("ext-42");
        assertThat(headers.getFirst("X-Company-External-Ids")).isEqualTo("c1,c2");
    }

    @Test
    void filter_whenValidTokenWithNullOptionalClaims_addsEmptyStringsForMissingHeaders() {
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.extractTokenFromHeader("Bearer " + token)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(jwtUtil.extractEmail(token)).thenReturn("minimal@example.com");
        when(jwtUtil.extractRoles(token)).thenReturn(null);
        when(jwtUtil.extractUserExternalId(token)).thenReturn(null);
        when(jwtUtil.extractCompanyExternalIds(token)).thenReturn(null);

        filter.filter(exchange, chain).block();

        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(exchangeCaptor.capture());

        HttpHeaders headers = exchangeCaptor.getValue().getRequest().getHeaders();
        assertThat(headers.getFirst("X-User-Id")).isEqualTo("1");
        assertThat(headers.getFirst("X-User-Email")).isEqualTo("minimal@example.com");
        assertThat(headers.getFirst("X-User-Roles")).isEqualTo("");
        assertThat(headers.getFirst("X-User-External-Id")).isEqualTo("");
        assertThat(headers.getFirst("X-Company-External-Ids")).isEqualTo("");
    }
}
