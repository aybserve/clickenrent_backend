package org.clickenrent.authservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Metrics for OAuth authentication flows.
 * Tracks success rates, failures, and performance of OAuth operations.
 * Integrates with Sentry for performance monitoring and transaction tracking.
 */
@Component
@RequiredArgsConstructor
public class OAuthMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // Counter names
    private static final String OAUTH_LOGIN_ATTEMPTS = "oauth.login.attempts";
    private static final String OAUTH_LOGIN_SUCCESS = "oauth.login.success";
    private static final String OAUTH_LOGIN_FAILURE = "oauth.login.failure";
    private static final String OAUTH_NEW_USER_REGISTRATION = "oauth.user.registration";
    private static final String OAUTH_AUTO_LINKING = "oauth.user.autolinking";
    
    // Timer names
    private static final String OAUTH_FLOW_DURATION = "oauth.flow.duration";
    
    /**
     * Record an OAuth login attempt.
     * 
     * @param provider OAuth provider (e.g., "google")
     */
    public void recordLoginAttempt(String provider) {
        Counter.builder(OAUTH_LOGIN_ATTEMPTS)
                .tag("provider", provider)
                .description("Total number of OAuth login attempts")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record an OAuth login attempt with flow type.
     * 
     * @param provider OAuth provider (e.g., "google")
     * @param flow OAuth flow type (e.g., "web", "mobile")
     */
    public void recordLoginAttempt(String provider, String flow) {
        Counter.builder(OAUTH_LOGIN_ATTEMPTS)
                .tag("provider", provider)
                .tag("flow", flow)
                .description("Total number of OAuth login attempts by flow")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record a successful OAuth login.
     * 
     * @param provider OAuth provider (e.g., "google")
     */
    public void recordLoginSuccess(String provider) {
        Counter.builder(OAUTH_LOGIN_SUCCESS)
                .tag("provider", provider)
                .description("Number of successful OAuth logins")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record a successful OAuth login with flow type.
     * 
     * @param provider OAuth provider (e.g., "google")
     * @param flow OAuth flow type (e.g., "web", "mobile")
     */
    public void recordLoginSuccess(String provider, String flow) {
        Counter.builder(OAUTH_LOGIN_SUCCESS)
                .tag("provider", provider)
                .tag("flow", flow)
                .description("Number of successful OAuth logins by flow")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record a failed OAuth login.
     * 
     * @param provider OAuth provider (e.g., "google")
     * @param reason Failure reason (e.g., "invalid_token", "user_error")
     */
    public void recordLoginFailure(String provider, String reason) {
        Counter.builder(OAUTH_LOGIN_FAILURE)
                .tag("provider", provider)
                .tag("reason", reason)
                .description("Number of failed OAuth logins")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record a failed OAuth login with flow type.
     * 
     * @param provider OAuth provider (e.g., "google")
     * @param flow OAuth flow type (e.g., "web", "mobile")
     * @param reason Failure reason (e.g., "invalid_token", "user_error")
     */
    public void recordLoginFailure(String provider, String flow, String reason) {
        Counter.builder(OAUTH_LOGIN_FAILURE)
                .tag("provider", provider)
                .tag("flow", flow)
                .tag("reason", reason)
                .description("Number of failed OAuth logins by flow")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record a new user registration via OAuth.
     * 
     * @param provider OAuth provider (e.g., "google")
     */
    public void recordNewUserRegistration(String provider) {
        Counter.builder(OAUTH_NEW_USER_REGISTRATION)
                .tag("provider", provider)
                .description("Number of new users registered via OAuth")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record an auto-linking event (existing user linked to OAuth provider).
     * 
     * @param provider OAuth provider (e.g., "google")
     */
    public void recordAutoLinking(String provider) {
        Counter.builder(OAUTH_AUTO_LINKING)
                .tag("provider", provider)
                .description("Number of auto-linking events")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Create a timer sample for measuring OAuth flow duration.
     * Call stop() on the returned sample when the flow completes.
     * 
     * @param provider OAuth provider (e.g., "google")
     * @return Timer.Sample to be stopped when flow completes
     */
    public Timer.Sample startFlowTimer(String provider) {
        return Timer.start(meterRegistry);
    }
    
    /**
     * Stop the timer and record OAuth flow duration.
     * 
     * @param sample Timer sample from startFlowTimer()
     * @param provider OAuth provider (e.g., "google")
     * @param outcome Flow outcome (e.g., "success", "failure")
     */
    public void recordFlowDuration(Timer.Sample sample, String provider, String outcome) {
        sample.stop(Timer.builder(OAUTH_FLOW_DURATION)
                .tag("provider", provider)
                .tag("outcome", outcome)
                .description("Duration of OAuth authentication flow")
                .register(meterRegistry));
    }
    
    /**
     * Stop the timer and record OAuth flow duration with flow type.
     * 
     * @param sample Timer sample from startFlowTimer()
     * @param provider OAuth provider (e.g., "google")
     * @param flow OAuth flow type (e.g., "web", "mobile")
     * @param outcome Flow outcome (e.g., "success", "failure")
     */
    public void recordFlowDuration(Timer.Sample sample, String provider, String flow, String outcome) {
        sample.stop(Timer.builder(OAUTH_FLOW_DURATION)
                .tag("provider", provider)
                .tag("flow", flow)
                .tag("outcome", outcome)
                .description("Duration of OAuth authentication flow by type")
                .register(meterRegistry));
    }
    
    // ========================================
    // Sentry Performance Monitoring
    // ========================================
    
    /**
     * Start a Sentry transaction for OAuth flow performance monitoring.
     * This creates a top-level transaction that tracks the entire OAuth flow.
     * 
     * @param provider OAuth provider (e.g., "google", "apple")
     * @return Sentry transaction to be finished when flow completes
     */
    public ITransaction startSentryTransaction(String provider) {
        ITransaction transaction = Sentry.startTransaction(
            "oauth.login",
            "oauth." + provider.toLowerCase()
        );
        transaction.setTag("provider", provider);
        return transaction;
    }
    
    /**
     * Start a child span within a Sentry transaction for specific OAuth operations.
     * Use this to track individual steps like token exchange, user info fetch, etc.
     * 
     * @param transaction Parent transaction
     * @param operation Operation name (e.g., "token_exchange", "user_info_fetch")
     * @return Sentry span to be finished when operation completes
     */
    public ISpan startSentrySpan(ITransaction transaction, String operation) {
        if (transaction != null) {
            return transaction.startChild("oauth." + operation);
        }
        return null;
    }
    
    /**
     * Finish a Sentry transaction with success status.
     * Call this when the OAuth flow completes successfully.
     * 
     * @param transaction Sentry transaction to finish
     */
    public void finishSentryTransactionSuccess(ITransaction transaction) {
        if (transaction != null) {
            transaction.setStatus(SpanStatus.OK);
            transaction.finish();
        }
    }
    
    /**
     * Finish a Sentry transaction with error status.
     * Call this when the OAuth flow fails.
     * 
     * @param transaction Sentry transaction to finish
     * @param throwable Error that occurred (optional, can be null)
     */
    public void finishSentryTransactionError(ITransaction transaction, Throwable throwable) {
        if (transaction != null) {
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            if (throwable != null) {
                transaction.setThrowable(throwable);
            }
            transaction.finish();
        }
    }
    
    /**
     * Finish a Sentry span (child operation).
     * 
     * @param span Sentry span to finish
     */
    public void finishSentrySpan(ISpan span) {
        if (span != null) {
            span.finish();
        }
    }
}

