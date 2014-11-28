/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fSQLScriptsConfiguration.java
 * Package: nl.tranquilizedquality.itest.tomcat.configuration
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
package nl.tranquilizedquality.itest.configuration;

import java.util.Arrays;

import nl.tranquilizedquality.itest.domain.SQLScripts;
import nl.tranquilizedquality.itest.domain.SQLScripts.SQLScriptsBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the SQL scripts that should be executed during a test.
 *
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
@Configuration
public class SQLScriptsConfiguration {

    @Bean
    public SQLScripts sqlScripts() {

        return new SQLScriptsBuilder()
                .withCleanUpScripts(Arrays.asList("test-clean-up.sql"))
                .withSetupScripts(Arrays.asList("test-setup.sql"))
                .build();
    }

}
