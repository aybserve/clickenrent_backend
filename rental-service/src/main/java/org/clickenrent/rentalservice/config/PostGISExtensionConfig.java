package org.clickenrent.rentalservice.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Ensures the PostGIS extension is installed BEFORE Hibernate DDL runs.
 *
 * <p>Hibernate's {@code ddl-auto=update} needs the PostGIS {@code geography} type
 * to be available when creating/updating the {@code coordinates} table's geom column.
 * This config uses a {@link BeanFactoryPostProcessor} to hook into the
 * {@code entityManagerFactory} initialization and install PostGIS first.</p>
 */
@Configuration
public class PostGISExtensionConfig {

    /**
     * A marker bean that installs PostGIS extension.
     * entityManagerFactory is made to depend on this via the BeanFactoryPostProcessor below.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Object postGISExtensionInitializer(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE EXTENSION IF NOT EXISTS postgis");
        } catch (Exception e) {
            // Log but don't fail — PostGIS might already exist or not be available
            // on all environments (e.g., H2 test databases)
            System.err.println("PostGIS extension setup: " + e.getMessage());
        }
        return new Object(); // marker bean
    }

    /**
     * Makes entityManagerFactory depend on postGISExtensionInitializer,
     * ensuring PostGIS is available before Hibernate DDL.
     */
    @Bean
    static BeanFactoryPostProcessor postGISDependencyProcessor() {
        return (ConfigurableListableBeanFactory beanFactory) -> {
            if (beanFactory.containsBeanDefinition("entityManagerFactory")) {
                BeanDefinition bd = beanFactory.getBeanDefinition("entityManagerFactory");
                String[] existing = bd.getDependsOn();
                if (existing == null) {
                    bd.setDependsOn("postGISExtensionInitializer");
                } else {
                    String[] updated = new String[existing.length + 1];
                    System.arraycopy(existing, 0, updated, 0, existing.length);
                    updated[existing.length] = "postGISExtensionInitializer";
                    bd.setDependsOn(updated);
                }
            }
        };
    }
}
