/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.kpi.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import es.gob.radarcovid.kpi.etc.Constants;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MdcTrackingHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        MDC.remove(Constants.TRACKING);
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler,
                           final ModelAndView modelAndView) {
        MDC.remove(Constants.TRACKING);
    }
}
