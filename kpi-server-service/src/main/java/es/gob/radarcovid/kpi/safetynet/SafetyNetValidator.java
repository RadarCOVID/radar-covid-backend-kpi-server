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

import java.util.Base64;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import es.gob.radarcovid.kpi.api.KpiDto;

public interface SafetyNetValidator {

	boolean verify(String signedAttestationStatment, String nonce);
	
	static String generateNonce(List<KpiDto> kpis, String salt) {
		StringBuilder sb = new StringBuilder();
		kpis.forEach(kpi -> {
			sb.append(kpi.getKpi() != null ? kpi.getKpi() : "");
			sb.append(kpi.getTimestamp() != null ? kpi.getTimestamp().toInstant().toEpochMilli() : "");
			sb.append(kpi.getValue() != null ? kpi.getValue() : "");
		});
		sb.append(salt);
		return Base64.getEncoder().encodeToString(DigestUtils.sha256(sb.toString()));
	}

}
