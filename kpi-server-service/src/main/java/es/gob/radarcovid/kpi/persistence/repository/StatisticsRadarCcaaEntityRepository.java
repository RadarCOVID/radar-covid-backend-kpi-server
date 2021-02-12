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

import es.gob.radarcovid.kpi.persistence.entity.StatisticsRadarCcaaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsRadarCcaaEntityRepository extends JpaRepository<StatisticsRadarCcaaEntity, Long> {

	@Modifying
	@Query("update StatisticsRadarCcaaEntity s set s.confirmedPositives = :confirmedPositives "
			+ "where s.statisticDate = :statisticDate and ccaa.id = :ccaaId")
	int updateConfirmedPositives(@Param("confirmedPositives") Long confirmedPositives,
			@Param("statisticDate") LocalDate statisticDate, @Param("ccaaId") String ccaaId);

	@Query("SELECT s FROM StatisticsRadarCcaaEntity s " +
		   "WHERE (s.statisticDate >= s.ccaa.startDate) " +
		   "AND (s.confirmedPositives IS NOT NULL AND s.deliveredCodes IS NOT NULL AND " +
		   "s.radarPositives IS NOT NULL) ORDER BY s.statisticDate, s.ccaa.id")
	Optional<List<StatisticsRadarCcaaEntity>> findCccaaStatistics();
}
