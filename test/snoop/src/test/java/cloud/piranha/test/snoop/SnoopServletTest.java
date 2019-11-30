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
package cloud.piranha.test.snoop;

import cloud.piranha.embedded.EmbeddedPiranha;
import cloud.piranha.embedded.EmbeddedPiranhaBuilder;
import cloud.piranha.embedded.EmbeddedRequest;
import cloud.piranha.embedded.EmbeddedRequestBuilder;
import cloud.piranha.embedded.EmbeddedResponse;
import cloud.piranha.embedded.EmbeddedResponseBuilder;
import javax.servlet.http.Cookie;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * The JUnit tests for the SnoopServlet class.
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class SnoopServletTest {

    /**
     * Test GET method.
     *
     * @throws Exception when a serious error occurs.
     */
    @Test
    public void testGetMethod() throws Exception {
        EmbeddedPiranha piranha = new EmbeddedPiranhaBuilder()
                .servletMapping("Snoop", "/Snoop")
                .servlet("Snoop", SnoopServlet.class.getName())
                .build()
                .initialize()
                .start();
        EmbeddedRequest request = new EmbeddedRequestBuilder()
                .servletPath("/Snoop")
                .build();
        EmbeddedResponse response = new EmbeddedResponseBuilder()
                .build();
        piranha.service(request, response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getResponseAsString().contains("Snoop"));
        piranha.stop()
                .destroy();
    }

    /**
     * Test GET method.
     *
     * @throws Exception when a serious error occurs.
     */
    @Test
    public void testGetMethod2() throws Exception {
        EmbeddedPiranha piranha = new EmbeddedPiranhaBuilder()
                .servletMapping("Snoop", "/Snoop")
                .servlet("Snoop", SnoopServlet.class.getName())
                .build()
                .initialize()
                .start();
        EmbeddedRequest request = new EmbeddedRequestBuilder()
                .servletPath("/Snoop")
                .attribute("Snoop", "Snoop")
                .parameter("Snoop", "Snoop")
                .cookie(new Cookie("COOKIE", "COOKIE"))
                .build();
        EmbeddedResponse response = new EmbeddedResponse();
        piranha.service(request, response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getResponseAsString().contains("Snoop"));
        piranha.stop()
                .destroy();
    }

    /**
     * Test POST method.
     *
     * @throws Exception when a serious error occurs.
     */
    @Test
    public void testPostMethod() throws Exception {
        EmbeddedPiranha piranha = new EmbeddedPiranhaBuilder()
                .servletMapping("Snoop", "/Snoop")
                .servlet("Snoop", SnoopServlet.class.getName())
                .build()
                .initialize()
                .start();
        EmbeddedRequest request = new EmbeddedRequestBuilder()
                .method("POST")
                .servletPath("/Snoop")
                .attribute("Snoop", "Snoop")
                .parameter("Snoop", "Snoop")
                .cookie(new Cookie("COOKIE", "COOKIE"))
                .build();
        EmbeddedResponse response = new EmbeddedResponse();
        piranha.service(request, response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getResponseAsString().contains("Snoop"));
        piranha.stop()
                .destroy();
    }
}