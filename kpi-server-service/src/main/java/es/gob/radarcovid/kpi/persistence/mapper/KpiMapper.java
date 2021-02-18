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


import java.util.Date;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import es.gob.radarcovid.kpi.api.KpiDto;
import es.gob.radarcovid.kpi.persistence.entity.KpiEntity;

@Mapper(componentModel = "spring")
public abstract class KpiMapper {

    @Mappings({
    	@Mapping(target = "kpi", source = "kpiType.name"),
    	@Mapping(target = "timestamp", ignore = true),
    	@Mapping(target = "soType", ignore = true)
    })
    public abstract KpiDto asDto(KpiEntity entity);

    @Mappings({
    	@Mapping(target = "id", ignore = true),
    	@Mapping(target = "kpiType", ignore = true),
    	@Mapping(target = "createDate", ignore = true),
    	@Mapping(source = "soType.id", target = "soType"),
    	@Mapping(source = "timestamp", target = "dateValue")
    })
    public abstract KpiEntity asEntity(KpiDto dto);
    
    @AfterMapping
    protected void setInternalValues(KpiDto dto, @MappingTarget KpiEntity entity) {
        entity.setCreateDate(new Date());
    }

}
