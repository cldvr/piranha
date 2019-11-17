/*
 *  Copyright (c) 2002-2019, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.piranha.runner.war;

import static com.manorrock.piranha.authorization.exousia.AuthorizationPreInitializer.AUTHZ_FACTORY_CLASS;
import static com.manorrock.piranha.authorization.exousia.AuthorizationPreInitializer.AUTHZ_POLICY_CLASS;
import static java.util.Arrays.stream;
import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static org.jboss.jandex.DotName.createSimple;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;
import org.jboss.shrinkwrap.api.Archive;
import org.omnifaces.exousia.modules.def.DefaultPolicy;
import org.omnifaces.exousia.modules.def.DefaultPolicyConfigurationFactory;

import com.manorrock.piranha.DefaultAnnotationManager;
import com.manorrock.piranha.DefaultAnnotationManager.DefaultAnnotationInfo;
import com.manorrock.piranha.DefaultHttpServer;
import com.manorrock.piranha.DefaultWebApplication;
import com.manorrock.piranha.DefaultWebApplicationServer;
import com.manorrock.piranha.api.HttpServer;
import com.manorrock.piranha.api.WebApplication;
import com.manorrock.piranha.authentication.elios.AuthenticationInitializer;
import com.manorrock.piranha.authorization.exousia.AuthorizationInitializer;
import com.manorrock.piranha.authorization.exousia.AuthorizationPreInitializer;
import com.manorrock.piranha.security.jakarta.JakartaSecurityInitializer;
import com.manorrock.piranha.security.soteria.SoteriaInitializer;
import com.manorrock.piranha.servlet.ServletFeature;
import com.manorrock.piranha.servlet.WebAnnotationInitializer;
import com.manorrock.piranha.shrinkwrap.ShrinkWrapResource;
import com.manorrock.piranha.webxml.WebXmlInitializer;
import com.manorrock.piranha.weld.WeldInitializer;

/**
 * Deploys a shrinkwrap application archive to a newly started embedded Piranha instance.
 * 
 * @author arjan
 *
 */
public class PiranhaServerDeployer {
    
    Class<?>[] webAnnotations = new Class<?>[] {
       WebServlet.class, 
       WebListener.class,
       WebInitParam.class,
       WebFilter.class,
       ServletSecurity.class,
       MultipartConfig.class};
    
    private HttpServer httpServer;
    
    public Set<String> start(Archive<?> applicationArchive, ClassLoader classLoader) {
        
        System.getProperties().put(INITIAL_CONTEXT_FACTORY, DynamicInitialContextFactory.class.getName());
        
        WebApplication webApplication = getWebApplication(applicationArchive, classLoader);
        
        // Source of annotations
        Index index = getIndex();
        
        // Target of annotations
        DefaultAnnotationManager annotationManager = (DefaultAnnotationManager) webApplication.getAnnotationManager();
        
        // Copy from source index to target manager
        forEachWebAnnotation(webAnnotation ->
            // Read the web annotations (@WebServlet.class etc) from the source index
            getAnnotations(index, webAnnotation)
                // Get the annotation target and annotation instance corresponding to the
                // (raw/abstract) indexed annotation
                .map(indexedAnnotation -> getTarget(indexedAnnotation))
                .forEach(annotationTarget -> 
                    getAnnotationInstances(annotationTarget, webAnnotation)
                        .forEach(annotationInstance ->  
                            // Store the matching annotation instance (@WebServlet(name=...)
                            // and annotation target (@WebServlet public class Target) in the manager
                            annotationManager.addAnnotation(
                                new DefaultAnnotationInfo<>(annotationInstance,  annotationTarget)))));
            
        
        DefaultWebApplicationServer webApplicationServer = new DefaultWebApplicationServer();
        webApplication.addFeature(new ServletFeature());
        webApplicationServer.addWebApplication(webApplication);
        
        
        webApplication.addInitializer(new WebXmlInitializer());
        webApplication.addInitializer(new WebAnnotationInitializer());
        
        webApplication.addInitializer(WeldInitializer.class.getName());
        
        webApplication.setAttribute(AUTHZ_FACTORY_CLASS, DefaultPolicyConfigurationFactory.class);
        webApplication.setAttribute(AUTHZ_POLICY_CLASS, DefaultPolicy.class);
        
        webApplication.addInitializer(AuthorizationPreInitializer.class.getName());
        webApplication.addInitializer(AuthenticationInitializer.class.getName());
        webApplication.addInitializer(AuthorizationInitializer.class.getName());
        webApplication.addInitializer(JakartaSecurityInitializer.class.getName());
        
        webApplication.addInitializer(SoteriaInitializer.class.getName());
        
        webApplicationServer.initialize();
        webApplicationServer.start();
        
        httpServer = new DefaultHttpServer(8080, webApplicationServer);
        httpServer.start();
        
        return webApplication.getServletRegistrations().keySet();
    }
    
    WebApplication getWebApplication(Archive<?> archive, ClassLoader newClassLoader) {
        WebApplication webApplication = new DefaultWebApplication();
        webApplication.setClassLoader(newClassLoader);
        webApplication.addResource(new ShrinkWrapResource(archive));
        
        return webApplication;
    }

    public void stop() {
        httpServer.stop();
    }
    
    Index getIndex() {
        ClassLoader classLoader= Thread.currentThread().getContextClassLoader();
        
        try (InputStream indexStream = classLoader.getResourceAsStream("META-INF/piranha.idx")) {
            return new IndexReader(indexStream).read();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    void forEachWebAnnotation(Consumer<? super Class<?>> consumer) {
        stream(webAnnotations).forEach(consumer);
    }
    
    Stream<AnnotationInstance> getAnnotations(Index index, Class<?> webAnnotation) {
        return 
            index.getAnnotations(
                    createSimple(webAnnotation.getName()))
                 .stream();
    }
    
    Class<?> getTarget (AnnotationInstance annotationInstance) {
        try {
            return Class.forName(annotationInstance.target().asClass().toString());
        } catch (ClassNotFoundException e) {
           throw new IllegalStateException(e);
        }
    }
    
    Stream<Annotation> getAnnotationInstances(Class<?> target, Class<?> annotationType) {
        return stream(target.getAnnotations())
                .filter(e -> e.annotationType().isAssignableFrom(annotationType));
                
    }
    
}