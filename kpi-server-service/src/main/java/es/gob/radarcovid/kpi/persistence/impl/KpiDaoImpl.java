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

import org.springframework.stereotype.Service;

import es.gob.radarcovid.kpi.api.KpiDto;
import es.gob.radarcovid.kpi.api.KpiTypeDto;
import es.gob.radarcovid.kpi.persistence.KpiDao;
import es.gob.radarcovid.kpi.persistence.entity.KpiEntity;
import es.gob.radarcovid.kpi.persistence.mapper.KpiMapper;
import es.gob.radarcovid.kpi.persistence.mapper.KpiTypeMapper;
import es.gob.radarcovid.kpi.persistence.repository.KpiEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.KpiTypeEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiDaoImpl implements KpiDao {

    private final KpiEntityRepository kpiEntityRepository;
    private final KpiTypeEntityRepository kpiTypeEntityRepository;

    private final KpiTypeMapper kpiTypeMapper;
    private final KpiMapper kpiMapper;

    @Override
    public List<KpiTypeDto> getKpiTypes() {
        return kpiTypeMapper.asListDto(kpiTypeEntityRepository.findAll());
    }
    
	@Override
	public void save(List<KpiDto> dtoList) {
		dtoList.stream().filter(dto -> kpiTypeEntityRepository.existsKpiTypeEntityByNameAndEnabledIsTrue(dto.getKpi()))
				.forEach(this::save);
	}
	
	private void save(KpiDto dto) {
		kpiTypeEntityRepository.findByNameAndEnabledIsTrue(dto.getKpi()).map(type -> {
			KpiEntity entity = kpiMapper.asEntity(dto);
			entity.setKpiType(type);
			return entity;
		}).ifPresent(entity -> {
			kpiEntityRepository.save(entity);
			log.debug("Saved kpi ({}) {} => {}", entity.getId(), entity.getKpiType().getName(), entity.getValue());
		});
	}

}
