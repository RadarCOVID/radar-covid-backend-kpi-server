/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.etc;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import es.gob.radarcovid.kpi.vo.GraphicStatisticsTypeEnum;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("application.kpi")
@Getter
@Setter
public class KpiProperties {
	
    private final Statistics statistics = new Statistics();
    private final SafetyNet safetyNet = new SafetyNet();
    private final DeviceCheck deviceCheck = new DeviceCheck();
    
    @Getter
    @Setter
    public static class Statistics {
        private final Batching batching = new Batching();
        private String ccaaDiagnosisCsv;
        private final Map<GraphicStatisticsTypeEnum, StatisticData> data = new HashMap<>();
    }
    
    @Getter
    @Setter
    public static class Batching {
        private int timeInterval = 300000;
        private int lockLimit = 1800000;
    }
    
    @Getter
    @Setter
    public static class StatisticData {
        private String title;
        private String description;
        private Map<GraphicStatisticsTypeEnum, String> included = new HashMap<>();;
    }
    
    @Getter
    @Setter
    public static class SafetyNet {
    	private Duration timeSkew;
    	private String packageName;
    	private String apkDigest;
    }
    
    @Getter
    @Setter
    public static class DeviceCheck {
    	private Credentials credentials = new Credentials();
    	private String teamId;
    	private String keyId;
    	private String host;
    	private Endpoints endpoints = new Endpoints();
        
        @Getter
        @Setter
        public static class Endpoints {
            private String query;
            private String update;
            private String validate;
        }
    }
    
    @Getter
    @Setter
    public static class Credentials {
    	private String privateKey;
        private String publicKey;
        private String algorithm;
    }

}
