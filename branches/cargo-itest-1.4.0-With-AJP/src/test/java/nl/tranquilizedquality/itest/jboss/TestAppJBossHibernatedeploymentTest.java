/**
 * Project: cargo-itest Created on: 13 feb 2009 File:
 * TestAppJBossHibernatedeploymentTest.java Package:
 * nl.tranquilizedquality.itest.jboss
 * 
 * Copyright (c) 2009 Tranquilized Quality www.tq-quality.nl All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of Tranquilized
 * Quality ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Tranquilized Quality.
 */
package nl.tranquilizedquality.itest.jboss;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import nl.tranquilizedquality.itest.AbstractDefaultHibernateDeploymentTest;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This is a simple example how you could create an integration test with the
 * {@link AbstractDefaultHibernateDeploymentTest}.
 * 
 * As you can see you only need to extend from
 * {@link AbstractDefaultHibernateDeploymentTest} and your application will be
 * started up in JBoss for you with your customized container utility. Also the
 * annotated classes context file will be used to generate your database schema.
 * 
 * @author Salomo Petrus
 * @since 13-2-2009
 * 
 */
public class TestAppJBossHibernatedeploymentTest extends AbstractDefaultHibernateDeploymentTest {

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
