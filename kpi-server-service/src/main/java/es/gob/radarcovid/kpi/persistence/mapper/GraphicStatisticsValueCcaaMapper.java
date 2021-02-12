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

import es.gob.radarcovid.kpi.api.GraphicStatisticsValueCcaaDto;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataCcaaWeekStatisticsEntity;

@Mapper(componentModel = "spring")
public abstract class GraphicStatisticsValueCcaaMapper {
	
    @Mappings({
	    @Mapping(target = "id", source = "id.ccaaId"),
	    @Mapping(target = "location", source = "ccaaName"),
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedAcc"),
	    @Mapping(target = "variation", source = "deliveredCodesPositiveConfirmed"),
	    @Mapping(target = "values", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueCcaaDto asDeliveredCodesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "id", source = "id.ccaaId"),
	    @Mapping(target = "location", source = "ccaaName"),
	    @Mapping(target = "value", source = "confirmedPositivesAcc"),
	    @Mapping(target = "variation", source = "confirmedPositives"),
	    @Mapping(target = "values", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueCcaaDto asConfirmedPositivesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "id", source = "id.ccaaId"),
	    @Mapping(target = "location", source = "ccaaName"),
	    @Mapping(target = "value", source = "deliveredCodesPositiveConfirmedAccRatio"),
	    @Mapping(target = "variation", source = "deliveredCodesPositiveConfirmedRatio"),
	    @Mapping(target = "values", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueCcaaDto asRatioDeliveredCodesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "id", source = "id.ccaaId"),
	    @Mapping(target = "location", source = "ccaaName"),
	    @Mapping(target = "value", source = "radarPositivesAcc"),
	    @Mapping(target = "variation", source = "radarPositives"),
	    @Mapping(target = "values", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueCcaaDto asRadarPositivesDto(ViewDataCcaaWeekStatisticsEntity entity);
    
    @Mappings({
	    @Mapping(target = "id", source = "id.ccaaId"),
	    @Mapping(target = "location", source = "ccaaName"),
	    @Mapping(target = "value", source = "radarPositivesAccRatio"),
	    @Mapping(target = "variation", source = "radarPositivesRatio"),
	    @Mapping(target = "values", ignore = true),
	    @Mapping(target = "date", ignore = true),
	    @Mapping(target = "sum", ignore = true),
	    @Mapping(target = "penetrationRate", ignore = true)
    })
    public abstract GraphicStatisticsValueCcaaDto asRatioRadarPositivesDto(ViewDataCcaaWeekStatisticsEntity entity);

}
