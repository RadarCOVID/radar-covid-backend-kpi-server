/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.client.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.gob.radarcovid.kpi.client.DeviceCheckClientService;
import es.gob.radarcovid.kpi.client.model.DeviceCheckQueryRequestDto;
import es.gob.radarcovid.kpi.client.model.DeviceCheckResponseDto;
import es.gob.radarcovid.kpi.client.model.DeviceCheckUpdateRequestDto;
import es.gob.radarcovid.kpi.etc.KpiProperties;
import es.gob.radarcovid.kpi.security.JwtGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DeviceCheckRestRetryableClientServiceImpl implements DeviceCheckClientService {
	
	private final static String BITS_NOT_FOUND = "Failed to find bit state";
		
    private final RestTemplate restTemplate;
    private final KpiProperties kpiProperties;
    private final JwtGenerator jwtGenerator;
    private final ObjectMapper objectMapper;

    @Override
	@Retryable(maxAttemptsExpression = "#{${application.kpi.device-check.query.retry.max-attempts:1}}", 
		backoff = @Backoff(delayExpression = "#{${application.kpi.device-check.query.retry.delay:100}}"))
    public Optional<DeviceCheckResponseDto> queryTwoBits(String token) {
    	
        log.debug("Entering DeviceCheckRestClientServiceImpl.queryTwoBits('{}')", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtGenerator.generateDeviceTokenJwt());
        
        DeviceCheckQueryRequestDto request = DeviceCheckQueryRequestDto.builder()
        	.deviceToken(token)
        	.transactionId(UUID.randomUUID().toString())
        	.timestamp(new Date().getTime())
        	.build();

        HttpEntity<DeviceCheckQueryRequestDto> httpEntity = new HttpEntity<DeviceCheckQueryRequestDto>(request, headers);        
        Optional<DeviceCheckResponseDto> result = Optional.empty();
        try {
			ResponseEntity<String> response = restTemplate
					.postForEntity(kpiProperties.getDeviceCheck().getEndpoints().getQuery(), httpEntity, String.class);
			if (response != null && response.getStatusCode().is2xxSuccessful() && !BITS_NOT_FOUND.equals(response.getBody())) {
				result = Optional.of(objectMapper.readValue(response.getBody(), DeviceCheckResponseDto.class));
			}
        } catch (HttpClientErrorException ex) {
        	if (ex.getStatusCode() != HttpStatus.BAD_REQUEST) {
	        	log.warn("Exception invoking device check query for payload : {}", request.toString(), ex);
	            throw ex;
        	}
        } catch (IOException ex) {
        	log.warn("Exception invoking device check query for payload : {}", request.toString(), ex);
			throw new RestClientException("Error while extracting response for type [" +
					DeviceCheckResponseDto.class + "] and content type [" + MediaType.APPLICATION_JSON + "]", ex);
		}
        log.debug("Leaving DeviceCheckRestClientServiceImpl.queryTwoBits with: {}", result);
        return result;
    }
    
	@Override
	@Retryable(maxAttemptsExpression = "#{${application.kpi.device-check.update.retry.max-attempts:1}}", 
		backoff = @Backoff(delayExpression = "#{${application.kpi.device-check.update.retry.delay:100}}"))
	public boolean updateTwoBits(String token, boolean bit0, boolean bit1) {
        log.debug("Entering DeviceCheckRestClientServiceImpl.updateTwoBits('{}', '{}', '{}')", token, bit0, bit1);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtGenerator.generateDeviceTokenJwt());
        
        DeviceCheckUpdateRequestDto request = DeviceCheckUpdateRequestDto.builder()
        	.deviceToken(token)
        	.transactionId(UUID.randomUUID().toString())
        	.timestamp(new Date().getTime())
        	.bit0(bit0)
        	.bit1(bit1)
        	.build();

        HttpEntity<DeviceCheckUpdateRequestDto> httpEntity = new HttpEntity<DeviceCheckUpdateRequestDto>(request, headers);
        boolean result = false;
        try {
			ResponseEntity<Void> response = restTemplate.postForEntity(
					kpiProperties.getDeviceCheck().getEndpoints().getUpdate(), httpEntity, Void.class);
            result = (response != null && response.getStatusCode().is2xxSuccessful());
        } catch (HttpClientErrorException ex) {
        	if (ex.getStatusCode() != HttpStatus.BAD_REQUEST) {
	        	log.warn("Exception invoking device check upload for payload : {}", request.toString(), ex);
	        	throw ex;
        	}
        }
        log.debug("Leaving DeviceCheckRestClientServiceImpl.updateTwoBits with: {}", result);
        return result;
	}

	@Override
	@Retryable(maxAttemptsExpression = "#{${application.kpi.device-check.validate.retry.max-attempts:1}}", 
		backoff = @Backoff(delayExpression = "#{${application.kpi.device-check.validate.retry.delay:100}}"))
	public boolean validate(String token) {
        log.debug("Entering DeviceCheckRestClientServiceImpl.validate('{}')", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtGenerator.generateDeviceTokenJwt());
        
        DeviceCheckQueryRequestDto request = DeviceCheckQueryRequestDto.builder()
        	.deviceToken(token)
        	.transactionId(UUID.randomUUID().toString())
        	.timestamp(new Date().getTime())
        	.build();

        HttpEntity<DeviceCheckQueryRequestDto> httpEntity = new HttpEntity<DeviceCheckQueryRequestDto>(request, headers);
        boolean result = false;
        log.debug("Validation URL: {}", kpiProperties.getDeviceCheck().getEndpoints().getValidate());
        try {
			ResponseEntity<Void> response = restTemplate.postForEntity(
					kpiProperties.getDeviceCheck().getEndpoints().getValidate(), httpEntity, Void.class);
            result = (response != null && response.getStatusCode().is2xxSuccessful());
        } catch (HttpClientErrorException ex) {
        	if (ex.getStatusCode() != HttpStatus.BAD_REQUEST) {
	        	log.warn("Exception invoking device check validate for payload : {}", request.toString(), ex);
	        	throw ex;
        	}
        }
        log.debug("Leaving DeviceCheckRestClientServiceImpl.validate with: {}", result);
        return result;
	}
    
}
