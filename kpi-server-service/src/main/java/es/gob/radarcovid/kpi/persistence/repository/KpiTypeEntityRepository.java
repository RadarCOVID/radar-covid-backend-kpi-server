/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.gob.radarcovid.kpi.etc.Constants;
import es.gob.radarcovid.kpi.persistence.entity.KpiTypeEntity;

@Repository
public interface KpiTypeEntityRepository extends JpaRepository<KpiTypeEntity, Integer> {

    @Cacheable(cacheNames = Constants.CACHE_FIND_KPI_TYPE_BY_NAME_AND_ENABLED, unless = "#result == null")
    Optional<KpiTypeEntity> findByNameAndEnabledIsTrue(String name);
    
    @Cacheable(cacheNames = Constants.CACHE_KPI_TYPE_ENABLED)
    boolean existsKpiTypeEntityByNameAndEnabledIsTrue(String name);
    
}
