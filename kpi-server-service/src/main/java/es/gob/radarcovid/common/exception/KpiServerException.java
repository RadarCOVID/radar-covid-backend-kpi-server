/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KpiServerException extends RuntimeException {

    private final HttpStatus httpStatus;

    /**
     * The Constructor for the Exception class.
     *
     * @param httpStatus the state of the server
     * @param message    the message
     */
    public KpiServerException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
