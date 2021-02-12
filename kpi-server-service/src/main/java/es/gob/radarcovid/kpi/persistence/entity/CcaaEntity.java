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
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "CCAA")
public class CcaaEntity implements Serializable {

    @Id
    @Column(name = "DE_CCAA_ID", length = 5)
    private String id;
    
    @Column(name = "DE_CCAA_NUM_ID", length = 2)
    private String numId;

    @Column(name = "DE_CCAA_NAME", length = 100)
    private String name;
    
    @Column(name = "FC_START_DATE")
    private LocalDate startDate;

}
