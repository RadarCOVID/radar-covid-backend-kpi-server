/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class GraphicStatisticsAttributeDto implements Serializable {

	public String title;
	public String type;
	public String description;

	@Schema(description = "Statistics values", oneOf = { GraphicStatisticsValueDto.class,
			GraphicStatisticsValueTotalDto.class, GraphicStatisticsValueSoDto.class, GraphicStatisticsValueCcaaDto.class })
	public List<GraphicStatisticsValueDto> values;

	@Schema(description = "Last update", pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC", locale = "es-ES")
	public Date lastUpdate;

}
