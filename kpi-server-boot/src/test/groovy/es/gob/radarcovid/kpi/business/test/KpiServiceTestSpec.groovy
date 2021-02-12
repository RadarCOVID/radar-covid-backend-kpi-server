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

import java.security.PrivateKey
import java.security.cert.Certificate
import java.time.Instant

import javax.annotation.Resource

import org.apache.commons.codec.digest.DigestUtils
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

import com.auth0.jwt.JWT
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.webtoken.JsonWebSignature
import com.google.api.client.util.Base64;
import com.google.api.client.util.Lists

import es.gob.radarcovid.common.exception.KpiServerException
import es.gob.radarcovid.kpi.api.GoogleKpiDto
import es.gob.radarcovid.kpi.api.KpiDto
import es.gob.radarcovid.kpi.business.KpiService
import es.gob.radarcovid.kpi.client.impl.DeviceCheckClientServiceImpl
import es.gob.radarcovid.kpi.etc.KpiProperties
import es.gob.radarcovid.kpi.persistence.AuthenticationDao
import es.gob.radarcovid.kpi.persistence.repository.KpiEntityRepository
import es.gob.radarcovid.kpi.safetynet.AttestationStatement
import es.gob.radarcovid.kpi.safetynet.SafetyNetValidator
import es.gob.radarcovid.kpi.util.HashingService
import spock.lang.Specification
import spock.lang.Unroll


@SpringBootTest
@ActiveProfiles("test")
class KpiServiceTestSpec extends Specification {

	@Autowired
	KpiService service

	@Autowired
	KpiProperties kpiProperties

	@Autowired
	KpiEntityRepository kpiEntityRepository

	@Autowired
	AuthenticationDao authenticationDao

	@Autowired
	HashingService hashingService

	@Resource(name = 'safetyNetPrivateKey')
	PrivateKey privateKey

	@Resource(name = 'safetyNetCertificate')
	Certificate certificate

	@SpringBean
	DeviceCheckClientServiceImpl deviceCheckClientService = Mock()

	def "get kpi types"() {
		expect:
		service.getKpiTypes().size() == 9
	}

	def "save"(String kpiName, int kpiValue) {
		given:
		def oldSize = kpiEntityRepository.findAll().size()

		def kpiDto = new KpiDto()
		kpiDto.kpi = kpiName
		kpiDto.value = kpiValue
		kpiDto.timestamp = new Date()
		List<KpiDto> dtoList = Arrays.asList(kpiDto)

		when:
		service.save(dtoList)

		then:
		kpiEntityRepository.findAll().size() > oldSize

		where:
		kpiName               | kpiValue
		"BLUETOOTH_ACTIVATED" | 1
		"MATCH_CONFIRMED"     | 1
	}

	@Unroll
	def "save google [#salt]"(String salt, String kpiName, int kpiValue, int result) {
		given:
		def oldSize = kpiEntityRepository.findAll().size()

		and:
		def kpiDto = new KpiDto()
		kpiDto.kpi = kpiName
		kpiDto.value = kpiValue
		kpiDto.timestamp = new Date()
		List<KpiDto> kpis = Arrays.asList(kpiDto)

		def googleKpiDto = new GoogleKpiDto()
		googleKpiDto.salt = salt
		googleKpiDto.signedAttestation = createSafetyNetToken(kpis, salt)
		googleKpiDto.kpis = kpis

		when:
		service.saveGoogle(googleKpiDto)

		then:
		kpiEntityRepository.findAll().size() - oldSize == result

		where:
		salt                       | kpiName               | kpiValue | result
		"gc3r5viXnWKPzb+Xl/0EHA==" | "BLUETOOTH_ACTIVATED" | 1        | 1
		"di9saqk8AyQXQKiK1LeA7w==" | "MATCH_CONFIRMED"     | 1        | 1
		"di9saqk8AyQXQKiK1LeA7w==" | "MATCH_CONFIRMED"     | 1        | 0
		"GbCGJQMy5FCkqaCtsA+rmQ==" | "MATCH_CONFIRMED"     | 0        | 0
	}

	def createSafetyNetToken(List<KpiDto> kpis, String salt) {
		JsonWebSignature.Header header = new JsonWebSignature.Header()
		header.setAlgorithm("RS256")
		List<String> certificates = Lists.newArrayList()
		certificates.add(Base64.encodeBase64String(certificate.encoded))
		header.setX509Certificates(certificates)

		AttestationStatement payload = new AttestationStatement()
		payload.nonce = SafetyNetValidator.generateNonce(kpis, salt)
		payload.timestampMs = Instant.now().toEpochMilli()
		payload.apkPackageName = kpiProperties.safetyNet.packageName
		payload.apkCertificateDigestSha256 = [
			kpiProperties.safetyNet.apkDigest
		]
		payload.ctsProfileMatch = true
		payload.basicIntegrity = true

		return JsonWebSignature.signUsingRsaSha256(privateKey, JacksonFactory.getDefaultInstance(), header, payload);
	}

	@Unroll
	def "authenticate token [#deviceToken]"(String deviceToken, boolean valid) {
		given:
		def tokenHash = hashingService.hash(deviceToken)

		and:
		deviceCheckClientService.validate(deviceToken) >> valid

		when:
		service.authenticateToken(deviceToken)
		Thread.sleep(2000)

		then:
		def authentication = authenticationDao.find(tokenHash)
		authentication.present
		authentication.get() == valid

		where:
		deviceToken | valid
		"token-11"  | true
		"token-12"  | false
	}

	@Unroll
	def "verify token [#deviceToken] OK"(String deviceToken) {
		given:
		def tokenHash = hashingService.hash(deviceToken)
		authenticationDao.save(tokenHash, true)

		when:
		def jwt = service.verifyToken(deviceToken)

		then:
		jwt.present
		JWT.decode(jwt.get()).subject == tokenHash

		where:
		deviceToken | _
		"token-21"  | _
	}
	
	@Unroll
	def "verify token [#deviceToken] KO"(String deviceToken) {	
		when:
		def jwt = service.verifyToken(deviceToken)

		then:
		jwt.empty		

		where:
		deviceToken | _
		"token-31"  | _
	}

	@Unroll
	def "verify token [#deviceToken] with exception"(String deviceToken) {
		given:
		def tokenHash = hashingService.hash(deviceToken)
		authenticationDao.save(tokenHash, false)

		when:
		service.verifyToken(deviceToken)

		then:
		KpiServerException exception = thrown()
		exception.httpStatus == HttpStatus.UNAUTHORIZED

		where:
		deviceToken | _
		"token-41"  | _
	}
	
	@Unroll
	def "save apple [#salt]"(String salt, String kpiName, int kpiValue, int result) {
		given:
		def oldSize = kpiEntityRepository.findAll().size()

		and:
		def kpiDto = new KpiDto()
		kpiDto.kpi = kpiName
		kpiDto.value = kpiValue
		kpiDto.timestamp = new Date()
		List<KpiDto> kpis = Arrays.asList(kpiDto)

		when:
		service.saveApple(salt, kpis)

		then:
		kpiEntityRepository.findAll().size() - oldSize == result

		where:
		salt                              | kpiName               | kpiValue | result
		DigestUtils.sha256Hex("token-51") | "BLUETOOTH_ACTIVATED" | 1        | 1
		DigestUtils.sha256Hex("token-52") | "MATCH_CONFIRMED"     | 1        | 1
		DigestUtils.sha256Hex("token-52") | "MATCH_CONFIRMED"     | 1        | 0
		DigestUtils.sha256Hex("token-53") | "MATCH_CONFIRMED"     | 0        | 0
	}
}
