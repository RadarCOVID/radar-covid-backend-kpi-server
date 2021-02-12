/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.gob.radarcovid.kpi.domain.CcaaDto;
import es.gob.radarcovid.kpi.persistence.CcaaDao;
import es.gob.radarcovid.kpi.persistence.mapper.CcaaMapper;
import es.gob.radarcovid.kpi.persistence.repository.CcaaEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CcaaDaoImpl implements CcaaDao {
	
	private final CcaaEntityRepository repository;
    private final CcaaMapper mapper;
    
	@Override
	public List<CcaaDto> getAllCcaa() {
		return repository.findAll().stream().map(mapper::asDto).collect(Collectors.toList());
	}
    
   
}
