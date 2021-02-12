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
import org.mapstruct.Mappings;

import es.gob.radarcovid.kpi.api.GraphicStatisticsValueSoDto;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataWeekStatisticsEntity;

@Mapper(componentModel = "spring")
public abstract class GraphicStatisticsValueSoMapper {
    
    @Mappings({
	    @Mapping(target = "value", source = "androidDownloadsAcc"),
	    @Mapping(target = "label", constant = "Android"),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "date", ignore = true)
    })
    public abstract GraphicStatisticsValueSoDto asAndroidDto(ViewDataWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "iosDownloadsAcc"),
	    @Mapping(target = "label", constant = "iOS"),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "date", ignore = true)
    })
    public abstract GraphicStatisticsValueSoDto asIosDto(ViewDataWeekStatisticsEntity entity);
    
}
