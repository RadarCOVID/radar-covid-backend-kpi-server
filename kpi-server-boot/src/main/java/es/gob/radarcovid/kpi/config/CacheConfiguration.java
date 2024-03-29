/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import es.gob.radarcovid.kpi.etc.Constants;
import es.gob.radarcovid.kpi.handler.CustomizedCacheErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfiguration extends CachingConfigurerSupport implements CachingConfigurer {

	private static final String SEPARATOR = "::";

	@Value("${application.cache.time-to-live.default:PT30M}")
	private Duration cacheTimeToLive;
	
	@Value("${application.cache.time-to-live.authentication:P7D}")
	private Duration authTimeToLive;
	
	@Value("${application.cache.prefix-name:kpi}")
	private String cachePrefixName;

	@Override
	public CacheErrorHandler errorHandler() {
		return new CustomizedCacheErrorHandler();
	}

	@Bean
	@DependsOn(value = { "noOpCacheManager", "redisCacheManager" })
	@Primary
	public CacheManager cacheManager(@Qualifier("noOpCacheManager") CacheManager noOpCacheManager,
			                         @Qualifier("redisCacheManager") CacheManager redisCacheManager) {
		CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
		compositeCacheManager.setFallbackToNoOpCache(true);
		List<CacheManager> cacheManagerList = new ArrayList<>();
		cacheManagerList.add(redisCacheManager);
		cacheManagerList.add(noOpCacheManager);
		compositeCacheManager.setCacheManagers(cacheManagerList);
		return compositeCacheManager;
	}

	@Bean("noOpCacheManager")
	public CacheManager noOpCacheManager() {
		return new NoOpCacheManager();
	}

	@Bean("redisCacheManager")
	public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
		Set<String> cacheNames = Stream.of(
				Constants.CACHE_FIND_KPI_TYPE_BY_NAME_AND_ENABLED,
				Constants.CACHE_KPI_TYPE_ENABLED,
				Constants.CACHE_SALT
			).collect(Collectors.toSet());
		
		CacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(redisCacheConfiguration())
				.initialCacheNames(cacheNames)
				.withCacheConfiguration(Constants.CACHE_AUTHENTICATION,
						redisCacheConfiguration().entryTtl(authTimeToLive))
				.build();
		log.debug("Redis cache enabled");
		return cacheManager;
	}

	private RedisCacheConfiguration redisCacheConfiguration() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.entryTtl(cacheTimeToLive)
				.prefixCacheNameWith(cachePrefixName + SEPARATOR)
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
		return redisCacheConfiguration;
	}
	
}
