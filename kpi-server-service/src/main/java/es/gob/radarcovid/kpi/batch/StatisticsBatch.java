/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.batch;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.gob.radarcovid.kpi.business.StatisticsService;
import es.gob.radarcovid.kpi.etc.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@ConditionalOnProperty(name = "application.kpi.statistics.batching.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsBatch {
	
    private final StatisticsService statisticsService;

    @Scheduled(cron = "${application.kpi.statistics.batching.cron}")
    @SchedulerLock(name = "statistics", lockAtLeastFor = "PT30S",
            lockAtMostFor = "${application.kpi.statistics.batching.lock-limit}")
    public void kpiStatisticsScheduledTask() {
        MDC.put(Constants.TRACKING, "UPDATE_STATISTICS");
        log.info("Update statistics process started");
        statisticsService.updateStatistics();
        log.info("Update statistics process finished");
        MDC.remove(Constants.TRACKING);
    }

}