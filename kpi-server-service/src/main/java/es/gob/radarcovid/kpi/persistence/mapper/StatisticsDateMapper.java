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


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.Date;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import es.gob.radarcovid.kpi.api.StatisticsDateDto;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataWeekStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataWeekStatisticsIdEntity;

@Mapper(componentModel = "spring")
public interface StatisticsDateMapper {

    @Mappings({
            @Mapping(target = "date", source = "id", qualifiedByName = "weekDate"),
            @Mapping(target = "applicationsDownloads.totalAcummulated", source = "entity", qualifiedByName = "totalDownloads"),
            @Mapping(target = "communicatedContagions.totalAcummulated", source = "radarPositivesAcc")
    })
    StatisticsDateDto asDto(ViewDataWeekStatisticsEntity entity);
    
    @Named("weekDate")
    public static Date weekDate(ViewDataWeekStatisticsIdEntity id) {
    	LocalDate date = LocalDate.now()
    			.withYear(id.getYear().intValue())
    			.with(WeekFields.ISO.weekOfYear(), id.getWeek().intValue())
    			.with(DayOfWeek.SUNDAY);
    	return Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC));
    }
    
    @Named("totalDownloads")
    public static long totalDownloads(ViewDataWeekStatisticsEntity entity) {
        return entity.getAndroidDownloadsAcc() + entity.getIosDownloadsAcc();
    }

}
