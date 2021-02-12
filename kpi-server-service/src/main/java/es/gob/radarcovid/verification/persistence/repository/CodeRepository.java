/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.gob.radarcovid.verification.persistence.entity.CodeEntity;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Long> {

	@Query("select count(c) from CodeEntity c where c.codeRedeemedAt >= :from and c.codeRedeemedAt < :to"
			+ " and c.codeRedeemed is true and c.ccaa in (:ccaas)")
	Long countByCodeRedeemedAtBetween(LocalDateTime from, LocalDateTime to, List<String> ccaas);
	
	@Query("select count(c) from CodeEntity c where c.codeRedeemedAt >= :from and c.codeRedeemedAt < :to"
			+ " and c.codeRedeemed is true and c.ccaa = :ccaa")
	Long countByCodeRedeemedAtBetweenAndCcaa(LocalDateTime from, LocalDateTime to, String ccaa);
	
	@Query("select count(c) from CodeEntity c where c.createdAt >= :from and c.createdAt < :to"
			+ " and c.ccaa in (:ccaas)")
	Long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to, List<String> ccaas);
	
	@Query("select count(c) from CodeEntity c where c.createdAt >= :from and c.createdAt < :to"
			+ " and c.ccaa = :ccaa")
	Long countByCreatedAtBetweenAndCcaa(LocalDateTime from, LocalDateTime to, String ccaa);

}
