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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.kpi.api.GraphicStatisticsAttributeDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsDataDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsValueDto;
import es.gob.radarcovid.kpi.api.StatisticsDateDto;
import es.gob.radarcovid.kpi.business.StatisticsService;
import es.gob.radarcovid.kpi.domain.CcaaDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarCcaaDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarDto;
import es.gob.radarcovid.kpi.etc.KpiProperties;
import es.gob.radarcovid.kpi.etc.KpiProperties.StatisticData;
import es.gob.radarcovid.kpi.persistence.CcaaDao;
import es.gob.radarcovid.kpi.persistence.StatisticsDao;
import es.gob.radarcovid.kpi.vo.GraphicStatisticsTypeEnum;
import es.gob.radarcovid.verification.persistence.CodeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
	
	private static final String ES_PREFIX = "ES-";
	private static final String CCAA_GROUP = "ccaa";
	private static final String DATE_GROUP = "date";
	private static final String POSITIVES_GROUP = "positives";
	private static final Pattern PATTERN = Pattern.compile("^(?<" + CCAA_GROUP + ">[A-Z]{2}),(?<" + DATE_GROUP
			+ ">(\\d{4})-(\\d{2})-(\\d{2})),(?<" + POSITIVES_GROUP + ">\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)$");
	
	private final KpiProperties kpiProperties;
	private final StatisticsDao dao;
	private final CcaaDao ccaaDao;
	private final CodeDao codeDao;
	
	@Loggable
	@Override
	public void updateStatistics() {
		log.debug("Executing updateStatistics");
		List<CcaaDto> ccaas = ccaaDao.getAllCcaa();
		this.insertRadarStatistics(ccaas);
		this.updateCSVStatistics(ccaas);
		log.debug("Leaving updateStatistics");
	}
	
	private void insertRadarStatistics(List<CcaaDto> ccaas) {
		LocalDate now = LocalDate.now();
		LocalDate statisticsDate = dao.getLastStatistics().map(s -> s.getStatisticDate().plusDays(1))
				.orElse(ccaas.stream().map(CcaaDto::getStartDate).min(Comparator.comparing(LocalDate::toEpochDay))
						.orElse(now.with(DayOfWeek.MONDAY)));
		
		while (statisticsDate.isBefore(now)) {	
			dao.save(this.createStatisticsRadarDto(ccaas, statisticsDate));
			dao.saveAll(this.createStatisticsRadarCcaaDtos(ccaas, statisticsDate));
			log.debug("Inserted radar statistics for date {}", statisticsDate);
			statisticsDate = statisticsDate.plusDays(1);
		}
	}
	
	private void updateCSVStatistics(List<CcaaDto> ccaas) {
		try {
			BufferedReader read = new BufferedReader(
					new InputStreamReader(new URL(kpiProperties.getStatistics().getCcaaDiagnosisCsv()).openStream()));
			int count = 0;
			String line;
			while ((line = read.readLine()) != null) {
				Matcher m;
				if ((m = PATTERN.matcher(line)).find()) {
					this.createStatisticsRadarCcaaDtoFromCSVLine(ccaas, m).ifPresent(dao::updateConfirmedPositives);
					count++;
				}
			}
			log.debug("Updated {} rows from CSV", count);
			read.close();
		} catch (IOException e) {
			log.error("Exception reading CSV: {}", e.getMessage(), e);
		}
	}
	
	private StatisticsRadarDto createStatisticsRadarDto(List<CcaaDto> ccaas, LocalDate statisticsDate) {
		return StatisticsRadarDto.builder()
				.statisticDate(statisticsDate)
				.deliveredCodes(codeDao.countTotalCodes(statisticsDate, ccaas))
				.radarPositives(codeDao.countRedeemedCodes(statisticsDate, ccaas))
				.build();
	}
	
	private List<StatisticsRadarCcaaDto> createStatisticsRadarCcaaDtos(List<CcaaDto> ccaas, LocalDate statisticsDate) {
		List<StatisticsRadarCcaaDto> statisticsRadarCcaaDtos = new ArrayList<>();
		ccaas.stream().map(ccaa -> StatisticsRadarCcaaDto.builder()
				.statisticDate(statisticsDate)
				.ccaa(ccaa)
				.deliveredCodes(codeDao.countTotalCodes(statisticsDate, ccaa))
				.radarPositives(codeDao.countRedeemedCodes(statisticsDate, ccaa))
				.build()).forEach(statisticsRadarCcaaDtos::add);
		return statisticsRadarCcaaDtos;
	}
	
	private Optional<StatisticsRadarCcaaDto> createStatisticsRadarCcaaDtoFromCSVLine(List<CcaaDto> ccaas, Matcher matcher) {
		return ccaas.stream().filter(c -> c.getId().equals(ES_PREFIX + matcher.group(CCAA_GROUP)))
				.findFirst()
				.map(ccaa -> StatisticsRadarCcaaDto.builder()
						.statisticDate(LocalDate.parse(matcher.group(DATE_GROUP), DateTimeFormatter.ISO_DATE))
						.ccaa(ccaa)
						.confirmedPositives(Long.valueOf(matcher.group(POSITIVES_GROUP)))
						.build());
	}

	@Loggable
	@Override
	public Optional<StatisticsDateDto> getLastStatistics() {
		return dao.getLastStatisticsDate();
	}
	
	@Loggable
	@Override
	public GraphicStatisticsDto getDownloadsStatistics() {
		AtomicInteger graphId = new AtomicInteger(1);
		StatisticData downloadsData = kpiProperties.getStatistics().getData().get(GraphicStatisticsTypeEnum.DOWNLOADS);
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> downloadsStatistics = dao.getDownloadsStatistics();
				
		GraphicStatisticsDataDto data = this.createStatisticsRadarDataDto(
				downloadsData.getTitle(),
				downloadsData.getDescription(),
				dao.getLastStatisticsDate().map(StatisticsDateDto::getDate).orElse(null),
				GraphicStatisticsTypeEnum.DOWNLOADS,
				GraphicStatisticsTypeEnum.DOWNLOADS.getValue());
		
		List<GraphicStatisticsDataDto> included = new ArrayList<>();
		downloadsStatistics.forEach((type, statisticsValues) -> {
			GraphicStatisticsDataDto statisticsRadarData = this.createStatisticsRadarDataDto(
					downloadsData.getIncluded().get(type),
					statisticsValues, 
					type, 
					graphId.getAndIncrement());
			included.add(statisticsRadarData);
		});
		
		return GraphicStatisticsDto.builder().data(data).included(included).build();
	}

	@Loggable
	@Override
	public GraphicStatisticsDto getDeliveredCodesStatistics() {
		AtomicInteger graphId = new AtomicInteger(1);
		StatisticData deliveredCodesData = kpiProperties.getStatistics().getData().get(GraphicStatisticsTypeEnum.DELIVERED_CODES);
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> deliveredCodesStatistics = dao.getDeliveredCodesStatistics();
		
		GraphicStatisticsDataDto data = this.createStatisticsRadarDataDto(
				deliveredCodesData.getTitle(),
				deliveredCodesData.getDescription(),
				dao.getLastStatisticsDate().map(StatisticsDateDto::getDate).orElse(null),
				GraphicStatisticsTypeEnum.DELIVERED_CODES,
				GraphicStatisticsTypeEnum.DELIVERED_CODES.getValue());
		
		List<GraphicStatisticsDataDto> included = new ArrayList<>();
		deliveredCodesStatistics.forEach((type, statisticsValues) -> {
			GraphicStatisticsDataDto statisticsRadarData = this.createStatisticsRadarDataDto(
					deliveredCodesData.getIncluded().get(type),
					statisticsValues, 
					type, 
					graphId.getAndIncrement());
			included.add(statisticsRadarData);
		});
		
		return GraphicStatisticsDto.builder().data(data).included(included).build();
	}

	@Loggable
	@Override
	public GraphicStatisticsDto getRatioCodeStatistics() {
		AtomicInteger graphId = new AtomicInteger(1);
		StatisticData ratioDeliveredCodesData = kpiProperties.getStatistics().getData().get(GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES);
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> ratioDeliveredCodesStatistics = dao.getRatioDeliveredCodesStatistics();
		
		GraphicStatisticsDataDto data = this.createStatisticsRadarDataDto(
				ratioDeliveredCodesData.getTitle(),
				ratioDeliveredCodesData.getDescription(),
				dao.getLastStatisticsDate().map(StatisticsDateDto::getDate).orElse(null),
				GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES,
				GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES.getValue());
		
		List<GraphicStatisticsDataDto> included = new ArrayList<>();
		ratioDeliveredCodesStatistics.forEach((type, statisticsValues) -> {
			GraphicStatisticsDataDto statisticsRadarData = this.createStatisticsRadarDataDto(
					ratioDeliveredCodesData.getIncluded().get(type),
					statisticsValues, 
					type, 
					graphId.getAndIncrement());
			included.add(statisticsRadarData);
		});
		
		return GraphicStatisticsDto.builder().data(data).included(included).build();
	}

	@Loggable
	@Override
	public GraphicStatisticsDto getRadarPositivesStatistics() {
		AtomicInteger graphId = new AtomicInteger(1);
		StatisticData positivesData = kpiProperties.getStatistics().getData().get(GraphicStatisticsTypeEnum.POSITIVES);
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> positivesStatistics = dao.getRadarPositivesStatistics();
		
		GraphicStatisticsDataDto data = this.createStatisticsRadarDataDto(
				positivesData.getTitle(),
				positivesData.getDescription(),
				dao.getLastStatisticsDate().map(StatisticsDateDto::getDate).orElse(null),
				GraphicStatisticsTypeEnum.POSITIVES,
				GraphicStatisticsTypeEnum.POSITIVES.getValue());
		
		List<GraphicStatisticsDataDto> included = new ArrayList<>();
		positivesStatistics.forEach((type, statisticsValues) -> {
			GraphicStatisticsDataDto statisticsRadarData = this.createStatisticsRadarDataDto(
					positivesData.getIncluded().get(type),
					statisticsValues, 
					type, 
					graphId.getAndIncrement());
			included.add(statisticsRadarData);
		});
		
		return GraphicStatisticsDto.builder().data(data).included(included).build();
	}

	@Loggable
	@Override
	public GraphicStatisticsDto getCcaaRadarPositivesStatistics() {
		AtomicInteger graphId = new AtomicInteger(1);
		StatisticData ccaaPositivesData = kpiProperties.getStatistics().getData().get(GraphicStatisticsTypeEnum.CCAA_POSITIVES);
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> ccaaPositivesStatistics = dao.getCcaaRadarPositivesStatistics();
		
		GraphicStatisticsDataDto data = this.createStatisticsRadarDataDto(
				ccaaPositivesData.getTitle(),
				ccaaPositivesData.getDescription(),
				dao.getLastStatisticsDate().map(StatisticsDateDto::getDate).orElse(null),
				GraphicStatisticsTypeEnum.CCAA_POSITIVES,
				GraphicStatisticsTypeEnum.CCAA_POSITIVES.getValue());
		
		List<GraphicStatisticsDataDto> included = new ArrayList<>();
		ccaaPositivesStatistics.forEach((type, statisticsValues) -> {
			GraphicStatisticsDataDto statisticsRadarData = this.createStatisticsRadarDataDto(
					ccaaPositivesData.getIncluded().get(type),
					statisticsValues, 
					type, 
					graphId.getAndIncrement());
			included.add(statisticsRadarData);
		});
		
		return GraphicStatisticsDto.builder().data(data).included(included).build();
	}

	@Loggable
	@Override
	public GraphicStatisticsDto getRatioCcaaRadarPositivesStatistics() {
		AtomicInteger graphId = new AtomicInteger(1);
		StatisticData ratioCcaaPositivesData = kpiProperties.getStatistics().getData().get(GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES);
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> ratioCcaaPositivesStatistics = dao.getRatioCcaaRadarPositivesStatistics();
		
		GraphicStatisticsDataDto data = this.createStatisticsRadarDataDto(
				ratioCcaaPositivesData.getTitle(),
				ratioCcaaPositivesData.getDescription(),
				dao.getLastStatisticsDate().map(StatisticsDateDto::getDate).orElse(null),
				GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES,
				GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES.getValue());
		
		List<GraphicStatisticsDataDto> included = new ArrayList<>();
		ratioCcaaPositivesStatistics.forEach((type, statisticsValues) -> {
			GraphicStatisticsDataDto statisticsRadarData = this.createStatisticsRadarDataDto(
					ratioCcaaPositivesData.getIncluded().get(type),
					statisticsValues, 
					type, 
					graphId.getAndIncrement());
			included.add(statisticsRadarData);
		});
		
		return GraphicStatisticsDto.builder().data(data).included(included).build();
	}
	
	private GraphicStatisticsDataDto createStatisticsRadarDataDto(String title, String description, Date lastUpdate,
			GraphicStatisticsTypeEnum type, String id) {
		GraphicStatisticsAttributeDto attributes = GraphicStatisticsAttributeDto.builder()
				.title(title)
				.lastUpdate(lastUpdate)
				.description(description)
				.build();
		return GraphicStatisticsDataDto.builder()
				.type(type)
				.id(id)
				.attributes(attributes)
				.build();
	}
	
	private GraphicStatisticsDataDto createStatisticsRadarDataDto(String title, List<GraphicStatisticsValueDto> values,
			GraphicStatisticsTypeEnum type, int id) {
		GraphicStatisticsAttributeDto attributes = GraphicStatisticsAttributeDto.builder()
				.title(title)
				.type("")
				.values(values)
				.build();
		return GraphicStatisticsDataDto.builder()
				.type(type)
				.id("graph" + id)
				.attributes(attributes)
				.build();
	}

}
