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
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import es.gob.radarcovid.kpi.api.validation.Base64Constraint;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GoogleKpiDto implements Serializable {

	@NotNull
	@Size(min = 24, max = 24)
	@Base64Constraint
	private String salt;
	
	@NotNull
	@Size(max = 10000)
	private String signedAttestation;
	
	@NotNull
	private List<KpiDto> kpis;
	
}
