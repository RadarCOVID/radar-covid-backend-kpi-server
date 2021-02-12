/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "VIEW_DATA_TOTAL_WEEK_STATISTICS")
public class ViewDataTotalWeekStatisticsEntity implements Serializable {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false)),
		@AttributeOverride(name="week", column=@Column(name="WEEK", nullable = false))
	 })
	private ViewDataTotalWeekStatisticsIdEntity id;

    @Column(name = "YEAR_WEEK")
    private Integer yearWeek;
    
    @Column(name = "CONFIRMED_POSITIVES")
    private Long confirmedPositives;

    @Column(name = "CONFIRMED_POSITIVES_ACC")
    private Long confirmedPositivesAcc;

    @Column(name = "DELIVERED_CODES_POSITIVE_CONFIRMED")
    private Long deliveredCodesPositiveConfirmed;

    @Column(name = "DELIVERED_CODES_POSITIVE_CONFIRMED_ACC")
    private Long deliveredCodesPositiveConfirmedAcc;

    @Column(name = "DELIVERED_CODES_POSITIVE_CONFIRMED_RATIO")
    private Double deliveredCodesPositiveConfirmedRatio;

    @Column(name = "DELIVERED_CODES_POSITIVE_CONFIRMED_ACC_RATIO")
    private Double deliveredCodesPositiveConfirmedAccRatio;
    
    @Column(name = "POSITIVES_RADAR")
    private Long radarPositives;

    @Column(name = "POSITIVES_RADAR_ACC")
    private Long radarPositivesAcc;

    @Column(name = "POSITIVES_RADAR_RATIO")
    private Double radarPositivesRatio;

    @Column(name = "POSITIVES_RADAR_ACC_RATIO")
    private Double radarPositivesAccRatio;
    
}
