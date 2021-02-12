/**
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import es.gob.radarcovid.kpi.persistence.repository.KpiTypeEntityRepository
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class KpiTypeEntityRepositoryTestSpec extends Specification {

    @Autowired
    KpiTypeEntityRepository kpiTypeEntityRepository

    def "findByNameAndEnabled"(String kpiName, boolean active) {
        expect:
        kpiTypeEntityRepository.findByNameAndEnabledIsTrue(kpiName).isPresent() == active

        where:
        kpiName                | active
        "SUMMATION_RISK_SCORE" | false
        "BLUETOOTH_ACTIVATED"  | true
        "MATCH_CONFIRMED"      | true
    }

}
