/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fTomcatContainerUtilConfiguration.java
 * Package: nl.tranquilizedquality.itest.test.conf
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
package nl.tranquilizedquality.itest.tomcat.configuration;

import java.util.Arrays;

import nl.tranquilizedquality.itest.cargo.AppTomcatContainerUtil;
import nl.tranquilizedquality.itest.cargo.ContainerUtil;
import nl.tranquilizedquality.itest.configuration.AbstractContainerConfiguration;
import nl.tranquilizedquality.itest.domain.DeployableLocationConfiguration;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Tomcat container utility.
 *
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
@Configuration
public class TomcatContainerUtilConfiguration extends AbstractContainerConfiguration {

    @Override
    public ContainerUtil containerUtil() {

        final AppTomcatContainerUtil container = new AppTomcatContainerUtil();
        container.setContainerPort(8890);
        container.setRemoteLocation("http://www.tr-quality.com/cargo-itest/");
        container.setContainerFile("apache-tomcat-7.0.57.zip");
        // container.setRemoteLocation("D:\\Development\\Servers\\apache-tomcat-7.0.57\\");
        container.setRmiPort(8805);
        container.setAjpPort(8809);
        container.setTomcatVersion("tomcat7x");
        container.setCleanUpAfterContainerStopped(true);

        final DeployableLocationConfiguration testApp = new DeployableLocationConfiguration();
        testApp.setContextName("test-app");
        testApp.setPath("src/test/resources/test-app.war");
        testApp.setType("WAR");
        container.setDeployableLocationConfigurations(Arrays.asList(testApp));

        return container;
    }

}
