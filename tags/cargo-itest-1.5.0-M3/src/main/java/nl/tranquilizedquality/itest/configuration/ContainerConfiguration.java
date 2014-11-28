/**
 * <pre>
 * Project: cargo-itest Created on: 24 nov. 2014 File: fContainerConfiguration.java
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

import nl.tranquilizedquality.itest.cargo.ContainerUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 24 nov. 2014
 *
 */
@Configuration
public interface ContainerConfiguration {

    /**
     * The container utility that will start up the application container.
     *
     * @return Returns a {@link ContainerUtil} bean.
     */
    @Bean
    public ContainerUtil containerUtil();

}
