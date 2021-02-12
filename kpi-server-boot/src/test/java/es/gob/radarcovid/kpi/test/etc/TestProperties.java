/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.test.etc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("test")
@Getter
@Setter
public class TestProperties {

	private final SafetyNetCert safetyNetCerts = new SafetyNetCert();

	@Getter
	@Setter
	public static class SafetyNetCert {
		private Resource trustStore;
		private Resource certificate;
		private Resource privateKey;
		private String algorithm;
	}

}
