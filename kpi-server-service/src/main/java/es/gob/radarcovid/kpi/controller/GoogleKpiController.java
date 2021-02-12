/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.controller;

import javax.validation.Valid;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.kpi.api.ErrorDto;
import es.gob.radarcovid.kpi.api.GoogleKpiDto;
import es.gob.radarcovid.kpi.business.KpiService;
import es.gob.radarcovid.kpi.etc.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/google")
@Validated
@RequiredArgsConstructor
public class GoogleKpiController {

	private final KpiService service;

	@Loggable
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Save KPIs data sent by the Android device, authorized with the provided SafetyNet hardware attestation token")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "KPI data saved", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) 
	})
	public ResponseEntity<Void> saveKpi(
			@Parameter(description = "KPIs to be saved", required = true, schema = @Schema(implementation = GoogleKpiDto.class)) @Valid @RequestBody GoogleKpiDto googleKpiDto) {
		MDC.put(Constants.TRACKING, "KPI_GOOGLE|SALT:" + googleKpiDto.getSalt());
		service.saveGoogle(googleKpiDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

}
