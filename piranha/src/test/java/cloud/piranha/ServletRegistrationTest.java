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
package cloud.piranha;

import cloud.piranha.api.WebApplication;
import java.util.HashMap;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * The JUnit tests for testing everything related to the ServletRegistration
 * API.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class ServletRegistrationTest {

    /**
     * Stores the web application.
     */
    protected WebApplication webApp;

    /**
     * Setup before testing.
     *
     * @throws Exception when a serious error occurs.
     */
    @Before
    public void setUp() throws Exception {
        webApp = new DefaultWebApplication();
    }

    /**
     * Test getClassName method.
     */
    @Test
    public void testGetClassName() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        assertNotNull(TestServlet.class.getCanonicalName(), registration.getClassName());
    }

    /**
     * Test getInitParameter method.
     */
    @Test
    public void testGetInitParameter() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        registration.setInitParameter("name", "value");
        assertEquals("value", registration.getInitParameter("name"));
    }
    /**
     * Test getInitParameters method.
     */
    @Test
    public void testGetInitParameters() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        assertNotNull(registration.getInitParameters());
    }

    /**
     * Test getName method.
     */
    @Test
    public void testGetName() {
        webApp.addServlet("servlet", TestServlet.class);
        assertNotNull("servlet", webApp.getServletRegistration("servlet").getName());
    }

    /**
     * Test getRunAsRole method.
     */
    @Test
    public void testGetRunAsRole() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration.Dynamic registration = (ServletRegistration.Dynamic) webApp.getServletRegistration("servlet");
        registration.setRunAsRole("role");
        assertNotNull(registration.getRunAsRole());
        assertEquals("role", registration.getRunAsRole());
    }

    /**
     * Test getServletRegistrations method.
     */
    @Test
    public void testGetServletRegistration() {
        webApp.addServlet("servlet", TestServlet.class);
        assertNotNull(webApp.getServletRegistration("servlet"));
    }

    /**
     * Test getFilterRegistration method.
     */
    @Test
    public void testGetServletRegistration2() {
        webApp.addServlet("servlet", "doesnotexist");
        assertNotNull(webApp.getServletRegistration("servlet"));
    }

    /**
     * Test getFilterRegistrations method.
     */
    @Test
    public void testGetServletRegistration3() {
        webApp.addServlet("servlet", TestServlet.class);
        assertFalse(webApp.getServletRegistrations().isEmpty());
    }

    /**
     * Test setInitParameters method.
     */
    @Test
    public void testSetInitParameters() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        registration.setInitParameter("name", "value");
        assertTrue(registration.setInitParameters(new HashMap<>()).isEmpty());
    }

    /**
     * Test setInitParameters method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetInitParameters2() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(null, null);
        registration.setInitParameters(parameters);
    }

    /**
     * Test setInitParameters method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetInitParameters3() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", null);
        registration.setInitParameters(parameters);
    }

    /**
     * Test setInitParameters method.
     */
    @Test
    public void testSetInitParameters4() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", "value");
        assertTrue(registration.setInitParameters(parameters).isEmpty());
    }

    /**
     * Test setInitParameters method.
     */
    @Test
    public void testSetInitParameters5() {
        webApp.addServlet("servlet", TestServlet.class);
        ServletRegistration registration = webApp.getServletRegistration("servlet");
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", "value");
        assertTrue(registration.setInitParameters(parameters).isEmpty());
        assertFalse(registration.setInitParameters(parameters).isEmpty());
    }

    /**
     * Test servlet used in a test which tests adding a servlet registration.
     */
    public static class TestServlet extends HttpServlet {
    }
}
