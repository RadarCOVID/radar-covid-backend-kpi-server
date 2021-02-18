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

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.kpi.api.AppleKpiTokenRequestDto;
import es.gob.radarcovid.kpi.api.AppleKpiTokenResponseDto;
import es.gob.radarcovid.kpi.api.ErrorDto;
import es.gob.radarcovid.kpi.api.KpiDto;
import es.gob.radarcovid.kpi.business.KpiService;
import es.gob.radarcovid.kpi.etc.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(AppleKpiController.APPLE_KPI_ROUTE)
@Validated
@RequiredArgsConstructor
@Slf4j
public class AppleKpiController {
	
	public static final String APPLE_KPI_ROUTE = "/apple";
	public static final String TOKEN_ROUTE = "/token";

	private final KpiService service;

	@Loggable
	@Secured({ Constants.AUTH_RADARCOVID })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Save KPIs data sent by the iOS device, in compliance with the monthly policy, authorized with the provided analytics token.",
			   security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "KPI data saved", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> saveKpi(
			Authentication authentication,
			@Parameter(description = "KPIs to be saved", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = KpiDto.class)))) 
			@Valid @RequestBody List<KpiDto> kpiDto) {
		String salt = authentication.getName();
		MDC.put(Constants.TRACKING, "KPI_APPLE|SALT:" + salt);
		service.saveApple(salt, kpiDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

	@Loggable
	@PostMapping(value = AppleKpiController.TOKEN_ROUTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Authorize the provided KPI token with the device authenticity SDK offered by the device operating system.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Token has been authorized already", content = @Content(schema = @Schema(implementation = AppleKpiTokenResponseDto.class))),
			@ApiResponse(responseCode = "202", description = "Token is being authorizing", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "401", description = "Invalid token device", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<?> verifyToken(
			@Parameter(description = "Token to be evaluated", required = true, schema = @Schema(implementation = AppleKpiTokenRequestDto.class)) 
			@Valid @RequestBody AppleKpiTokenRequestDto appleKpiTokenRequestDto) {
		MDC.put(Constants.TRACKING, "TOKEN_APPLE|DEVICE:" + StringUtils.abbreviate(appleKpiTokenRequestDto.getDeviceToken(), 48));
		Optional<String> result = service.verifyToken(appleKpiTokenRequestDto.getDeviceToken());
        if (result.isPresent()) {
            log.debug("Token device is already authorized");
            AppleKpiTokenResponseDto response = new AppleKpiTokenResponseDto(result.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
        	log.debug("Token device is not authorized yet");
        	service.authenticateToken(appleKpiTokenRequestDto.getDeviceToken());
        	return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        }
	}

}
