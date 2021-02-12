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
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.kpi.api.ErrorDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsDto;
import es.gob.radarcovid.kpi.api.StatisticsDateDto;
import es.gob.radarcovid.kpi.business.StatisticsService;
import es.gob.radarcovid.kpi.etc.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
	
	private final StatisticsService service; 

    @Loggable
    @GetMapping(value = "/basics", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get basics statistics")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get defined statistics", content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatisticsDateDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<List<StatisticsDateDto>> getStatistics() {
    	//TODO Retornar un unico objeto en lugar de un array, con los ultimos totales
    	//TODO Cambiar estructura json retornando unicamente fecha, total contagios, total descargas.
    	MDC.put(Constants.TRACKING, "BASIC_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getLastStatistics().stream().collect(Collectors.toList()));
    }
    
    @Loggable
    @GetMapping(value = "/downloads", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get application downloads statistics")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get downloads statistics", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GraphicStatisticsDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<GraphicStatisticsDto> getDownloads() {
    	MDC.put(Constants.TRACKING, "DOWNLOADS_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getDownloadsStatistics());
    }
    
    @Loggable
    @GetMapping(value = "/codes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get delivered codes to confirmed cases statistics")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get delivered codes statistics", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GraphicStatisticsDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<GraphicStatisticsDto> getCodes() {
    	MDC.put(Constants.TRACKING, "CODES_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getDeliveredCodesStatistics());
    }
    
    @Loggable
    @GetMapping(value = "/codes/ratio", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get ratio delivered codes to confirmed cases statistics")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get ratio delivered codes statistics", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GraphicStatisticsDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<GraphicStatisticsDto> getRatioCodes() {
    	MDC.put(Constants.TRACKING, "CODES_RATIO_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getRatioCodeStatistics());
    }
    
    @Loggable
    @GetMapping(value = "/positives", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get RadarCOVID positives statistics")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get positives statistics", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GraphicStatisticsDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<GraphicStatisticsDto> getPositives() {
    	MDC.put(Constants.TRACKING, "POSITIVES_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getRadarPositivesStatistics());
    }
    
    @Loggable
    @GetMapping(value = "/positives/ccaa", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get RadarCOVID positives statistics by CCAA")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get positives statistics by CCAA", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GraphicStatisticsDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<GraphicStatisticsDto> getCCAAPositives() {
    	MDC.put(Constants.TRACKING, "POSITIVES_CCAA_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getCcaaRadarPositivesStatistics());
    }
    
    @Loggable
    @GetMapping(value = "/positives/ccaa/ratio", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get ratio RadarCOVID positives statistics by CCAA")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Get ratio positives statistics by CCAA", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GraphicStatisticsDto.class)))),
           @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
           @ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    public ResponseEntity<GraphicStatisticsDto> getRatioCCAAPositives() {
    	MDC.put(Constants.TRACKING, "POSITIVES_CCAA_RATIO_STATS");
        return ResponseEntity.status(HttpStatus.OK).body(service.getRatioCcaaRadarPositivesStatistics());
    }
}
