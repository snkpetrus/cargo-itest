/**
 * <pre>
 * Project: cargo-itest Created on: 24 nov. 2014 File: fAbstractContainerConfiguration.java
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
 * Base class for the container configuration.
 *
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 24 nov. 2014
 *
 */
@Configuration
public abstract class AbstractContainerConfiguration implements ContainerConfiguration {

    @Bean(name = "containerUtil")
    public abstract ContainerUtil containerUtil();

}
