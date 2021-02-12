/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.client;

import java.util.Optional;

import es.gob.radarcovid.kpi.client.model.DeviceCheckResponseDto;

public interface DeviceCheckClientService {

	Optional<DeviceCheckResponseDto> queryTwoBits(String token);
    
    boolean updateTwoBits(String token, boolean bit0, boolean bit1);
    
    boolean validate(String token);

}
