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
package com.manorrock.piranha.nano;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * The Nano version of Piranha.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class NanoPiranha {

    /**
     * Stores the filters.
     */
    private final LinkedList<Filter> filters;

    /**
     * Stores the servlet.
     */
    private Servlet servlet;

    /**
     * Stores the servlet context.
     */
    private NanoServletContext servletContext;

    /**
     * Constructor.
     */
    public NanoPiranha() {
        filters = new LinkedList<>();
        servletContext = new NanoServletContext();
    }

    /**
     * Add a filter.
     *
     * @param filter the filter.
     */
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    /**
     * Get the filters.
     * 
     * @return the filters.
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Get the servlet.
     * 
     * @return the servlet.
     */
    public Servlet getServlet() {
        return servlet;
    }
    
    /**
     * Get the servlet context.
     *
     * @return the servlet context.
     */
    public NanoServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Service.
     *
     * @param servletRequest the request.
     * @param servletResponse the response.
     * @throws IOException when an I/O error occurs.
     * @throws ServletException when a Servlet error occurs.
     */
    public void service(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IOException, ServletException {
        Iterator<Filter> iterator = filters.descendingIterator();
        NanoFilterChain chain = new NanoFilterChain(servlet);
        while (iterator.hasNext()) {
            Filter filter = iterator.next();
            NanoFilterChain previousChain = chain;
            chain = new NanoFilterChain(filter, previousChain);
        }
        /*
         * If you are passing in your own ServletContext we are going to assume
         * you know what your are doing. If you did not supply one, we will supply
         * ours, provided you used our companion NanoRequest. If you
         * did not use a NanoRequest we again assume you know what you
         * are doing.
         */
        if (servletRequest.getServletContext() == null
                && servletRequest instanceof NanoRequest) {
            NanoRequest nanoRequest = (NanoRequest) servletRequest;
            nanoRequest.setServletContext(servletContext);
        }
        chain.doFilter(servletRequest, servletResponse);
        servletResponse.flushBuffer();
    }

    /**
     * Set the servlet.
     *
     * @param servlet the servlet.
     */
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    /**
     * Set the servlet context.
     *
     * @param servletContext the Nano servlet context.
     */
    public void setServletContext(NanoServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
