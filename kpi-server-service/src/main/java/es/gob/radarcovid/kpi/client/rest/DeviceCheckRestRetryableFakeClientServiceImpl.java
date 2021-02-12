/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.client.rest;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

import es.gob.radarcovid.kpi.client.DeviceCheckClientService;
import es.gob.radarcovid.kpi.client.model.DeviceCheckResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DeviceCheckRestRetryableFakeClientServiceImpl implements DeviceCheckClientService {
	
    @Value("${application.kpi.device-check.simulate.valid: true}")
    private boolean isValid;
	
    private final Map<String, DeviceCheckResponseDto> deviceCheckResponseMap;

    @Override
    public Optional<DeviceCheckResponseDto> queryTwoBits(String token) {
        log.debug("Entering DeviceCheckRestRetryableFakeClientServiceImpl.queryTwoBits('{}')", token);
        Optional<DeviceCheckResponseDto> result = Optional.ofNullable(deviceCheckResponseMap.get(token));
        log.debug("Leaving DeviceCheckRestRetryableFakeClientServiceImpl.queryTwoBits with: {}", result);
        return result;
    }
    
	@Override
	public boolean updateTwoBits(String token, boolean bit0, boolean bit1) {
        log.debug("Entering DeviceCheckRestRetryableFakeClientServiceImpl.updateTwoBits('{}', '{}', '{}')", token, bit0, bit1);
        DeviceCheckResponseDto response = DeviceCheckResponseDto.builder().bit0(bit0).bit1(bit1).lastUpdateTime(new Date()).build();
        deviceCheckResponseMap.put(token, response);
        log.debug("Leaving DeviceCheckRestRetryableFakeClientServiceImpl.updateTwoBits with: {}", true);
        return true;
	}

	@Override
	public boolean validate(String token) {
        log.debug("Entering DeviceCheckRestRetryableFakeClientServiceImpl.validate('{}')", token);
        boolean result = isValid;
        log.debug("Leaving DeviceCheckRestRetryableFakeClientServiceImpl.validate with: {}", result);
        return result;
	}
    
}
