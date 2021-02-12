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

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.kpi.business.ExportService;
import es.gob.radarcovid.kpi.etc.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/export")
@AllArgsConstructor
public class ExportController {
    private final ExportService exportService;

    @Loggable
    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export downloads data in CSV format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get downloads data", content = @Content(array =
            @ArraySchema(schema = @Schema(implementation = Resource.class))))
    })  public ResponseEntity<Resource> exportDownloads() {
        MDC.put(Constants.TRACKING, "EXPORT_DEVICES");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+Constants.DEVICES_CSV)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(exportService.exportDownloads()));
    }

    @Loggable
    @GetMapping(value = "/ccaa", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export downloads by CCAA data in CSV format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get downloads data", content = @Content(array =
            @ArraySchema(schema = @Schema(implementation = Resource.class))))
    })  public ResponseEntity<Resource> exportDownloadsCcaa() {
        MDC.put(Constants.TRACKING, "EXPORT_CCAA");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+Constants.CCAA_CSV)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(exportService.exportCodes()));
    }
}
