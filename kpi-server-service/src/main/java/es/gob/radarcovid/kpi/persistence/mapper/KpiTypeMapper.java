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

import es.gob.radarcovid.kpi.api.KpiTypeDto;
import es.gob.radarcovid.kpi.persistence.entity.KpiTypeEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KpiTypeMapper {
    KpiTypeDto asDto(KpiTypeEntity entity);

    KpiTypeEntity asEntity(KpiTypeDto dto);

    default List<KpiTypeDto> asListDto(List<KpiTypeEntity> entityList) {
        return entityList.stream().map(entity -> asDto(entity)).collect(Collectors.toList());
    }

}
