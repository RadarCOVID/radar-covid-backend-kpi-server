/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.security;

import java.io.IOException;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import es.gob.radarcovid.common.security.KeyVault;
import es.gob.radarcovid.kpi.etc.Constants;
import es.gob.radarcovid.kpi.persistence.AuthenticationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final AuthenticationDao authenticationDao;
    private final HandlerExceptionResolver resolver;
    private final KeyVault keyVault;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            if (existsTokenJWT(request)) {
                Optional<String> optionalToken = validateToken(request);
                if (optionalToken.isPresent()) {
                    setUpSpringAuthentication(optionalToken.get());
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.warn("JWT not exists({})", request.getServletPath());
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            log.error("Exception reading token JWT: {}", e.getMessage());
            resolver.resolveException(request, response, null, e);
        }
        
    }

    private boolean existsTokenJWT(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
        return (!StringUtils.isEmpty(authenticationHeader) && authenticationHeader.startsWith(
                Constants.AUTHORIZATION_PREFIX));
    }
    
	private Optional<String> validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (!StringUtils.isEmpty(Constants.AUTHORIZATION_PREFIX)) {
            jwtToken = jwtToken.replace(Constants.AUTHORIZATION_PREFIX, "");
        }
        try {
            ECPublicKey publicKey = (ECPublicKey) keyVault.get(Constants.PAIR_KEY_RADAR).getPublic();
            Algorithm algorithm = Algorithm.ECDSA512(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(jwtToken);
            String hash = jwt.getSubject();
            Optional<Boolean> authenticated = authenticationDao.find(hash);
    		if (authenticated.isPresent() && authenticated.get()) {
    			return Optional.of(hash);
    		}
    		log.warn("Not found data for token device");
        } catch (TokenExpiredException ex) {
            log.warn("JWT expired");
        } catch (Exception ex) {
            throw new JWTVerificationException("Token verify error");
        }
		return Optional.empty();
	}

	private void setUpSpringAuthentication(String principal) {
		final List<String> authorizationList = new ArrayList<>(List.of(Constants.AUTH_RADARCOVID));
		log.debug("PRINCIPAL:{}|AUTHORITIES:{}", principal, authorizationList);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal,
				null, authorizationList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

}
