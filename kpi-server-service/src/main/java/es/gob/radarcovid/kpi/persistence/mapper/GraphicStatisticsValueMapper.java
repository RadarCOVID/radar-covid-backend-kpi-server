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

import es.gob.radarcovid.kpi.api.GraphicStatisticsValueDto;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataCcaaMonthStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataCcaaWeekStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataMonthStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataTotalMonthStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataTotalWeekStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataWeekStatisticsEntity;

@Mapper(componentModel = "spring")
public abstract class GraphicStatisticsValueMapper {
	
    @Mappings({
	    @Mapping(target = "value", source = "entity", qualifiedByName = "totalDownloads"),
	    @Mapping(target = "sum", source = "entity", qualifiedByName = "accumulatedDownloads"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asDownloadsDto(ViewDataWeekStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "entity", qualifiedByName = "totalDownloads"),
	    @Mapping(target = "sum", source = "entity", qualifiedByName = "accumulatedDownloads"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asDownloadsDto(ViewDataMonthStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "radarPositives"),
	    @Mapping(target = "sum", source = "radarPositivesAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asPositvesDto(ViewDataWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "radarPositives"),
	    @Mapping(target = "sum", source = "radarPositivesAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asPositvesDto(ViewDataMonthStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asDeliveredCodesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asDeliveredCodesDto(ViewDataCcaaMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asDeliveredCodesDto(ViewDataTotalWeekStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asDeliveredCodesDto(ViewDataTotalMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asAccDeliveredCodesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedRatio"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioDeliveredCodesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedRatio"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioDeliveredCodesDto(ViewDataCcaaMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedRatio"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioDeliveredCodesDto(ViewDataTotalWeekStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedRatio"),
	    @Mapping(target = "sum", source = "deliveredCodesPositiveConfirmedAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioDeliveredCodesDto(ViewDataTotalMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "radarPositives"),
	    @Mapping(target = "sum", source = "radarPositivesAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRadarPositivesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "radarPositives"),
	    @Mapping(target = "sum", source = "radarPositivesAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRadarPositivesDto(ViewDataCcaaMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "radarPositives"),
	    @Mapping(target = "sum", source = "radarPositivesAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRadarPositivesDto(ViewDataTotalWeekStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "radarPositives"),
	    @Mapping(target = "sum", source = "radarPositivesAcc"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRadarPositivesDto(ViewDataTotalMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "radarPositivesRatio"),
	    @Mapping(target = "sum", source = "radarPositivesAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioRadarPositivesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "value", source = "radarPositivesRatio"),
	    @Mapping(target = "sum", source = "radarPositivesAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioRadarPositivesDto(ViewDataCcaaMonthStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "radarPositivesRatio"),
	    @Mapping(target = "sum", source = "radarPositivesAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioRadarPositivesDto(ViewDataTotalWeekStatisticsEntity entity);

    @Mappings({
	    @Mapping(target = "value", source = "radarPositivesRatio"),
	    @Mapping(target = "sum", source = "radarPositivesAccRatio"),
	    @Mapping(target = "date", source = "entity", qualifiedByName = "dateDownloads")
    })
    public abstract GraphicStatisticsValueDto asRatioRadarPositivesDto(ViewDataTotalMonthStatisticsEntity entity);

    @Named("totalDownloads")
    public static long totalDownloads(ViewDataWeekStatisticsEntity entity) {
    	return entity.getAndroidDownloads() + entity.getIosDownloads();
    }
    
    @Named("totalDownloads")
    public static long totalDownloads(ViewDataMonthStatisticsEntity entity) {
    	return entity.getAndroidDownloads() + entity.getIosDownloads();
    }
    
    @Named("accumulatedDownloads")
    public static long accumulatedDownloads(ViewDataWeekStatisticsEntity entity) {
        return entity.getAndroidDownloadsAcc() + entity.getIosDownloadsAcc();
    }
    
    @Named("accumulatedDownloads")
    public static long accumulatedDownloads(ViewDataMonthStatisticsEntity entity) {
        return entity.getAndroidDownloadsAcc() + entity.getIosDownloadsAcc();
    }
    
    @Named("dateDownloads")
    public static Date dateDownloads(ViewDataWeekStatisticsEntity entity) {
    	return weekDate(entity.getId().getYear().intValue(), entity.getId().getWeek().intValue());
    }
    
    @Named("dateDownloads")
    public static Date dateDownloads(ViewDataMonthStatisticsEntity entity) {
    	return monthDate(entity.getId().getYear().intValue(), entity.getId().getMonth().intValue());
    }
    
	@Named("dateDownloads")
	public static Date dateDownloads(ViewDataCcaaWeekStatisticsEntity entity) {
		return weekDate(entity.getId().getYear().intValue(), entity.getId().getWeek().intValue());
	}
    
    @Named("dateDownloads")
    public static Date dateDownloads(ViewDataCcaaMonthStatisticsEntity entity) {
    	return monthDate(entity.getId().getYear().intValue(), entity.getId().getMonth().intValue());
    }

	@Named("dateDownloads")
	public static Date dateDownloads(ViewDataTotalWeekStatisticsEntity entity) {
		return weekDate(entity.getId().getYear().intValue(), entity.getId().getWeek().intValue());
	}

	@Named("dateDownloads")
    public static Date dateDownloads(ViewDataTotalMonthStatisticsEntity entity) {
    	return monthDate(entity.getId().getYear().intValue(), entity.getId().getMonth().intValue());
    }

    private static Date weekDate(int year, int week) {
    	LocalDate date = LocalDate.now()
    			.withYear(year)
    			.with(WeekFields.ISO.weekOfYear(), week)
    			.with(DayOfWeek.MONDAY);
    	return Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC));
    }
    
    private static Date monthDate(int year, int month) {
    	LocalDate date = LocalDate.now()
    			.withYear(year)
    			.withMonth(month)
    			.withDayOfMonth(1);
    	return Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC));
    }
}
