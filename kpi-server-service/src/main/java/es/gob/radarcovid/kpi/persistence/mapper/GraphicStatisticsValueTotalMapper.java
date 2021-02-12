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
import org.mapstruct.Named;

import es.gob.radarcovid.kpi.api.GraphicStatisticsValueTotalDto;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataWeekStatisticsEntity;

@Mapper(componentModel = "spring")
public abstract class GraphicStatisticsValueTotalMapper {
	
	private static final double TOTAL_POPULATION_OVER_14_AGE = 40950956;
    
    @Mappings({
	    @Mapping(target = "value", source = "entity", qualifiedByName = "totalDownloads"),
	    @Mapping(target = "variation", source = "entity", qualifiedByName = "variationDownloads"),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "penetrationRate", source = "entity", qualifiedByName = "penetrationRate")
    })
    public abstract GraphicStatisticsValueTotalDto asDownloadsDto(ViewDataWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "variation", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueTotalDto asDeliveredCodesDto(ViewDataWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "radarPositivesAcc"),
	    @Mapping(target = "variation", source = "radarPositives"),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueTotalDto asPositivesDto(ViewDataWeekStatisticsEntity entity);
    
    @Named("totalDownloads")
    public static long totalDownloads(ViewDataWeekStatisticsEntity entity) {
        return entity.getAndroidDownloadsAcc() + entity.getIosDownloadsAcc();
    }
    
    @Named("variationDownloads")
    public static long variationDownloads(ViewDataWeekStatisticsEntity entity) {
        return entity.getAndroidDownloads() + entity.getIosDownloads();
    }
    
    @Named("penetrationRate")
    public static double penetrationRate(ViewDataWeekStatisticsEntity entity) {
        return totalDownloads(entity) * 100 / TOTAL_POPULATION_OVER_14_AGE;
    }

}
