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

import es.gob.radarcovid.kpi.client.DeviceCheckClientService;
import es.gob.radarcovid.kpi.client.model.DeviceCheckResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DeviceCheckClientServiceImpl implements DeviceCheckClientService {
	
    private final DeviceCheckClientService seviceCheckClientService;

	@Override
	public Optional<DeviceCheckResponseDto> queryTwoBits(String token) {
        log.debug("Entering DeviceCheckClientServiceImpl.queryTwoBits('{}')", token);
        Optional<DeviceCheckResponseDto> result = seviceCheckClientService.queryTwoBits(token);
        log.debug("Leaving DeviceCheckClientServiceImpl.queryTwoBits() with: {}", result);
        return result;
	}

	@Override
	public boolean updateTwoBits(String token, boolean bit0, boolean bit1) {
        log.debug("Entering DeviceCheckClientServiceImpl.updateTwoBits('{}', '{}', '{}')", token, bit0, bit1);
        boolean result = seviceCheckClientService.updateTwoBits(token, bit0, bit1);
        log.debug("Leaving DeviceCheckClientServiceImpl.updateTwoBits() with: {}", result);
		return result;
	}

	@Override
	public boolean validate(String token) {
        log.debug("Entering DeviceCheckClientServiceImpl.validate('{}')", token);
        boolean result = seviceCheckClientService.validate(token);
        log.debug("Leaving DeviceCheckClientServiceImpl.validate() with: {}", result);
        return result;
	}
    
}
