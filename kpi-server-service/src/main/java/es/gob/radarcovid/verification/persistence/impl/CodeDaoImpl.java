/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.gob.radarcovid.kpi.domain.CcaaDto;
import es.gob.radarcovid.verification.persistence.CodeDao;
import es.gob.radarcovid.verification.persistence.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeDaoImpl implements CodeDao {
	
	private final CodeRepository repository;
	
	@Override
	public Long countRedeemedCodes(LocalDate date, List<CcaaDto> ccaas) {
		return repository.countByCodeRedeemedAtBetween(date.atStartOfDay(),
				date.plusDays(1).atStartOfDay(), 
				ccaas.stream().map(CcaaDto::getNumId).collect(Collectors.toList()));
	}

	@Override
	public Long countRedeemedCodes(LocalDate date, CcaaDto ccaa) {
		return repository.countByCodeRedeemedAtBetweenAndCcaa(date.atStartOfDay(),
				date.plusDays(1).atStartOfDay(), ccaa.getNumId());
	}

	@Override
	public Long countTotalCodes(LocalDate date, List<CcaaDto> ccaas) {
		return repository.countByCreatedAtBetween(date.atStartOfDay(),
				date.plusDays(1).atStartOfDay(),
				ccaas.stream().map(CcaaDto::getNumId).collect(Collectors.toList()));
	}

	@Override
	public Long countTotalCodes(LocalDate date, CcaaDto ccaa) {
		return repository.countByCreatedAtBetweenAndCcaa(date.atStartOfDay(),
				date.plusDays(1).atStartOfDay(), ccaa.getNumId());
	}

}
