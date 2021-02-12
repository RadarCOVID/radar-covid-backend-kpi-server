/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.verification.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import es.gob.radarcovid.kpi.domain.CcaaDto;

public interface CodeDao {

	@Transactional(readOnly = true)
    Long countRedeemedCodes(LocalDate date, List<CcaaDto> ccaas);
    
	@Transactional(readOnly = true)
    Long countRedeemedCodes(LocalDate date, CcaaDto ccaa);
    
	@Transactional(readOnly = true)
    Long countTotalCodes(LocalDate date, List<CcaaDto> ccaas);
    
	@Transactional(readOnly = true)
    Long countTotalCodes(LocalDate date, CcaaDto ccaa);

}
