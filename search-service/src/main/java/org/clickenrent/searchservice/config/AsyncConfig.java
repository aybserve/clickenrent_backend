package org.clickenrent.searchservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for asynchronous indexing operations.
 * 
 * Enables @Async support for non-blocking bulk indexing and event processing.
 * 
 * @author Vitaliy Shvetsov
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${search.indexing.async-pool-size:5}")
    private int asyncPoolSize;

    @Bean(name = "indexingTaskExecutor")
    public Executor indexingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncPoolSize);
        executor.setMaxPoolSize(asyncPoolSize * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("indexing-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
