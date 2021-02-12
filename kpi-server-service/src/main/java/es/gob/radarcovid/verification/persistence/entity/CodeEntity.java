/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CODE")
public class CodeEntity implements Serializable {

    private static final String SEQUENCE_NAME = "SQ_NM_ID_CODE";

    @Id
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @Column(name = "NM_ID_CODE")
    private Long id;

    @Version
    @Column(name = "NM_VERSION")
    private Long version;

    @Column(name = "FC_CREATION_DATE")
    private LocalDateTime createdAt;

    @Column(name = "DE_CCAA_ID")
    private String ccaa;

    @Column(name = "IN_CCAA_CREATION")
    private boolean ccaaCreation;

    @Column(name = "FC_CODE_VALID_FROM")
    private LocalDate codeValidFrom;

    @Column(name = "FC_CODE_VALID_UNTIL")
    private LocalDate codeValidUntil;

    @Column(name = "DE_CODE_HASH")
    private String codeHash;

    @Column(name = "IN_CODE_REDEEMED")
    private boolean codeRedeemed;
    
    @Column(name = "FC_CODE_REDEEMED_DATE")
    private LocalDateTime codeRedeemedAt;

}
