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
package nl.tranquilizedquality.itest.cargo;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nl.tranquilizedquality.itest.cargo.exception.ConfigurationException;
import nl.tranquilizedquality.itest.cargo.exception.DeployException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.util.log.FileLogger;
import org.codehaus.cargo.util.log.LogLevel;
import org.codehaus.cargo.util.log.Logger;

/**
 * AbstractTomcatContainerUtil is an implementation of {@link ContainerUtil}
 * which managaes a Tomcat servlet container. It can configure, start and stop
 * the Tomcat servlet container.
 * 
 * @author Salomo Petrus
 * 
 */
public abstract class AbstractTomcatContainerUtil extends
        AbstractInstalledContainerUtil {

    /** Logger for this class */
    private static final Log log =
            LogFactory.getLog(AbstractTomcatContainerUtil.class);

    /**
     * Default constructor that will detect which OS is used to make sure the
     * Tomcat will be downloaded in the correct location.
     */
    public AbstractTomcatContainerUtil() {
        setContainerName("Tomcat");

        cleanUpContainer();
    }

    /**
     * Installs the container and the application configuration. It also sets
     * some system properties so the container can startup properly. Finally it
     * sets up additional configuration like jndi.proprties files etc.
     * 
     * @throws Exception Is thrown when something goes wrong during the setup of
     *         the container.
     */
    protected void setupContainer() throws Exception {
        /*
         * Execute default setup behavior.
         */
        super.setupContainer();

        setupConfiguration();
    }

    /**
     * Deploys the application to the correct
     */
    protected void deploy() {
        // create configuration factory
        ConfigurationFactory configurationFactory =
                new DefaultConfigurationFactory();

        // create JBoss configuration
        LocalConfiguration configuration =
                (LocalConfiguration) configurationFactory.createConfiguration(
                        "tomcat5x", ContainerType.INSTALLED,
                        ConfigurationType.EXISTING, containerHome);

        // setup configuration
        StringBuilder args = new StringBuilder();
        for (String arg : jvmArguments) {
            args.append(arg);
            args.append(" ");
        }
        configuration.setProperty(GeneralPropertySet.JVMARGS, args.toString());
        configuration.setProperty(ServletPropertySet.PORT, containerPort
                .toString());

        /*
         * Iterate over all available deployable locations.
         */
        Set<Entry<String, String>> entrySet = deployableLocations.entrySet();
        for (Entry<String, String> entry : entrySet) {
            String value = entry.getValue();
            DeployableType deployableType = null;

            /*
             * Check what kind of deployable it is.
             */
            if (value.equals("EAR")) {
                if(log.isWarnEnabled()) {
                    log.warn("Tomcat doesn't suport EAR files.");
                }
                throw new DeployException("Tomcat doesn't support EAR files.");
            } else if (value.equals("WAR")) {
                deployableType = DeployableType.WAR;
            } else if (value.equals("EJB")) {
                if(log.isWarnEnabled()) {
                    log.warn("Tomcat doesn't suport EJB files.");
                }
                throw new DeployException("Tomcat doesn't support EJB files.");
            } else {
                // Default value is WAR file
                deployableType = DeployableType.WAR;
            }

            // retrieve deployable file
            Deployable deployable =
                    new DefaultDeployableFactory().createDeployable("tomcat5x",
                            entry.getKey(), deployableType);

            // add deployable
            configuration.addDeployable(deployable);
        }

        // create installedLocalContainer
        installedLocalContainer =
                (InstalledLocalContainer) new DefaultContainerFactory()
                        .createContainer("tomcat5x", ContainerType.INSTALLED,
                                configuration);

        // configure installedLocalContainer
        installedLocalContainer.setHome(containerHome);
        Logger fileLogger =
                new FileLogger(new File(cargoLogFilePath + "cargo.log"), true);
        fileLogger.setLevel(LogLevel.DEBUG);
        installedLocalContainer.setLogger(fileLogger);
        installedLocalContainer.setOutput(cargoLogFilePath + "output.log");

        // set the system properties
        installedLocalContainer.setSystemProperties(systemProperties);

        if (log.isInfoEnabled()) {
            log.info("Starting Tomcat ...");
        }

        // startup installedLocalContainer
        installedLocalContainer.start();

        // Here you are assured the container is started.
        if (log.isInfoEnabled()) {
            log.info("Tomcat up and running!");
        }
    }

    /**
     * @return the deployableLocations
     */
    public Map<String, String> getDeployableLocations() {
        return deployableLocations;
    }

    /**
     * Constructs the full path to a specific directory from the configuration.
     * 
     * @param dir The directory name.
     * @return Returns a String representation of the full path.
     */
    private String getContainerDirectory(final String dir) {
        final StringBuilder fullPath = new StringBuilder();
        fullPath.append(this.containerHome);
        fullPath.append(dir);

        final String path = fullPath.toString();

        final File directory = new File(path);
        if (!directory.exists()) {
            final String msg = dir + " directory does not excist! : " + path;
            if (log.isErrorEnabled()) {
                log.error(msg);
            }

            throw new ConfigurationException(msg);
        }

        return path;
    }

    public String getSharedLibDirectory() {
        return getContainerDirectory("lib/");
    }

    public String getConfDirectory() {
        return getContainerDirectory("conf/");
    }
}
