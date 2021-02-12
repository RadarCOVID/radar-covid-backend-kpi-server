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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import es.gob.radarcovid.kpi.vo.SoTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KpiDto implements Serializable {

    private String kpi;
    
    @Schema(description = "KPI date", pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "UTC", locale = "es-ES")
    private Date timestamp;
    
    private Integer value;
    
    @JsonIgnore
    private SoTypeEnum soType;

}
