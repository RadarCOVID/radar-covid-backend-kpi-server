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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "kpiEntityManagerFactory", 
                       transactionManagerRef = "kpiTransactionManager", 
                       basePackages = { "es.gob.radarcovid.kpi.persistence.repository" })
public class DataSourceKpiConfiguration {
	
	public final static String PACKAGES_NAME = "es.gob.radarcovid.kpi.persistence.entity";
	public final static String PERSISTENCE_UNIT_NAME = "kpi";
	
	@Primary
    @Bean
    @ConfigurationProperties("spring.datasource-kpi")
    public DataSourceProperties kpiDataSourceProperties() {
        return new DataSourceProperties();
    }
	
	@Primary
	@Bean	
	@ConfigurationProperties("spring.datasource-kpi.hikari")
	DataSource kpiDataSource(@Qualifier("kpiDataSourceProperties") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Primary
	@Bean
	@ConfigurationProperties("spring.jpa-kpi")
	public JpaProperties kpiJpaProperties() {
		return new JpaProperties();
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean kpiEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("kpiDataSource") DataSource dataSource,
			@Qualifier("kpiJpaProperties") JpaProperties jpaProperties) {
		return builder.dataSource(dataSource).packages(PACKAGES_NAME).persistenceUnit(PERSISTENCE_UNIT_NAME)
				.properties(jpaProperties.getProperties()).build();
	}

	@Primary
	@Bean
	public PlatformTransactionManager kpiTransactionManager(
			@Qualifier("kpiEntityManagerFactory") EntityManagerFactory kpiEntityManagerFactory) {
		return new JpaTransactionManager(kpiEntityManagerFactory);
	}

}
