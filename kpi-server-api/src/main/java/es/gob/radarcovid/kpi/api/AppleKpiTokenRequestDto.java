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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import es.gob.radarcovid.kpi.api.validation.Base64Constraint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AppleKpiTokenRequestDto implements Serializable {
	
	@NotNull
	@Size(max = 10000)
	@Base64Constraint
    @Schema(description = "Device token to be authorized", required = true)
	private String deviceToken;
	
}
