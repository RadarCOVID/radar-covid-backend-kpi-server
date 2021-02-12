/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.security.impl;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.kpi.etc.Constants;
import es.gob.radarcovid.kpi.etc.KpiProperties;
import es.gob.radarcovid.kpi.security.JwtGenerator;
import es.gob.radarcovid.kpi.util.HashingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtGeneratorImpl implements JwtGenerator {
	
	private static final String KEY_ID_HEADER = "kid";
	
    @Value("${application.jwt.issuer}")
    private String jwtIssuer;
    
	@Value("${application.jwt.minutes}")
    private long minutes;

    private final KeyVault keyVault;
    private final KpiProperties kpiProperties;
    private final HashingService hashingService;

    @Override
    public String generateRadarJwt(String token) {

        KeyPair keyPair = keyVault.get(Constants.PAIR_KEY_RADAR);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        Algorithm algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        String jwtId = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date validUntil = Date.from(Instant.now().plus(minutes, ChronoUnit.MINUTES));
        String hash = hashingService.hash(token);
        
        return JWT.create()
                .withJWTId(jwtId)
                .withSubject(hash)
                .withIssuer(jwtIssuer)
                .withIssuedAt(issuedAt)
                .withExpiresAt(validUntil)
                .sign(algorithm);
    }

	@Override
	public String generateDeviceTokenJwt() {
		
        KeyPair keyPair = keyVault.get(Constants.PAIR_KEY_DEVICE_CHECK);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        
        Algorithm algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        Date issuedAt = new Date();
        Map<String, Object> headers = Map.of(KEY_ID_HEADER, kpiProperties.getDeviceCheck().getKeyId());
		
		return JWT.create()
				.withIssuer(kpiProperties.getDeviceCheck().getTeamId())
				.withIssuedAt(issuedAt)
				.withHeader(headers)
				.sign(algorithm);
	}

}
