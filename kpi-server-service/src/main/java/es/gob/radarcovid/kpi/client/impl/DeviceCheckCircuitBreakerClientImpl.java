/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.client.impl;

import java.util.Optional;

import org.springframework.retry.annotation.CircuitBreaker;

import es.gob.radarcovid.kpi.client.DeviceCheckClientService;
import es.gob.radarcovid.kpi.client.model.DeviceCheckResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DeviceCheckCircuitBreakerClientImpl implements DeviceCheckClientService {
	
    private final DeviceCheckClientService deviceCheckClientService;

    @Override
	@CircuitBreaker(maxAttemptsExpression = "#{${application.kpi.device-check.query.circuit-breaker.max-attempts:3}}", 
		openTimeoutExpression = "#{${application.kpi.device-check.circuit-breaker.query.open-timeout:5000}}")
	public Optional<DeviceCheckResponseDto> queryTwoBits(String token) {
        log.debug("Entering DeviceCheckCircuitBreakerClientImpl.queryTwoBits('{}')", token);
        Optional<DeviceCheckResponseDto> result = deviceCheckClientService.queryTwoBits(token);
        log.debug("Leaving DeviceCheckCircuitBreakerClientImpl.queryTwoBits with: {}", result);
        return result;
	}

	@Override
	@CircuitBreaker(maxAttemptsExpression = "#{${application.kpi.device-check.update.circuit-breaker.max-attempts:3}}", 
		openTimeoutExpression = "#{${application.kpi.device-check.update.circuit-breaker.open-timeout:5000}}")
	public boolean updateTwoBits(String token, boolean bit0, boolean bit1) {
        log.debug("Entering DeviceCheckCircuitBreakerClientImpl.updateTwoBits('{}', '{}', '{}')", token, bit0, bit1);
        boolean result = deviceCheckClientService.updateTwoBits(token, bit0, bit1);
        log.debug("Leaving DeviceCheckCircuitBreakerClientImpl.updateTwoBits with: {}", result);
        return result;
	}

	@Override
	@CircuitBreaker(maxAttemptsExpression = "#{${application.kpi.device-check.validate.circuit-breaker.max-attempts:3}}", 
	openTimeoutExpression = "#{${application.kpi.device-check.validate.circuit-breaker.open-timeout:5000}}")
	public boolean validate(String token) {
        log.debug("Entering DeviceCheckCircuitBreakerClientImpl.validate('{}')", token);
        boolean result = deviceCheckClientService.validate(token);
        log.debug("Leaving DeviceCheckCircuitBreakerClientImpl.validate with: {}", result);
        return result;
	}

}
