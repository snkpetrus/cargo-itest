/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fJettyConfiguration.java
 * Package: nl.tranquilizedquality.itest.jetty
 *
 * Copyright (c) 2014 Tranquilized Quality www.tr-quality.com All rights
 * reserved.
 *
 * This software is the confidential and proprietary information of Dizizid
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Tranquilized Quality.
 * </pre>
 */
package nl.tranquilizedquality.itest.jetty.configuration;

import java.util.Arrays;

import nl.tranquilizedquality.itest.cargo.AppJettyContainerUtil;
import nl.tranquilizedquality.itest.cargo.ContainerUtil;
import nl.tranquilizedquality.itest.configuration.AbstractContainerConfiguration;
import nl.tranquilizedquality.itest.domain.DeployableLocationConfiguration;

import org.springframework.context.annotation.Configuration;

/**
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
@Configuration
public class JettyConfiguration extends AbstractContainerConfiguration {

    @Override
    public ContainerUtil containerUtil() {

        final AppJettyContainerUtil container = new AppJettyContainerUtil();
        container.setContainerPort(8090);
        container.setRemoteLocation("http://www.tr-quality.com/cargo-itest/");
        container.setContainerFile("jetty.zip");

        final DeployableLocationConfiguration testApp = new DeployableLocationConfiguration();
        testApp.setContextName("test-app");
        testApp.setPath("src/test/resources/test-app.war");
        testApp.setType("WAR");
        container.setDeployableLocationConfigurations(Arrays.asList(testApp));

        return container;
    }

}
