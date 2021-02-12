/**
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.business.test

import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT

import es.gob.radarcovid.common.security.KeyVault
import es.gob.radarcovid.kpi.api.AppleKpiTokenRequestDto
import es.gob.radarcovid.kpi.api.AppleKpiTokenResponseDto
import es.gob.radarcovid.kpi.api.KpiDto
import es.gob.radarcovid.kpi.client.impl.DeviceCheckClientServiceImpl
import es.gob.radarcovid.kpi.etc.Constants
import es.gob.radarcovid.kpi.util.HashingService
import spock.lang.Specification


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AppleKpiControllerTestSpec extends Specification {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	KeyVault keyVault

	@Autowired
	HashingService hashingService
	
	@SpringBean
	DeviceCheckClientServiceImpl deviceCheckClientService = Mock()

	def 'verification apple token accepted (#tokenDevice) = #httpStatus'(String tokenDevice, boolean validToken, HttpStatus httpStatus) {
		given:
		HttpHeaders httpHeaders = new HttpHeaders()

		httpHeaders.setContentType(MediaType.APPLICATION_JSON)
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))

		AppleKpiTokenRequestDto bodyRequest = new AppleKpiTokenRequestDto()
		bodyRequest.deviceToken = tokenDevice

		HttpEntity<?> request = new HttpEntity<>(bodyRequest, httpHeaders)
		
		and:
		deviceCheckClientService.validate(_) >> validToken

		when: 'request the verification token endpoint'
		def result = testRestTemplate.postForEntity('/apple/token', request, Void.class)
		Thread.sleep(2000)

		then:
		result.statusCode == httpStatus
		
		where:
		tokenDevice    | validToken | httpStatus
		'dG9rZW4tMDE=' | true       | HttpStatus.ACCEPTED
		'dG9rZW4tMDI=' | false      | HttpStatus.ACCEPTED
	}
	
	def 'verification apple token (#tokenDevice) = #httpStatus'(String tokenDevice, HttpStatus httpStatus) {
		given:
		HttpHeaders httpHeaders = new HttpHeaders()

		httpHeaders.setContentType(MediaType.APPLICATION_JSON)
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))

		AppleKpiTokenRequestDto bodyRequest = new AppleKpiTokenRequestDto()
		bodyRequest.deviceToken = tokenDevice

		HttpEntity<?> request = new HttpEntity<>(bodyRequest, httpHeaders)

		when: 'request the verification token endpoint'
		def result = testRestTemplate.postForEntity('/apple/token', request, AppleKpiTokenResponseDto.class)

		then:
		result.statusCode == httpStatus
		(result.statusCode == HttpStatus.CREATED && validateResponse(result.body.token, tokenDevice)) || !(result.statusCode == HttpStatus.CREATED)

		where:
		tokenDevice    | httpStatus
		'dG9rZW4tMDE=' | HttpStatus.CREATED
		'dG9rZW4tMDI=' | HttpStatus.UNAUTHORIZED
	}
		
	def 'save apple kpi (#subject) = #httpStatus'(String subject, HttpStatus httpStatus) {
		given:
		HttpHeaders httpHeaders = new HttpHeaders()

		httpHeaders.setContentType(MediaType.APPLICATION_JSON)
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))
		httpHeaders.set(Constants.AUTHORIZATION_HEADER, Constants.AUTHORIZATION_PREFIX + generateToken(subject))
		
		KpiDto kpi = new KpiDto()
		kpi.kpi = 'MATCH_CONFIRMED'
		kpi.timestamp = new Date()
		kpi.value = 1
		def bodyRequest = Collections.singletonList(kpi)

		HttpEntity<?> request = new HttpEntity<>(bodyRequest, httpHeaders)

		when: 'request the verification token endpoint'
		def result = testRestTemplate.postForEntity('/apple', request, Void.class)

		then:
		result.statusCode == httpStatus
		
		where:
		subject        | httpStatus
		'dG9rZW4tMDE=' | HttpStatus.CREATED
		'dG9rZW4tMDI=' | HttpStatus.FORBIDDEN
	}

	def validateResponse(String jwtToken, String tokenDevice) throws Exception {
		ECPublicKey publicKey = (ECPublicKey) keyVault.get(Constants.PAIR_KEY_RADAR).getPublic()
		Algorithm algorithm = Algorithm.ECDSA512(publicKey, null)
		JWTVerifier verifier = JWT.require(algorithm).build()
		DecodedJWT jwt = verifier.verify(jwtToken)
		return jwt.getSubject() == hashingService.hash(tokenDevice)
	}
	
	def generateToken(String subject) throws Exception {
		ECPrivateKey privateKey = (ECPrivateKey) keyVault.get(Constants.PAIR_KEY_RADAR).getPrivate()
		Algorithm algorithm = Algorithm.ECDSA512(null, privateKey)

		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS)
		Instant expiresAt = issuedAt.plus(15, ChronoUnit.MINUTES)

		return JWT.create()
				.withJWTId(UUID.randomUUID().toString())
				.withSubject(hashingService.hash(subject))
				.withIssuer('ISSUER')
				.withIssuedAt(Date.from(issuedAt))
				.withExpiresAt(Date.from(expiresAt))
				.sign(algorithm)
	}
}
