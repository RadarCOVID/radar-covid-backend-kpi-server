/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.test.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import com.google.api.client.util.PemReader;
import com.google.api.client.util.SecurityUtils;

import es.gob.radarcovid.common.exception.KpiServerException;
import es.gob.radarcovid.kpi.test.etc.TestProperties;
import lombok.extern.slf4j.Slf4j;

@TestConfiguration
@EnableConfigurationProperties(TestProperties.class)
@Slf4j
public class KpiTestConfiguration {

	@Autowired
	TestProperties testProperties;

	@Bean
	PrivateKey safetyNetPrivateKey() {
		try {
			Resource privateKey = testProperties.getSafetyNetCerts().getPrivateKey();
			byte[] encoded = PemReader
					.readFirstSectionAndClose(new InputStreamReader(privateKey.getInputStream()), "PRIVATE KEY")
					.getBase64DecodedBytes();
			KeyFactory keyFactory = KeyFactory.getInstance(testProperties.getSafetyNetCerts().getAlgorithm());
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
			return keyFactory.generatePrivate(keySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.warn("Error loading safety net private key: {}", e.getMessage(), e);
			throw new KpiServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Bean
	Certificate safetyNetCertificate() {
		try {
			Resource certificate = testProperties.getSafetyNetCerts().getCertificate();
			String pem = IOUtils.toString(certificate.getInputStream(), StandardCharsets.UTF_8);
			byte[] bytes = PemReader.readFirstSectionAndClose(new StringReader(pem), "CERTIFICATE")
					.getBase64DecodedBytes();
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			return SecurityUtils.getX509CertificateFactory().generateCertificate(bis);
		} catch (IOException | CertificateException e) {
			log.warn("Error loading safety net certificate: {}", e.getMessage(), e);
			throw new KpiServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@PostConstruct
	public void postConstruct() throws IOException {
		String trustStorePath = testProperties.getSafetyNetCerts().getTrustStore().getURI().getPath();		
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
	}

}
