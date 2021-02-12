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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import es.gob.radarcovid.kpi.safetynet.SafetyNetValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "application.kpi.safety-net.simulate.enabled", havingValue = "true")
@Component()
@RequiredArgsConstructor
@Slf4j
public class SafetyNetFakeValidatorImpl implements SafetyNetValidator {

    @Value("${application.kpi.safety-net.simulate.valid: true}")
    private boolean isValid;

    @Override
	public boolean verify(String signedAttestationStatment, String nonce) {
        log.debug("Entering SafetyNetFakeValidator.verify('{}')", nonce);
        boolean result = isValid;
        log.debug("Leaving SafetyNetFakeValidator.verify with: {}", result);
        return result;
	}

}
