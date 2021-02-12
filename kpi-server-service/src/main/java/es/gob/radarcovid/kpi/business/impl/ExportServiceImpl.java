/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.business.impl;

import es.gob.radarcovid.kpi.business.ExportService;
import es.gob.radarcovid.kpi.domain.StatisticsRadarCcaaDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarDto;
import es.gob.radarcovid.kpi.etc.Constants;
import es.gob.radarcovid.kpi.persistence.ExportDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final ExportDao dao;

    @Override
    public InputStream exportDownloads() {
        try (PrintWriter writer = new PrintWriter(Constants.DEVICES_CSV, StandardCharsets.UTF_8)) {
            writer.println(Constants.DOWNLOADS_FILE_HEADER);
            List<StatisticsRadarDto> statisticsRadarDtoList = dao.getStatistics();
            LocalDate lastSunday = lastSunday(statisticsRadarDtoList.get(statisticsRadarDtoList.size()-1).getStatisticDate());
            dao.getStatistics().stream()
                    .filter(statistics -> statistics.getStatisticDate().isBefore(lastSunday) || statistics.getStatisticDate().isEqual(lastSunday))
                    .map(statistics -> statistics.getStatisticDate() + Constants.CSV_SEPARATOR + Constants.ANDROID_NAME + Constants.CSV_SEPARATOR +
                                       statistics.getAndroidDownloads() + "\r\n" + statistics.getStatisticDate() +
                                       Constants.CSV_SEPARATOR + Constants.IOS_NAME + Constants.CSV_SEPARATOR + statistics.getIosDownloads())
                    .forEach(writer::println);
            return new FileInputStream(Constants.DEVICES_CSV);
        } catch (IOException e) {
            log.error("Error creating CSV file with downloads data.", e);
        }
        return null;
    }

    @Override
    public InputStream exportCodes() {
        try (PrintWriter writer = new PrintWriter(Constants.CCAA_CSV, StandardCharsets.UTF_8)) {
            writer.println(Constants.DOWNLOADS_CCAA_FILE_HEADER);
            List<StatisticsRadarCcaaDto> statisticsRadarCcaaDtoList = dao.getStatisticsCcaa();
            LocalDate lastSunday = lastSunday(statisticsRadarCcaaDtoList.get(statisticsRadarCcaaDtoList.size()-1).getStatisticDate());
            statisticsRadarCcaaDtoList.stream()
                    .filter(statistics -> statistics.getStatisticDate().isBefore(lastSunday) || statistics.getStatisticDate().isEqual(lastSunday))
                    .map(statistics -> statistics.getStatisticDate() + Constants.CSV_SEPARATOR + statistics.getCcaa().getId() +
                                       Constants.CSV_SEPARATOR + statistics.getConfirmedPositives() + Constants.CSV_SEPARATOR +
                                       statistics.getDeliveredCodes() + Constants.CSV_SEPARATOR + statistics.getRadarPositives())
                    .forEach(writer::println);
            return new FileInputStream(Constants.CCAA_CSV);
        } catch (IOException e) {
            log.error("Error creating CSV file with downloads data by CCAA. ", e);
        }
        return null;
    }

    private LocalDate lastSunday(LocalDate maxDate) {
        if(maxDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return maxDate;
        }
        return maxDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    }
}
