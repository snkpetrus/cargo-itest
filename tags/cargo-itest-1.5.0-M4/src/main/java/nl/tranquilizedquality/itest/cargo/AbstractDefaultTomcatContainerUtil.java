/**
 * <pre>
 * Project: cargo-itest Created on: 24 nov. 2014 File: fAbstractDefaultTomcatContainer.java
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
package nl.tranquilizedquality.itest.cargo;

import java.util.Arrays;

/**
 * Base class for a default Tomcat container.
 *
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 24 nov. 2014
 *
 */
public abstract class AbstractDefaultTomcatContainerUtil extends AbstractTomcatContainerUtil {

    /**
     * Default constructor setting the default values for a tomcat container
     * utility.
     */
    public AbstractDefaultTomcatContainerUtil() {
        setCargoLogFilePath("target/");
        setContainerPort(8890);
        setJvmArguments(Arrays.asList("-Xms128m", "-Xmx512m", "-XX:PermSize=128m"));
    }

}
