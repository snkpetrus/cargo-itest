/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fCommonDBConfiguration.java
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
@Configuration
public class CommonDBConfiguration extends DatasourceConfiguration {

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(final SingleConnectionDataSource dataSource) {

        return new DataSourceTransactionManager(dataSource);
    }

}
