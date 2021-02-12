/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.safetynet;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Key;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
@EqualsAndHashCode(callSuper = true)
public class AttestationStatement extends JsonWebSignature.Payload {
	
	/**
	 * Embedded nonce sent as part of the request.
	 */
	@Key
	private String nonce;

	/**
	 * Timestamp of the request.
	 */
	@Key
	private long timestampMs;

	/**
	 * Package name of the APK that submitted this request.
	 */
	@Key
	private String apkPackageName;

	/**
	 * Digest of certificate of the APK that submitted this request.
	 */
	@Key
	private String[] apkCertificateDigestSha256;

	/**
	 * Digest of the APK that submitted this request.
	 */
	@Key
	private String apkDigestSha256;

	/**
	 * The device passed CTS and matches a known profile.
	 */
	@Key
	private boolean ctsProfileMatch;

	/**
	 * The device has passed a basic integrity test, but the CTS profile could not
	 * be verified.
	 */
	@Key
	private boolean basicIntegrity;
	
	/**
	 * Evaluation type
	 */
	@Key
	private String evaluationType;

}
