/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "verificationEntityManagerFactory", 
                       transactionManagerRef = "verificationTransactionManager", 
                       basePackages = { "es.gob.radarcovid.verification.persistence.repository" })
public class DataSourceVerificationConfiguration {
	
	public final static String PACKAGES_NAME = "es.gob.radarcovid.verification.persistence.entity";
	public final static String PERSISTENCE_UNIT_NAME = "verification";
	public final static String DEFAULT_SCHEMA_NAME = "verification";
	
    @Bean
    @ConfigurationProperties("spring.datasource-verification")
    public DataSourceProperties verificationDataSourceProperties() {
        return new DataSourceProperties();
    }
	
	@Bean	
	@ConfigurationProperties("spring.datasource-verification.hikari")
	public DataSource verificationDataSource(@Qualifier("verificationDataSourceProperties") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean
	@ConfigurationProperties("spring.jpa-verification")
	public JpaProperties verificationJpaProperties() {
		return new JpaProperties();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean verificationEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("verificationDataSource") DataSource dataSource,
			@Qualifier("verificationJpaProperties") JpaProperties jpaProperties) {
		return builder.dataSource(dataSource).packages(PACKAGES_NAME).persistenceUnit(PERSISTENCE_UNIT_NAME)
				.properties(jpaProperties.getProperties()).build();
	}

	@Bean
	public PlatformTransactionManager verificationTransactionManager(
			@Qualifier("verificationEntityManagerFactory") EntityManagerFactory verificationEntityManagerFactory) {
		return new JpaTransactionManager(verificationEntityManagerFactory);
	}

}
