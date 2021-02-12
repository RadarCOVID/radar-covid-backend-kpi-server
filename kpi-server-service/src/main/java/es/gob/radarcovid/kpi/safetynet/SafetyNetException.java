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

import lombok.Getter;

@Getter
public class SafetyNetException extends Exception {

    private final String signedAttestationStatment;

    /**
     * The Constructor for the Exception class.
     *
     * @param signedAttestationStatment the safety net token
     * @param message                   the message
     */
    public SafetyNetException(String signedAttestationStatment, String message) {
        super(message);
        this.signedAttestationStatment = signedAttestationStatment;
    }
}
