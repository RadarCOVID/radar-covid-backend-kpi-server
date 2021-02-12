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

import java.util.Optional;

import es.gob.radarcovid.kpi.api.StatisticsDateDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsDto;

public interface StatisticsService {
	
	void updateStatistics();
	
    Optional<StatisticsDateDto> getLastStatistics();
    
    GraphicStatisticsDto getDownloadsStatistics();
    
    GraphicStatisticsDto getDeliveredCodesStatistics();
    
    GraphicStatisticsDto getRatioCodeStatistics();
    
    GraphicStatisticsDto getRadarPositivesStatistics();
    
    GraphicStatisticsDto getCcaaRadarPositivesStatistics();
    
    GraphicStatisticsDto getRatioCcaaRadarPositivesStatistics();    
    
}
