/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import es.gob.radarcovid.kpi.api.GraphicStatisticsValueDto;
import es.gob.radarcovid.kpi.api.StatisticsDateDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarCcaaDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarDto;
import es.gob.radarcovid.kpi.vo.GraphicStatisticsTypeEnum;

public interface StatisticsDao {

	Optional<StatisticsRadarDto> getLastStatistics();
	
    @Transactional
    void save(StatisticsRadarDto statisticsRadarDto);
    
    @Transactional
    void saveAll(List<StatisticsRadarCcaaDto> statisticsRadarCcaaDtos);
    
    @Transactional
    void updateConfirmedPositives(StatisticsRadarCcaaDto statisticsRadarCcaaDto);
	
    Optional<StatisticsDateDto> getLastStatisticsDate();
    
    Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getDownloadsStatistics();	
    
    Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getDeliveredCodesStatistics();

    Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getRatioDeliveredCodesStatistics();
    
    Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getRadarPositivesStatistics();
    
    Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getCcaaRadarPositivesStatistics();
    
    Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getRatioCcaaRadarPositivesStatistics();
    
}
