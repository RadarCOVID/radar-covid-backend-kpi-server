/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GraphicStatisticsTypeEnum {

    DOWNLOADS("descargas_radar"),
    DOWNLOADS_TOTAL("total/variacion"),
    DOWNLOADS_MONTH("mensual_descargas"),
    DOWNLOADS_WEEK("semanal_descargas"),
    DOWNLOADS_SO("acumulado_sistemas_operativo"),
    
    DELIVERED_CODES("codigos_entregados_confirmados"),
    DELIVERED_CODES_TOTAL("total/variacion"),
    DELIVERED_CODES_ACCUMULATED("acumulado_entregados"),
    DELIVERED_CODES_CONFIRMED("acumulado_confirmados"),
    DELIVERED_CODES_MONTH("mensual_codigos_entregados"),
    DELIVERED_CODES_WEEK("semanal_codigos_entregados"),
    
    RATIO_DELIVERED_CODES("ratio_entregados_confirmados"),
    RATIO_DELIVERED_CODES_ACCUMULATED("ratio_acumulado_entregados_confirmado"),
    RATIO_DELIVERED_CODES_MONTH("ratio_mensual_codigos_entregados"),
    RATIO_DELIVERED_CODES_WEEK("ratio_semanal_codigos_entregados"),
    RATIO_DELIVERED_CODES_ACCUMULATED_NATIONAL_MONTH("ratio_acumulado_entregados_confirmado_mensual_nacional"),
    RATIO_DELIVERED_CODES_ACCUMULATED_NATIONAL_WEEK("ratio_acumulado_entregados_confirmado_semanal_nacional"),
    
    POSITIVES("casos_positivos"),
    POSITIVES_TOTAL("total/variacion"),
    POSITIVES_MONTH("mensual_casos_positivos"),
    POSITIVES_WEEK("semanal_casos_positivos"),
    
    CCAA_POSITIVES("casos_positivos"),
    CCAA_POSITIVES_TOTAL("total/variacion"),
    CCAA_POSITIVES_ACCUMULATED("acumulado_casos_positivos"),
    CCAA_POSITIVES_MONTH("mensual_casos_positivos"),
    CCAA_POSITIVES_WEEK("semanal_casos_positivos"),
    
    RATIO_CCAA_POSITIVES("ratio_casos_positivos"),
    RATIO_CCAA_POSITIVES_ACCUMULATED("ratio_acumulado_positivos_confirmados"),
    RATIO_CCAA_POSITIVES_MONTH("ratio_mensual_casos_positivos"),
    RATIO_CCAA_POSITIVES_WEEK("ratio_semanal_casos_positivos"),
    RATIO_CCAA_POSITIVES_ACCUMULATED_NATIONAL_MONTH("ratio_acumulado_positivos_confirmados_mensual_nacional"),
    RATIO_CCAA_POSITIVES_ACCUMULATED_NATIONAL_WEEK("ratio_acumulado_positivos_confirmados_semanal_nacional");
	
    @Getter
    @JsonValue
    private final String value;
    
}
