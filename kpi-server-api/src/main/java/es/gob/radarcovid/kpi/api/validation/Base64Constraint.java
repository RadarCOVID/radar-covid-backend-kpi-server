/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.api.validation;


import javax.validation.Constraint;
import javax.validation.Payload;

import es.gob.radarcovid.kpi.api.validation.impl.Base64Validator;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Base64Validator.class)
public @interface Base64Constraint {

    String message() default "Invalid base64";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
