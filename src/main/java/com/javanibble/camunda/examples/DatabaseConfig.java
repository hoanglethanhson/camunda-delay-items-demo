package com.javanibble.camunda.examples;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "backendEntityManagerFactory",
        transactionManagerRef = "backendTransactionManager",
        basePackages = {"vn.com.viettel.vds.contact.repository"}
)
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.backend")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "backendDataSource")
    @ConfigurationProperties(prefix = "app.datasource.backend.configuration")
    public DataSource dataSource() {
        return defaultDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "backendEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean contactEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("backendDataSource") DataSource dataSource
    ) {
        return builder.dataSource(dataSource)
                .packages("vn.com.viettel.vds.contact.entity")
                .persistenceUnit("backend")
                .build();
    }

    @Primary
    @Bean(name = "backendTransactionManager")
    public PlatformTransactionManager contactTransactionManager(
            @Qualifier("backendEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}