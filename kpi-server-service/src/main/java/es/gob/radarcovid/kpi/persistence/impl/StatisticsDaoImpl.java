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

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.gob.radarcovid.kpi.api.StatisticsDateDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsValueCcaaDto;
import es.gob.radarcovid.kpi.api.GraphicStatisticsValueDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarCcaaDto;
import es.gob.radarcovid.kpi.domain.StatisticsRadarDto;
import es.gob.radarcovid.kpi.persistence.StatisticsDao;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataCcaaMonthStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataCcaaWeekStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataTotalMonthStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataTotalWeekStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.entity.ViewDataWeekStatisticsEntity;
import es.gob.radarcovid.kpi.persistence.mapper.StatisticsDateMapper;
import es.gob.radarcovid.kpi.persistence.mapper.StatisticsRadarCcaaMapper;
import es.gob.radarcovid.kpi.persistence.mapper.StatisticsRadarMapper;
import es.gob.radarcovid.kpi.persistence.mapper.GraphicStatisticsValueCcaaMapper;
import es.gob.radarcovid.kpi.persistence.mapper.GraphicStatisticsValueMapper;
import es.gob.radarcovid.kpi.persistence.mapper.GraphicStatisticsValueSoMapper;
import es.gob.radarcovid.kpi.persistence.mapper.GraphicStatisticsValueTotalMapper;
import es.gob.radarcovid.kpi.persistence.repository.CcaaEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.StatisticsRadarCcaaEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.StatisticsRadarEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.ViewDataCcaaMonthStatisticsEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.ViewDataCcaaWeekStatisticsEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.ViewDataMonthStatisticsEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.ViewDataTotalMonthStatisticsEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.ViewDataTotalWeekStatisticsEntityRepository;
import es.gob.radarcovid.kpi.persistence.repository.ViewDataWeekStatisticsEntityRepository;
import es.gob.radarcovid.kpi.vo.GraphicStatisticsTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsDaoImpl implements StatisticsDao {
	
	private final StatisticsRadarEntityRepository statisticsRadarEntityRepository;
	private final StatisticsRadarCcaaEntityRepository statisticsRadarCcaaEntityRepository;
    private final ViewDataMonthStatisticsEntityRepository dataMonthStatisticsRepository;
    private final ViewDataWeekStatisticsEntityRepository dataWeekStatisticsRepository;
    private final ViewDataCcaaMonthStatisticsEntityRepository dataCcaaMonthStatisticsEntityRepository;
    private final ViewDataCcaaWeekStatisticsEntityRepository dataCcaaWeekStatisticsEntityRepository;
    private final ViewDataTotalMonthStatisticsEntityRepository dataTotalMonthStatisticsEntityRepository;
    private final ViewDataTotalWeekStatisticsEntityRepository dataTotalWeekStatisticsEntityRepository;
    private final CcaaEntityRepository ccaaEntityRepository;
    
    private final StatisticsRadarMapper statisticsRadarMapper;
    private final StatisticsRadarCcaaMapper statisticsRadarCcaaMapper;
    private final StatisticsDateMapper statisticsDateMapper;
    private final GraphicStatisticsValueMapper graphicStatisticsValueMapper;
    private final GraphicStatisticsValueTotalMapper graphicStatisticsValueTotalMapper;
    private final GraphicStatisticsValueSoMapper graphicStatisticsValueSoMapper;
    private final GraphicStatisticsValueCcaaMapper graphicStatisticsValueCcaaMapper;
    
    private static final int TOTAL_ROWS_MONTH = 12;
    private static final int TOTAL_ROWS_WEEK = 24;
    
	@Override
	public Optional<StatisticsRadarDto> getLastStatistics() {
		return statisticsRadarEntityRepository.findFirstByOrderByStatisticDateDesc().map(statisticsRadarMapper::asDto);
	}
	
	@Override
	public void save(StatisticsRadarDto statisticsRadarDto) {
		statisticsRadarEntityRepository.save(statisticsRadarMapper.asEntity(statisticsRadarDto));
	}
	
	@Override
	public void saveAll(List<StatisticsRadarCcaaDto> statisticsRadarCcaaDtos) {
		statisticsRadarCcaaEntityRepository.saveAll(
				statisticsRadarCcaaDtos.stream().map(statisticsRadarCcaaMapper::asEntity).collect(Collectors.toList()));
	}
	
	@Override
	public void updateConfirmedPositives(StatisticsRadarCcaaDto statisticsRadarCcaaDto) {
		statisticsRadarCcaaEntityRepository.updateConfirmedPositives(statisticsRadarCcaaDto.getConfirmedPositives(),
				statisticsRadarCcaaDto.getStatisticDate(), statisticsRadarCcaaDto.getCcaa().getId());
	}
	
    @Override
	public Optional<StatisticsDateDto> getLastStatisticsDate() {
		return dataWeekStatisticsRepository.findFirstByOrderByYearWeekDesc().map(statisticsDateMapper::asDto);
	}
	
	@Override
	public Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getDownloadsStatistics() {
		log.debug("Entering in getDownloadsStatistics()");
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> result = new LinkedHashMap<>();
		Optional<ViewDataWeekStatisticsEntity> lastStatistics = dataWeekStatisticsRepository.findFirstByOrderByYearWeekDesc();

		List<GraphicStatisticsValueDto> totalValues = new ArrayList<>();
		lastStatistics.map(graphicStatisticsValueTotalMapper::asDownloadsDto).ifPresent(totalValues::add);
				
		List<GraphicStatisticsValueDto> monthValues = dataMonthStatisticsRepository
				.findAll(PageRequest.of(0, TOTAL_ROWS_MONTH, Sort.by(Sort.Direction.DESC, "yearMonth"))).stream()
				.map(graphicStatisticsValueMapper::asDownloadsDto).collect(Collectors.toList());
		monthValues.sort(Comparator.comparing(GraphicStatisticsValueDto::getDate));
		
		List<GraphicStatisticsValueDto> weekValues = dataWeekStatisticsRepository
				.findAll(PageRequest.of(0, TOTAL_ROWS_WEEK, Sort.by(Sort.Direction.DESC, "yearWeek"))).stream()
				.map(graphicStatisticsValueMapper::asDownloadsDto).collect(Collectors.toList());
		weekValues.sort(Comparator.comparing(GraphicStatisticsValueDto::getDate));
		
		List<GraphicStatisticsValueDto> soValues = new ArrayList<>();
		lastStatistics.map(graphicStatisticsValueSoMapper::asAndroidDto).ifPresent(soValues::add);
		lastStatistics.map(graphicStatisticsValueSoMapper::asIosDto).ifPresent(soValues::add);
		
		result.put(GraphicStatisticsTypeEnum.DOWNLOADS_TOTAL, totalValues);
		result.put(GraphicStatisticsTypeEnum.DOWNLOADS_MONTH, monthValues);
		result.put(GraphicStatisticsTypeEnum.DOWNLOADS_WEEK, weekValues);
		result.put(GraphicStatisticsTypeEnum.DOWNLOADS_SO, soValues);
		
		log.debug("Leaving getDownloadsStatistics() with: {} total values, {} month values, {} week values, {} SO values",
				totalValues.size(), monthValues.size(), weekValues.size(), soValues.size());
		return result;
	}
	
	@Override
	public Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getDeliveredCodesStatistics() {
		log.debug("Entering in getDeliveredCodesStatistics()");
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> result = new LinkedHashMap<>();
		
		long totalCcaa = ccaaEntityRepository.count();
		List<ViewDataCcaaWeekStatisticsEntity> lastStatisticsByCcaa = this.lastStatisticsByCcaa(totalCcaa);

		List<GraphicStatisticsValueDto> totalValues = new ArrayList<>();
		dataWeekStatisticsRepository.findFirstByOrderByYearWeekDesc()
				.map(graphicStatisticsValueTotalMapper::asDeliveredCodesDto).ifPresent(totalValues::add);
		
		List<GraphicStatisticsValueDto> accDelivered = lastStatisticsByCcaa.stream()
				.map(graphicStatisticsValueCcaaMapper::asDeliveredCodesDto).collect(Collectors.toList());
		
		List<GraphicStatisticsValueDto> accConfirmed = lastStatisticsByCcaa.stream()
				.map(graphicStatisticsValueCcaaMapper::asConfirmedPositivesDto).collect(Collectors.toList());
		
		List<GraphicStatisticsValueDto> monthDeliveredCodes = this.monthStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asDeliveredCodesDto);
		List<GraphicStatisticsValueDto> weekDeliveredCodes = this.weekStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asDeliveredCodesDto);
		
		result.put(GraphicStatisticsTypeEnum.DELIVERED_CODES_TOTAL, totalValues);
		result.put(GraphicStatisticsTypeEnum.DELIVERED_CODES_ACCUMULATED, accDelivered);
		result.put(GraphicStatisticsTypeEnum.DELIVERED_CODES_CONFIRMED, accConfirmed);
		result.put(GraphicStatisticsTypeEnum.DELIVERED_CODES_MONTH, monthDeliveredCodes);
		result.put(GraphicStatisticsTypeEnum.DELIVERED_CODES_WEEK, weekDeliveredCodes);

		log.debug("Leaving getDeliveredCodesStatistics() with: {} total values, {} acc values, {} acc confirmed values, {} month values, "
				+ "{} week values", totalValues.size(), accDelivered.size(), accConfirmed.size(), monthDeliveredCodes.size(), weekDeliveredCodes.size());
		return result;
	}
	
	@Override
	public Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getRatioDeliveredCodesStatistics() {
		log.debug("Entering in getRatioDeliveredCodesStatistics()");
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> result = new LinkedHashMap<>();
		
		long totalCcaa = ccaaEntityRepository.count();
		List<GraphicStatisticsValueDto> ratioAccDelivered = this.lastStatisticsByCcaa(totalCcaa).stream()
				.map(graphicStatisticsValueCcaaMapper::asRatioDeliveredCodesDto).collect(Collectors.toList());
		
		List<GraphicStatisticsValueDto> ratioMonthDeliveredCodes = this.monthStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asRatioDeliveredCodesDto);
		List<GraphicStatisticsValueDto> ratioWeekDeliveredCodes = this.weekStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asRatioDeliveredCodesDto);
		List<GraphicStatisticsValueDto> ratioTotalMonthDeliveredCodes = this.monthStatistics(graphicStatisticsValueMapper::asRatioDeliveredCodesDto);
		List<GraphicStatisticsValueDto> ratioTotalWeekDeliveredCodes = this.weekStatistics(graphicStatisticsValueMapper::asRatioDeliveredCodesDto);
		
		result.put(GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES_ACCUMULATED, ratioAccDelivered);
		result.put(GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES_MONTH, ratioMonthDeliveredCodes);
		result.put(GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES_WEEK, ratioWeekDeliveredCodes);
		result.put(GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES_ACCUMULATED_NATIONAL_MONTH, ratioTotalMonthDeliveredCodes);
		result.put(GraphicStatisticsTypeEnum.RATIO_DELIVERED_CODES_ACCUMULATED_NATIONAL_WEEK, ratioTotalWeekDeliveredCodes);
		
		log.debug("Leaving getRatioDeliveredCodesStatistics() with: {} ratio acc values, {} ratio month, values, {} ratio week values, "
				+ "{} ratio acc values national month, {} ratio acc values national week", 
				ratioAccDelivered.size(), ratioMonthDeliveredCodes.size(), ratioWeekDeliveredCodes.size(), 
				ratioTotalMonthDeliveredCodes.size(), ratioTotalWeekDeliveredCodes.size());
		return result;
	}
	
	@Override
	public Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getRadarPositivesStatistics() {
		log.debug("Entering in getRadarPositivesStatistics()");
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> result = new LinkedHashMap<>();

		List<GraphicStatisticsValueDto> totalValues = new ArrayList<>();
		dataWeekStatisticsRepository.findFirstByOrderByYearWeekDesc()
				.map(graphicStatisticsValueTotalMapper::asPositivesDto).ifPresent(totalValues::add);
		
		List<GraphicStatisticsValueDto> monthValues = dataMonthStatisticsRepository
				.findAll(PageRequest.of(0, TOTAL_ROWS_MONTH, Sort.by(Sort.Direction.DESC, "yearMonth"))).stream()
				.map(graphicStatisticsValueMapper::asPositvesDto).collect(Collectors.toList());
		monthValues.sort(Comparator.comparing(GraphicStatisticsValueDto::getDate));
		
		List<GraphicStatisticsValueDto> weekValues = dataWeekStatisticsRepository
				.findAll(PageRequest.of(0, TOTAL_ROWS_WEEK, Sort.by(Sort.Direction.DESC, "yearWeek"))).stream()
				.map(graphicStatisticsValueMapper::asPositvesDto).collect(Collectors.toList());
		weekValues.sort(Comparator.comparing(GraphicStatisticsValueDto::getDate));
		
		result.put(GraphicStatisticsTypeEnum.POSITIVES_TOTAL, totalValues);
		result.put(GraphicStatisticsTypeEnum.POSITIVES_MONTH, monthValues);
		result.put(GraphicStatisticsTypeEnum.POSITIVES_WEEK, weekValues);
		
		log.debug("Leaving getRadarPositivesStatistics() with: {} total values, {} month values, {} week values",
				totalValues.size(), monthValues.size(), weekValues.size());
		return result;
	}
	
	@Override
	public Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getCcaaRadarPositivesStatistics() {
		log.debug("Entering in getCcaaRadarPositivesStatistics()");
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> result = new LinkedHashMap<>();

		List<GraphicStatisticsValueDto> totalValues = new ArrayList<>();
		dataWeekStatisticsRepository.findFirstByOrderByYearWeekDesc()
				.map(graphicStatisticsValueTotalMapper::asPositivesDto).ifPresent(totalValues::add);
		
		long totalCcaa = ccaaEntityRepository.count();
		List<GraphicStatisticsValueDto> accRadarPositives = this.lastStatisticsByCcaa(totalCcaa).stream()
				.map(graphicStatisticsValueCcaaMapper::asRadarPositivesDto).collect(Collectors.toList());
		
		List<GraphicStatisticsValueDto> monthRadarPositives = this.monthStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asRadarPositivesDto);
		List<GraphicStatisticsValueDto> weekRadarPositives = this.weekStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asRadarPositivesDto);
		
		result.put(GraphicStatisticsTypeEnum.CCAA_POSITIVES_TOTAL, totalValues);
		result.put(GraphicStatisticsTypeEnum.CCAA_POSITIVES_ACCUMULATED, accRadarPositives);
		result.put(GraphicStatisticsTypeEnum.CCAA_POSITIVES_MONTH, monthRadarPositives);
		result.put(GraphicStatisticsTypeEnum.CCAA_POSITIVES_WEEK, weekRadarPositives);

		log.debug("Leaving getCcaaRadarPositivesStatistics() with: {} total values, {} acc values, {} month values, "
				+ "{} week values", totalValues.size(), accRadarPositives.size(), monthRadarPositives.size(), weekRadarPositives.size());
		return result;
	}
	
	@Override
	public Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> getRatioCcaaRadarPositivesStatistics() {
		log.debug("Entering in getRatioCcaaRadarPositivesStatistics()");
		Map<GraphicStatisticsTypeEnum, List<GraphicStatisticsValueDto>> result = new LinkedHashMap<>();
		
		long totalCcaa = ccaaEntityRepository.count();
		List<GraphicStatisticsValueDto> ratioAccPositives = this.lastStatisticsByCcaa(totalCcaa).stream()
				.map(graphicStatisticsValueCcaaMapper::asRatioRadarPositivesDto).collect(Collectors.toList());
		
		List<GraphicStatisticsValueDto> ratioMonthPositives = this.monthStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asRatioRadarPositivesDto);
		List<GraphicStatisticsValueDto> ratioWeekPositives = this.weekStatisticsByCcaa(totalCcaa, graphicStatisticsValueMapper::asRatioRadarPositivesDto);
		List<GraphicStatisticsValueDto> ratioTotalMonthPositives = this.monthStatistics(graphicStatisticsValueMapper::asRatioRadarPositivesDto);
		List<GraphicStatisticsValueDto> ratioTotalWeekPositives = this.weekStatistics(graphicStatisticsValueMapper::asRatioRadarPositivesDto);
		
		result.put(GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES_ACCUMULATED, ratioAccPositives);
		result.put(GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES_MONTH, ratioMonthPositives);
		result.put(GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES_WEEK, ratioWeekPositives);
		result.put(GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES_ACCUMULATED_NATIONAL_MONTH, ratioTotalMonthPositives);
		result.put(GraphicStatisticsTypeEnum.RATIO_CCAA_POSITIVES_ACCUMULATED_NATIONAL_WEEK, ratioTotalWeekPositives);
		
		log.debug("Leaving getRatioCcaaRadarPositivesStatistics() with: {} ratio acc values, {} ratio month, values, {} ratio week values, "
				+ "{} ratio acc values national month, {} ratio acc values national week", 
				ratioAccPositives.size(), ratioMonthPositives.size(), ratioWeekPositives.size(), 
				ratioTotalMonthPositives.size(), ratioTotalWeekPositives.size());
		return result;
	}
	
	private List<ViewDataCcaaWeekStatisticsEntity> lastStatisticsByCcaa(long totalCcaa) {
		int lastYearWeek = dataCcaaWeekStatisticsEntityRepository.findLastYearWeekByRedeemedForAllCcaa(totalCcaa)
				.orElse(getActualYearWeek());
		return dataCcaaWeekStatisticsEntityRepository.findByYearWeekIs(lastYearWeek);
	}
	
	private List<GraphicStatisticsValueDto> monthStatisticsByCcaa(long totalCcaa, Function<ViewDataCcaaMonthStatisticsEntity, GraphicStatisticsValueDto> mapper) {
		List<GraphicStatisticsValueDto> result = new ArrayList<>();
		int lastYearMonth = dataCcaaMonthStatisticsEntityRepository.findLastYearMonthByRedeemedForAllCcaa(totalCcaa)
				.orElse(getActualYearMonth());
		int size = Long.valueOf(TOTAL_ROWS_MONTH * totalCcaa).intValue();
		dataCcaaMonthStatisticsEntityRepository
				.findByYearMonthLessThanEqual(lastYearMonth, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "yearMonth")))
				.stream()
				.collect(Collectors.groupingBy(e -> new GraphicStatisticsValueCcaaDto(e.getId().getCcaaId(), e.getCcaaName(), null)))
				.forEach((ccaa, entities) -> {
					ccaa.setValues(
							entities.stream().map(mapper).sorted(Comparator.comparing(GraphicStatisticsValueDto::getDate))
									.collect(Collectors.toList()));
					result.add(ccaa);
				});
		return result;
	}
	
	private List<GraphicStatisticsValueDto> weekStatisticsByCcaa(long totalCcaa, Function<ViewDataCcaaWeekStatisticsEntity, GraphicStatisticsValueDto> mapper) {
		List<GraphicStatisticsValueDto> result = new ArrayList<>();
		int lastYearWeek = dataCcaaWeekStatisticsEntityRepository.findLastYearWeekByRedeemedForAllCcaa(totalCcaa)
				.orElse(getActualYearWeek());
		int size = Long.valueOf(TOTAL_ROWS_WEEK * totalCcaa).intValue();
		dataCcaaWeekStatisticsEntityRepository
				.findByYearWeekLessThanEqual(lastYearWeek, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "yearWeek")))
				.stream()
				.collect(Collectors.groupingBy(e -> new GraphicStatisticsValueCcaaDto(e.getId().getCcaaId(), e.getCcaaName(), null)))
				.forEach((ccaa, entities) -> {
					ccaa.setValues(
							entities.stream().map(mapper).sorted(Comparator.comparing(GraphicStatisticsValueDto::getDate))
									.collect(Collectors.toList()));
					result.add(ccaa);
				});
		return result;
	}

	private List<GraphicStatisticsValueDto> monthStatistics(Function<ViewDataTotalMonthStatisticsEntity, GraphicStatisticsValueDto> mapper) {
		int lastYearMonth = dataTotalMonthStatisticsEntityRepository.findLastYearMonthByRedeemed()
				.orElse(getActualYearMonth());
		int size = Long.valueOf(TOTAL_ROWS_MONTH).intValue();
		return dataTotalMonthStatisticsEntityRepository
				.findByYearMonthLessThanEqual(lastYearMonth, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "yearMonth")))
				.stream()
				.map(mapper).sorted(Comparator.comparing(GraphicStatisticsValueDto::getDate))
				.collect(Collectors.toList());
	}

	private List<GraphicStatisticsValueDto> weekStatistics(Function<ViewDataTotalWeekStatisticsEntity, GraphicStatisticsValueDto> mapper) {
		int lastYearWeek = dataTotalWeekStatisticsEntityRepository.findLastYearWeekByRedeemed()
				.orElse(getActualYearWeek());
		int size = Long.valueOf(TOTAL_ROWS_WEEK).intValue();
		return dataTotalWeekStatisticsEntityRepository
				.findByYearWeekLessThanEqual(lastYearWeek, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "yearWeek")))
				.stream()
				.map(mapper).sorted(Comparator.comparing(GraphicStatisticsValueDto::getDate))
				.collect(Collectors.toList());
	}

	private int getActualYearWeek() {
		LocalDate now = LocalDate.now();
		int weekNumber = now.get(WeekFields.ISO.weekOfYear());
		int yearNumber = now.getYear();
		return Integer.valueOf(new StringBuilder().append(yearNumber).append(weekNumber).toString());
	}
	
	private int getActualYearMonth() {
		LocalDate now = LocalDate.now();
		int monthNumber = now.getMonthValue();
		int yearNumber = now.getYear();
		return Integer.valueOf(new StringBuilder().append(yearNumber).append(monthNumber).toString());
	}

}
