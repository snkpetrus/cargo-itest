/*
 * Copyright 2009 Salomo Petrus
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package nl.tranquilizedquality.itest.jboss;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import nl.tranquilizedquality.itest.AbstractDefaultDeploymentTest;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is a simple example how you could create an integration test with the
 * {@link AbstractDefaultDeploymentTest}.
 * 
 * As you can see you only need to extend from
 * {@link AbstractDefaultDeploymentTest} and your application will be started up
 * in JBoss for you with your customized container utility.
 * 
 * @author Salomo Petrus (sape)
 * @since 11 dec 2008
 * 
 */
@RunWith(JUnit4ClassRunner.class)
public class TestAppJBossDeploymentTest extends AbstractDefaultDeploymentTest {

	@Test
	public void testHelloWorld() throws Exception {
		final WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(false);

		// Get the first page
		final HtmlPage index = (HtmlPage) webClient.getPage("http://" + host + "/test-app/");

		assertNotNull(index);
		assertTrue(StringUtils.contains(index.asText(), "hello INDEX"));
	}
}
