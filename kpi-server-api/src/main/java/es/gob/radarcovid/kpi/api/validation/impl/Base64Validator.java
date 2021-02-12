/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.api.validation.impl;

import java.util.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.gob.radarcovid.kpi.api.validation.Base64Constraint;

public class Base64Validator implements ConstraintValidator<Base64Constraint, String> {

    @Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			Base64.getDecoder().decode(value);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
}
