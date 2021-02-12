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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import es.gob.radarcovid.kpi.etc.Constants;

public interface AuthenticationDao {
	
	@Cacheable(cacheNames = Constants.CACHE_AUTHENTICATION, key = "#hash", unless = "#result == null")
	default Optional<Boolean> find(String hash) {
		return Optional.empty();
	}

	@CachePut(cacheNames = Constants.CACHE_AUTHENTICATION, key = "#hash")
	default boolean save(String hash, boolean isValid) {
		return isValid;
	}
	
	@CacheEvict(cacheNames = Constants.CACHE_AUTHENTICATION, key = "#hash")
	default void delete(String hash) {
		return;
	}
    
}
