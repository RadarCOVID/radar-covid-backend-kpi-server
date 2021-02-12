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

import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import es.gob.radarcovid.common.exception.KpiServerException;
import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.kpi.etc.Constants;
import es.gob.radarcovid.kpi.etc.KpiProperties;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KeysConfiguration {

    @Autowired
    KpiProperties kpiProperties;
    
    @Value("${application.credentials.privateKey:}")
    private String credentialsPrivateKey;

    @Value("${application.credentials.publicKey:}")
    private String credentialsPublicKey;
    
    @Value("${application.credentials.algorithm:}")
    private String credentialsAlgorithm;

    @Bean
    KeyVault keyVault() {
        Security.addProvider(new BouncyCastleProvider());
        Security.setProperty("crypto.policy", "unlimited");

        try {
            var radar = createRadarKeys();
            log.debug("Loaded radar keys");
            
            var deviceCheck = createDeviceCheckKeys();
            log.debug("Loaded device check keys");
            
            return new KeyVault(radar, deviceCheck);
            
        } catch (KeyVault.PrivateKeyNoSuitableEncodingFoundException | KeyVault.PublicKeyNoSuitableEncodingFoundException | IOException e) {
            log.warn("Error loading keys: {}", e.getMessage(), e);
            throw new KpiServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
	private KeyVault.KeyVaultEntry createRadarKeys() throws IOException {
		var privateKey = KeyVault.loadKey(credentialsPrivateKey);
		var publicKey = KeyVault.loadKey(credentialsPublicKey);

		return new KeyVault.KeyVaultEntry(Constants.PAIR_KEY_RADAR, privateKey, publicKey, credentialsAlgorithm);
	}
    
    private KeyVault.KeyVaultEntry createDeviceCheckKeys() throws IOException {
        var privateKey = KeyVault.loadKey(kpiProperties.getDeviceCheck().getCredentials().getPrivateKey());
        var publicKey = KeyVault.loadKey(kpiProperties.getDeviceCheck().getCredentials().getPublicKey());

        return new KeyVault.KeyVaultEntry(Constants.PAIR_KEY_DEVICE_CHECK, privateKey, publicKey,
        		kpiProperties.getDeviceCheck().getCredentials().getAlgorithm());
    }

}
