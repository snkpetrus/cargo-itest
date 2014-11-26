/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fDatasourceConfiguration.java
 * Package: nl.tranquilizedquality.itest.configuration
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
@Configuration
@PropertySource("classpath:itest-db.properties")
public class DatasourceConfiguration {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean(name = "dataSource")
    public SingleConnectionDataSource singleConnectionDataSource() {

        final SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        singleConnectionDataSource.setDriverClassName(driverClassName);
        singleConnectionDataSource.setUrl(url);
        singleConnectionDataSource.setUsername(username);
        singleConnectionDataSource.setPassword(password);
        singleConnectionDataSource.setSuppressClose(true);
        return singleConnectionDataSource;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
