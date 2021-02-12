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

import es.gob.radarcovid.kpi.domain.StatisticsRadarCcaaDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarDto;
import es.gob.radarcovid.kpi.persistence.ExportDao;
import es.gob.radarcovid.kpi.persistence.mapper.StatisticsRadarCcaaMapper;
import es.gob.radarcovid.kpi.persistence.mapper.StatisticsRadarMapper;
import es.gob.radarcovid.kpi.persistence.repository.StatisticsRadarCcaaEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.StatisticsRadarEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportDaoImpl implements ExportDao {
    private final StatisticsRadarEntityRepository statisticsRadarEntityRepository;
    private final StatisticsRadarCcaaEntityRepository statisticsRadarCcaaEntityRepository;
    private final StatisticsRadarMapper mapperRadar;
    private final StatisticsRadarCcaaMapper mapperRadarCcaa;

    @Override
    public List<StatisticsRadarDto> getStatistics() {
        return statisticsRadarEntityRepository.findDownloads().get().stream()
                .map(mapperRadar::asDto).collect(Collectors.toList());
    }

    @Override
    public List<StatisticsRadarCcaaDto> getStatisticsCcaa() {
        return statisticsRadarCcaaEntityRepository.findCccaaStatistics().get().stream()
                .map(mapperRadarCcaa::asDto)
                .map(dto -> {
                    dto.getCcaa().setId(processCAName(dto.getCcaa().getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String processCAName(String ca) {
        if(ca != null && !ca.trim().isEmpty() && ca.contains("-")) {
            return ca.split("-")[1];
        }
        return ca;
    }
}
