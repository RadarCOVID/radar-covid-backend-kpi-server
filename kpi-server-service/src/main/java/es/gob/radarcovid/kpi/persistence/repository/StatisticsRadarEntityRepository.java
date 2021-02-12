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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.gob.radarcovid.kpi.persistence.entity.StatisticsRadarEntity;

@Repository
public interface StatisticsRadarEntityRepository extends JpaRepository<StatisticsRadarEntity, Long> {
	
	Optional<StatisticsRadarEntity> findFirstByOrderByStatisticDateDesc();

	@Query("SELECT s FROM StatisticsRadarEntity s WHERE s.androidDownloads IS NOT NULL AND " +
		   "s.iosDownloads IS NOT NULL AND s.deliveredCodes IS NOT NULL AND s.radarPositives " +
		   "IS NOT NULL ORDER BY s.statisticDate")
	Optional<List<StatisticsRadarEntity>> findDownloads();
	
}
