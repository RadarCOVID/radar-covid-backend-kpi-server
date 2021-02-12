/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.persistence;

import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import es.gob.radarcovid.kpi.etc.Constants;

public interface SaltDao {
	
    @Cacheable(cacheNames = Constants.CACHE_SALT, unless = "#result == null")
    default Optional<String> find(String key) {
    	return Optional.empty();
    }
	
    @CachePut(cacheNames = Constants.CACHE_SALT)
    default String save(String key) {
    	return key;
    }
    
}
