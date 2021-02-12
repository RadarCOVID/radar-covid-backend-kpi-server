/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.safetynet.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.time.Instant;

import javax.net.ssl.SSLException;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;

import es.gob.radarcovid.kpi.etc.KpiProperties;
import es.gob.radarcovid.kpi.safetynet.AttestationStatement;
import es.gob.radarcovid.kpi.safetynet.SafetyNetException;
import es.gob.radarcovid.kpi.safetynet.SafetyNetValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "application.kpi.safety-net.simulate.enabled", havingValue = "false", matchIfMissing = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class SafetyNetValidatorImpl implements SafetyNetValidator {

	private static final String VALIDATION_FAILED_MESSAGE = "Validation of SafetyNet with nonce ({}) failed: {}";
	private static final DefaultHostnameVerifier HOSTNAME_VERIFIER = new DefaultHostnameVerifier();
	
	private final KpiProperties kpiProperties;

	@Override
	public boolean verify(String signedAttestationStatment, String nonce) {
		try {
			// Parse and verify JSON Web Signature format
			JsonWebSignature jws = verifySignedAttestation(signedAttestationStatment);

			// Verify payload
			if (!verifyPayload(jws.getPayload(), nonce)) {
				log.warn(VALIDATION_FAILED_MESSAGE, nonce, "Error during payload verification");
				return false;
			}

		} catch (SafetyNetException e) {
			log.warn(VALIDATION_FAILED_MESSAGE, nonce, e.getMessage());
			return false;
		}

		return true;
	}
	
	private static JsonWebSignature verifySignedAttestation(String signedAttestationStatment) throws SafetyNetException {
		// Parse JSON Web Signature format
		JsonWebSignature jws;
		try {
			jws = JsonWebSignature.parser(JacksonFactory.getDefaultInstance())
					.setPayloadClass(AttestationStatement.class).parse(signedAttestationStatment);
		} catch (IOException e) {
			throw new SafetyNetException(signedAttestationStatment, "SignedAttestationStatment is not valid JWS format");
		}

		// Verify the signature of the JWS and retrieve the signature certificate
		X509Certificate cert;
		try {
			cert = jws.verifySignature();
			if (cert == null) {
				throw new SafetyNetException(signedAttestationStatment, "Signature verification failed");
			}
		} catch (GeneralSecurityException e) {
			throw new SafetyNetException(signedAttestationStatment, "Error during cryptographic verification of the JWS signature");
		}

		// Verify the hostname of the certificate
		if (!verifyHostname("attest.android.com", cert)) {
			throw new SafetyNetException(signedAttestationStatment, "Certificate isn't issued for the hostname attest.android.com");
		}

		return jws;
	}

	private static boolean verifyHostname(String hostname, X509Certificate leafCert) {
		try {
			// Check that the hostname matches the certificate. This method throws an
			// exception if the cert could not be verified.
			HOSTNAME_VERIFIER.verify(hostname, leafCert);
		} catch (SSLException e) {
			return false;
		}
		return true;
	}
	
	private boolean verifyPayload(Payload payload, String nonce) {
		if (payload instanceof AttestationStatement) {
			AttestationStatement stmt = (AttestationStatement) payload;
			log.info("Attestation payload: {}", stmt);
			Instant now = Instant.now();
			return (stmt.getTimestampMs() >= now.minus(kpiProperties.getSafetyNet().getTimeSkew()).toEpochMilli()
					&& stmt.getTimestampMs() <= now.plus(kpiProperties.getSafetyNet().getTimeSkew()).toEpochMilli()
					&& stmt.getNonce().equals(nonce)
					&& stmt.getApkPackageName().equals(kpiProperties.getSafetyNet().getPackageName())
					&& stmt.getApkCertificateDigestSha256()[0].equals(kpiProperties.getSafetyNet().getApkDigest())
					&& stmt.isBasicIntegrity() 
					&& stmt.isCtsProfileMatch());
		}
		return false;
	}

}
