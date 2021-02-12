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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.common.exception.KpiServerException;
import es.gob.radarcovid.kpi.api.GoogleKpiDto;
import es.gob.radarcovid.kpi.api.KpiDto;
import es.gob.radarcovid.kpi.api.KpiTypeDto;
import es.gob.radarcovid.kpi.business.KpiService;
import es.gob.radarcovid.kpi.client.DeviceCheckClientService;
import es.gob.radarcovid.kpi.persistence.AuthenticationDao;
import es.gob.radarcovid.kpi.persistence.KpiDao;
import es.gob.radarcovid.kpi.persistence.SaltDao;
import es.gob.radarcovid.kpi.safetynet.SafetyNetValidator;
import es.gob.radarcovid.kpi.security.JwtGenerator;
import es.gob.radarcovid.kpi.util.HashingService;
import es.gob.radarcovid.kpi.vo.SoTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiServiceImpl implements KpiService {

	private final KpiDao kpiDao;
	private final SaltDao saltDao;
	private final AuthenticationDao authenticationDao;
	private final SafetyNetValidator safetyNetValidator;
	private final JwtGenerator jwtGenerator;
	private final HashingService hashingService;
	private final DeviceCheckClientService deviceCheckClientService;

	@Loggable
	@Override
	public List<KpiTypeDto> getKpiTypes() {
		return kpiDao.getKpiTypes();
	}

	@Loggable
	@Override
	public void save(List<KpiDto> dtoList) {
		kpiDao.save(dtoList);
	}

	@Loggable
	@Override
	public void saveGoogle(GoogleKpiDto googleKpiDto) {
		if (saltDao.find(googleKpiDto.getSalt()).isPresent()) {
			log.warn("Salt ({}) has already been used", googleKpiDto.getSalt());
			return;
		}
		String nonce = SafetyNetValidator.generateNonce(googleKpiDto.getKpis(), googleKpiDto.getSalt());
		if (!safetyNetValidator.verify(googleKpiDto.getSignedAttestation(), nonce)) {
			log.warn("SafetyNet invalid for nonce ({})", nonce);
			return;
		}
		save(googleKpiDto.getKpis(), SoTypeEnum.ANDROID);
		saltDao.save(googleKpiDto.getSalt());
	}

	@Loggable
	@Override
	public void saveApple(String salt, List<KpiDto> kpiDtos) {
		if (saltDao.find(salt).isPresent()) {
			log.warn("Salt ({}) has already been used", salt);
			return;
		}	
		save(kpiDtos, SoTypeEnum.IOS);
		saltDao.save(salt);
	}
	
	private void save(List<KpiDto> kpiDtos, SoTypeEnum soType) {
		List<KpiDto> dtoList = kpiDtos.stream()
				.filter(kpi -> kpi.getValue() > 0)
				.map(kpi -> {
					kpi.setSoType(soType);
					return kpi;
				}).collect(Collectors.toList());
		kpiDao.save(dtoList);
	}
	
	@Loggable
	@Override
	public Optional<String> verifyToken(String deviceToken) {
		String hash = hashingService.hash(deviceToken);
		Optional<Boolean> authenticate = authenticationDao.find(hash);
		if (authenticate.isPresent()) {
			if (authenticate.get()) {
				return Optional.of(jwtGenerator.generateRadarJwt(deviceToken));
			}
			authenticationDao.delete(hash);
			throw new KpiServerException(HttpStatus.UNAUTHORIZED, "Invalid token device");
		}
		return Optional.empty();
	}
	
	@Loggable
	@Override
	@Async
	public void authenticateToken(String deviceToken) {
		String hash = hashingService.hash(deviceToken);
		boolean isValid = deviceCheckClientService.validate(deviceToken);
		authenticationDao.save(hash, isValid);
		log.debug("Device token {} is valid: {}", hash, isValid);
	}

}
