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
@Table(name = "VIEW_DATA_MONTH_STATISTICS")
public class ViewDataMonthStatisticsEntity implements Serializable {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "year", column = @Column(name = "YEAR", nullable = false)),
        @AttributeOverride(name="month", column=@Column(name="MONTH", nullable = false) )
	 })
	private ViewDataMonthStatisticsIdEntity id;

    @Column(name = "YEAR_MONTH")
    private Integer yearMonth;
    
    @Column(name = "DOWNLOADS_ANDROID")
    private Long androidDownloads;

    @Column(name = "DOWNLOADS_ANDROID_ACC")
    private Long androidDownloadsAcc;

    @Column(name = "DOWNLOADS_IOS")
    private Long iosDownloads;

    @Column(name = "DOWNLOADS_IOS_ACC")
    private Long iosDownloadsAcc;

    @Column(name = "DELIVERED_CODES_POSITIVE_CONFIRMED")
    private Long deliveredCodesPositiveConfirmed;

    @Column(name = "DELIVERED_CODES_POSITIVE_CONFIRMED_ACC")
    private Long deliveredCodesPositiveConfirmedAcc;

    @Column(name = "POSITIVES_RADAR")
    private Long radarPositives;

    @Column(name = "POSITIVES_RADAR_ACC")
    private Long radarPositivesAcc;

}
