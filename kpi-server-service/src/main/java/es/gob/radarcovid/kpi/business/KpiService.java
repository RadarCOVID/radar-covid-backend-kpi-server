/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.business;

import java.util.List;
import java.util.Optional;

import es.gob.radarcovid.kpi.api.GoogleKpiDto;
import es.gob.radarcovid.kpi.api.KpiDto;
import es.gob.radarcovid.kpi.api.KpiTypeDto;

public interface KpiService {
	
    List<KpiTypeDto> getKpiTypes();

    void save(List<KpiDto> dtoList);
    
    void saveGoogle(GoogleKpiDto googleKpiDto);
    
    void saveApple(String salt, List<KpiDto> dtoList);
    
    Optional<String> verifyToken(String deviceToken);
    
    void authenticateToken(String deviceToken);
}
