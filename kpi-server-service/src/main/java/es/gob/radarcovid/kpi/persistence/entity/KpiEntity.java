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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "KPIS")
public class KpiEntity implements Serializable {
	private static final String SEQUENCE_NAME = "SQ_NM_ID_KPI";

	@Id
	@SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
	@Column(name = "NM_ID_KPI")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NM_ID_KPI_TYPE")
	private KpiTypeEntity kpiType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FC_CREATION_DATE")
	private Date createDate;

	@Column(name = "NM_VALUE")
	private Integer value;

	@Column(name = "NM_SO_TYPE")
	private Integer soType;

}
