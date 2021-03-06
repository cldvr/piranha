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
package cloud.piranha.rest.jersey;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.servlet.init.JerseyServletContainerInitializer;

import cloud.piranha.DefaultWebApplication;

/**
 * The Jersey initializer.
 * 
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class JerseyInitializer implements ServletContainerInitializer {

    /**
     * Initialize Mojarra.
     * 
     * @param classes the classes.
     * @param servletContext the Servlet context.
     * @throws ServletException when a Servlet error occurs.
     */
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        
        DefaultWebApplication context = (DefaultWebApplication) servletContext;
        
        Set<Class<?>> jerseyClasses = new HashSet<>();
        
        context.getAnnotationManager()
               .getAnnotations(Path.class, Provider.class, ApplicationPath.class)
               .stream()
               .map(e -> e.getTarget())
               .forEach(e -> addClass(e, jerseyClasses));
        
        
        jerseyClasses.addAll(context.getAnnotationManager().getInstances(Application.class));
        
        JerseyServletContainerInitializer containerInitializer = new JerseyServletContainerInitializer();
        containerInitializer.onStartup(jerseyClasses, servletContext);
    }
    
    private void addClass(AnnotatedElement e, Set<Class<?>> jerseyClasses) {
        if (e instanceof Class) {
            jerseyClasses.add((Class<?>)e);
            return;
        }
        
        jerseyClasses.add(((Method) e).getDeclaringClass());
    }
}
