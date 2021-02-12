/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import es.gob.radarcovid.kpi.etc.KpiProperties;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "es.gob.radarcovid")
@EnableConfigurationProperties(KpiProperties.class)
@EnableAsync
public class KpiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KpiApplication.class, args);
    }

}
