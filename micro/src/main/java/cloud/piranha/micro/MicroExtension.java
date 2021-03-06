/*
 * Copyright (c) 2002-2020 Manorrock.com. All Rights Reserved.
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
package cloud.piranha.micro;

import cloud.piranha.api.WebApplicationExtension;
import cloud.piranha.api.WebApplicationExtensionContext;
import cloud.piranha.pages.jasper.JasperExtension;
import cloud.piranha.servlet.annotationscan.AnnotationScanExtension;
import cloud.piranha.servlet.servletcontainerinitializer.ServletContainerInitializerExtension;
import cloud.piranha.servlet.webxml.WebXmlExtension;

/**
 * The {@link cloud.piranha.api.WebApplicationExtension} used by
 * {@link cloud.piranha.micro.MicroPiranha} to configure each web application.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 * @see cloud.piranha.micro.MicroPiranha
 * @see cloud.piranha.api.WebApplicationExtension
 */
public class MicroExtension implements WebApplicationExtension {

    /**
     * Extend the web application.
     *
     * @param context the context.
     */
    @Override
    public void extend(WebApplicationExtensionContext context) {
        context.add(AnnotationScanExtension.class);
        context.add(WebXmlExtension.class);
        context.add(JasperExtension.class);
        context.add(ServletContainerInitializerExtension.class);
    }
}
