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

import es.gob.radarcovid.kpi.persistence.entity.ViewDataTotalWeekStatisticsEntity;

@Repository
public interface ViewDataTotalWeekStatisticsEntityRepository extends JpaRepository<ViewDataTotalWeekStatisticsEntity, Long> {
	
	List<ViewDataTotalWeekStatisticsEntity> findByYearWeekLessThanEqual(Integer yearWeek, Pageable page);
	
	List<ViewDataTotalWeekStatisticsEntity> findByYearWeekIs(Integer yearWeek);
	
	@Query("select v.yearWeek from ViewDataTotalWeekStatisticsEntity v group by v.yearWeek")
	List<Integer> findYearWeekByRedeemed(Pageable page);
	
	default Optional<Integer> findLastYearWeekByRedeemed() {
		return findYearWeekByRedeemed(
				PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "yearWeek"))).stream().findFirst();
	}
	
}
