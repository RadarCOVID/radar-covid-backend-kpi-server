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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.gob.radarcovid.kpi.client.DeviceCheckClientService;
import es.gob.radarcovid.kpi.client.impl.DeviceCheckCircuitBreakerClientImpl;
import es.gob.radarcovid.kpi.client.impl.DeviceCheckClientServiceImpl;
import es.gob.radarcovid.kpi.client.model.DeviceCheckResponseDto;
import es.gob.radarcovid.kpi.client.rest.DeviceCheckRestRetryableClientServiceImpl;
import es.gob.radarcovid.kpi.client.rest.DeviceCheckRestRetryableFakeClientServiceImpl;
import es.gob.radarcovid.kpi.etc.KpiProperties;
import es.gob.radarcovid.kpi.security.JwtGenerator;

@Configuration
@EnableRetry
public class DeviceCheckClientConfiguration {

	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@ConditionalOnProperty(name = "application.kpi.device-check.simulate.enabled", havingValue = "false", matchIfMissing = true)
	@Bean("deviceCheckRestRetryableClientService")
	DeviceCheckClientService deviceCheckRestRetryableClientService(RestTemplate restTemplate,
			KpiProperties kpiProperties, JwtGenerator jwtGenerator, ObjectMapper objectMapper) {
		return new DeviceCheckRestRetryableClientServiceImpl(restTemplate, kpiProperties, jwtGenerator, objectMapper);
	}
	
    @ConditionalOnProperty(name = "application.kpi.device-check.simulate.enabled", havingValue = "true")
    @Bean("deviceCheckResponseMap")
    Map<String, DeviceCheckResponseDto> deviceCheckResponseMap() {
        return new HashMap<>();
    }
	
	@ConditionalOnProperty(name = "application.kpi.device-check.simulate.enabled", havingValue = "true")
	@Bean("deviceCheckRestRetryableClientService")
	DeviceCheckClientService deviceCheckRestRetryableFakeClientService(
			@Qualifier("deviceCheckResponseMap") Map<String, DeviceCheckResponseDto> deviceCheckResponseMap) {
		return new DeviceCheckRestRetryableFakeClientServiceImpl(deviceCheckResponseMap);
	}

	@Bean
	DeviceCheckClientService deviceCheckCircuitBreakerClient(
			@Qualifier("deviceCheckRestRetryableClientService") DeviceCheckClientService deviceCheckClientService) {
		return new DeviceCheckCircuitBreakerClientImpl(deviceCheckClientService);
	}

	@Bean
	@Primary
	DeviceCheckClientService deviceCheckClientService(
			@Qualifier("deviceCheckCircuitBreakerClient") DeviceCheckClientService deviceCheckClientService) {
		return new DeviceCheckClientServiceImpl(deviceCheckClientService);
	}

}
