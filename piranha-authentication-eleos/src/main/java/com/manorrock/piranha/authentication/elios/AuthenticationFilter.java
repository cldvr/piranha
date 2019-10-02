/*
 * Copyright (c) 2002-2019 Manorrock.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *   3. Neither the name of the copyright holder nor the names of its 
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.piranha.authentication.elios;

import static com.manorrock.piranha.authentication.elios.AuthenticationInitializer.AUTH_SERVICE;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omnifaces.eleos.config.helper.Caller;
import org.omnifaces.eleos.services.DefaultAuthenticationService;

import com.manorrock.piranha.DefaultAuthenticatedIdentity;
import com.manorrock.piranha.DefaultWebApplicationRequest;

/**
 * This filter is uses to call a Jakarta Authentication system module at the start of an HTTP request.
 * 
 * <p>
 * Note, this Filter *MUST* be installed as the first filter, and it should *NOT* be possible to place
 * a filter before this filter. The standard Servlet API does not provide facilitities for this.
 * 
 * @author Arjan Tijms
 *
 */
public class AuthenticationFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    private DefaultAuthenticationService authenticationService;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        authenticationService = (DefaultAuthenticationService) filterConfig.getServletContext().getAttribute(AUTH_SERVICE);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        Caller caller = authenticationService.validateRequest(request, response, false, e -> true);

        if (caller != null) {

            DefaultWebApplicationRequest defaultWebApplicationRequest = (DefaultWebApplicationRequest) request;
            defaultWebApplicationRequest.setUserPrincipal(caller.getCallerPrincipal());
            
            DefaultAuthenticatedIdentity.setCurrentIdentity(caller.getCallerPrincipal(), caller.getGroups());

            chain.doFilter(request, response);
        }

    }

}