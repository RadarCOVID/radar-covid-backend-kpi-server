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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.gob.radarcovid.kpi.persistence.entity.ViewDataCcaaWeekStatisticsEntity;

@Repository
public interface ViewDataCcaaWeekStatisticsEntityRepository extends JpaRepository<ViewDataCcaaWeekStatisticsEntity, Long> {
	
	List<ViewDataCcaaWeekStatisticsEntity> findByYearWeekLessThanEqual(Integer yearWeek, Pageable page);
	
	List<ViewDataCcaaWeekStatisticsEntity> findByYearWeekIs(Integer yearWeek);
	
	@Query("select v.yearWeek from ViewDataCcaaWeekStatisticsEntity v group by v.yearWeek having count(v) = :totalCcaa")
	List<Integer> findYearWeekByRedeemedForAllCcaa(Long totalCcaa, Pageable page);
	
	default Optional<Integer> findLastYearWeekByRedeemedForAllCcaa(Long totalCcaa) {
		return findYearWeekByRedeemedForAllCcaa(totalCcaa,
				PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "yearWeek"))).stream().findFirst();
	}
	
}

