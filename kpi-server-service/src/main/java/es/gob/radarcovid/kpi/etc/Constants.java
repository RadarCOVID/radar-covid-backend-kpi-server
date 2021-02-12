/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.etc;

import java.time.LocalDateTime;

public class Constants {
	
    public static final String AUTHORIZATION_HEADER = "x-sedia-authorization";
    public static final String AUTHORIZATION_PREFIX = "";
    
    public static final String AUTH_RADARCOVID = "ROLE_RADARCOVID";
    
    public static final String API_KEY_AUTH = "apiKeyAuth";

    public static final String CACHE_FIND_KPI_TYPE_BY_NAME_AND_ENABLED = "findKpiTypeByNameAndEnabled";
    public static final String CACHE_KPI_TYPE_ENABLED = "kpiTypeEnabled";    
    public static final String CACHE_SALT = "salt";
    public static final String CACHE_AUTHENTICATION = "authenticate";
    
    public static final String PAIR_KEY_RADAR = "radar";
    public static final String PAIR_KEY_DEVICE_CHECK= "devicecheck";
    
    public static final String TRACKING = "TRACKING";

    public static final String DOWNLOADS_FILE_HEADER = "FECHA;DISPOSITIVO;DESCARGAS";
    public static final String DOWNLOADS_CCAA_FILE_HEADER =
            "FECHA;CA;CASOS CONFIRMADOS;COD SOLICITADOS;COD INTRODUCIDOS";
    public static final String CSV_SEPARATOR = ";";
    public static final String DEVICES_CSV = "devices.csv";
    public static final String CCAA_CSV = "ccaa.csv";
    public static final String ANDROID_NAME = "Android";
    public static final String IOS_NAME = "iOS";

    private Constants() {}
}
