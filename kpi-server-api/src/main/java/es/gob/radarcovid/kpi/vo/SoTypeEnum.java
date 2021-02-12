/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.vo;

import lombok.Getter;

public enum SoTypeEnum {

	ANDROID(1), 
	IOS(2);
	
	@Getter
	private final Integer id;

	SoTypeEnum(Integer id) {
		this.id = id;
	}

}
