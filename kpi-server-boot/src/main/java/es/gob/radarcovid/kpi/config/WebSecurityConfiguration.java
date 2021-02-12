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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import es.gob.radarcovid.common.config.AppleTokenAuthenticationEntryPoint;
import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.kpi.controller.AppleKpiController;
import es.gob.radarcovid.kpi.persistence.AuthenticationDao;
import es.gob.radarcovid.kpi.security.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationDao authenticationDao;
    private final AppleTokenAuthenticationEntryPoint unauthorizedHandler;
    private final KeyVault keyVault;
    
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
            .headers()
                .and()
            .antMatcher(AppleKpiController.APPLE_KPI_ROUTE)
            .addFilterAfter(new JwtAuthorizationFilter(authenticationDao, resolver, keyVault), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
            .csrf().disable()
            .cors();
        // @formatter.on
    }

}
