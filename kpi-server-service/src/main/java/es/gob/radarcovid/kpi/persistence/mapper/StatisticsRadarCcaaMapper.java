/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.gob.radarcovid.kpi.domain.StatisticsRadarCcaaDto;
import es.gob.radarcovid.kpi.persistence.entity.StatisticsRadarCcaaEntity;

@Mapper(componentModel = "spring", uses = CcaaMapper.class)
public interface StatisticsRadarCcaaMapper {
	
    StatisticsRadarCcaaDto asDto(StatisticsRadarCcaaEntity entity);

    @Mapping(target = "id", ignore = true)
    StatisticsRadarCcaaEntity asEntity(StatisticsRadarCcaaDto dto);

}
