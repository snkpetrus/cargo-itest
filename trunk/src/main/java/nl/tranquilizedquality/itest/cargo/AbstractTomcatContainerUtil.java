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
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.tranquilizedquality.itest.cargo.exception.ConfigurationException;
import nl.tranquilizedquality.itest.cargo.exception.DeployException;
import nl.tranquilizedquality.itest.domain.DeployableLocationConfiguration;

import org.apache.commons.io.FileUtils;
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
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.util.log.FileLogger;
import org.codehaus.cargo.util.log.LogLevel;
import org.codehaus.cargo.util.log.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * AbstractTomcatContainerUtil is an implementation of {@link ContainerUtil}
 * which managaes a Tomcat servlet container. It can configure, start and stop
 * the Tomcat servlet container.
 * 
 * @author Salomo Petrus
 * @author Enric Ballo
 * 
 */
public abstract class AbstractTomcatContainerUtil extends AbstractInstalledContainerUtil {

    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(AbstractTomcatContainerUtil.class);

    /**
     * The AJP (Apache JServ Protocol) port may be used by a web server (such as
     * the Apache httpd server) to communicate with Tomcat. This port is also
     * used if you set up a load-balanced server. Use the property
     * ${cargo.server.ajp.port} to set the port dynamically and set the system
     * properties with this value.
     * 
     * Default value for Tomcat: 8009
     */
    protected Integer ajpPort;

    /**
     * The port to use when communicating with this server, for example to start
     * and stop it
     * 
     * Default value for Tomcat: 8005
     */
    protected Integer rmiPort;

    protected String tomcatVersion;

    /**
     * Default constructor that will detect which OS is used to make sure the
     * Tomcat will be downloaded in the correct location.
     */
    public AbstractTomcatContainerUtil() {
        setContainerName("Tomcat");

        setupContainerHome();
    }

    /**
     * Installs the container and the application configuration. It also sets
     * some system properties so the container can startup properly. Finally it
     * sets up additional configuration like jndi.proprties files etc.
     * 
     * @throws Exception
     *             Is thrown when something goes wrong during the setup of the
     *             container.
     */
    @Override
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
    @Override
    protected void deploy() {
        // create configuration factory
        final ConfigurationFactory configurationFactory = new DefaultConfigurationFactory();

        // create JBoss configuration
        final LocalConfiguration configuration = (LocalConfiguration) configurationFactory.createConfiguration(tomcatVersion, ContainerType.INSTALLED, ConfigurationType.EXISTING, containerHome);

        // setup configuration
        final StringBuilder args = new StringBuilder();
        for (final String arg : jvmArguments) {
            args.append(arg);
            args.append(" ");

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Added JVM argument: " + arg);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("== CONFIGURATION PROPERTIES ==");
            LOGGER.debug("CONTAINER PORT : " + containerPort);
            LOGGER.debug("AJP PORT       : " + ajpPort);
            LOGGER.debug("RMI PORT       : " + rmiPort);
        }

        configuration.setProperty(GeneralPropertySet.JVMARGS, args.toString());
        configuration.setProperty(ServletPropertySet.PORT, containerPort.toString());
        configuration.setProperty(TomcatPropertySet.AJP_PORT, ajpPort.toString());
        configuration.setProperty(GeneralPropertySet.RMI_PORT, rmiPort.toString());

        systemProperties.put(TomcatPropertySet.AJP_PORT, ajpPort.toString());
        systemProperties.put(GeneralPropertySet.RMI_PORT, rmiPort.toString());

        /*
         * Iterate over all available deployable locations.
         */
        final Set<Entry<String, String>> entrySet = deployableLocations.entrySet();
        final Iterator<Entry<String, String>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            final Entry<String, String> entry = iterator.next();
            final String key = entry.getKey();
            final String value = entry.getValue();
            DeployableType deployableType = null;

            /*
             * Determine the deployable type.
             */
            deployableType = determineDeployableType(value);

            /*
             * Add the deployable.
             */
            addDeployable(configuration, key, deployableType);
        }

        /*
         * Iterate over all available deployable location configurations.
         */
        for (final DeployableLocationConfiguration config : deployableLocationConfigurations) {
            final String contextName = config.getContextName();
            final String type = config.getType();
            String path = config.getPath();

            /*
             * Determine deployable type.
             */
            DeployableType deployableType = null;
            if (contextName != null && contextName.length() > 0) {
                deployableType = determineDeployableType(type);
                if (DeployableType.WAR.equals(deployableType)) {
                    final File srcFile = new File(path);
                    final File destFile = new File("target/" + contextName + ".war");
                    try {
                        FileUtils.copyFile(srcFile, destFile);
                    }
                    catch (final IOException e) {
                        throw new DeployException("Failed to copy WAR file: " + path, e);
                    }

                    try {
                        FileUtils.copyFile(srcFile, destFile);
                    }
                    catch (final IOException e) {
                        throw new DeployException("Failed to copy WAR file: " + path, e);
                    }
                    path = destFile.getPath();
                }
            } else {
                deployableType = determineDeployableType(type);
            }

            /*
             * Add the deployable
             */
            addDeployable(configuration, path, deployableType);
        }

        // create installedLocalContainer
        installedLocalContainer = (InstalledLocalContainer) new DefaultContainerFactory().createContainer("tomcat5x", ContainerType.INSTALLED, configuration);

        // create installedLocalContainer
        installedLocalContainer = (InstalledLocalContainer) new DefaultContainerFactory().createContainer(tomcatVersion, ContainerType.INSTALLED, configuration);
        // configure installedLocalContainer
        installedLocalContainer.setHome(containerHome);
        final Logger fileLogger = new FileLogger(new File(cargoLogFilePath + "cargo.log"), true);
        fileLogger.setLevel(LogLevel.DEBUG);
        installedLocalContainer.setLogger(fileLogger);
        installedLocalContainer.setOutput(cargoLogFilePath + "output.log");

        // set the system properties
        installedLocalContainer.setSystemProperties(systemProperties);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting Tomcat ...");
        }

        // startup installedLocalContainer
        installedLocalContainer.start();

        // Here you are assured the container is started.
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Tomcat up and running!");
        }
    }

    /**
     * @return the deployableLocations
     */
    public Map<String, String> getDeployableLocations() {
        return deployableLocations;
    }

    /**
     * Determines the type of deployable.
     * 
     * @param type
     *            A string representation of the deployable type.
     * @return Returns a {@link DeployableType} that corresponds to the string
     *         representation or if none could be found the default value (EAR)
     *         will be returned.
     */
    private DeployableType determineDeployableType(final String type) {
        DeployableType deployableType;
        /*
         * Check what kind of deployable it is.
         */
        if ("EAR".equals(type)) {
            throw new DeployException("Tomcat doesn't support EAR files!");
        } else if ("WAR".equals(type)) {
            deployableType = DeployableType.WAR;
        } else if ("EJB".equals(type)) {
            throw new DeployException("Tomcat doesn't support EJB files!");
        } else {
            // Default value is WAR file
            deployableType = DeployableType.WAR;
        }

        return deployableType;
    }

    /**
     * Adds a deployable to the {@link LocalConfiguration}.
     * 
     * @param configuration
     *            The configuration where a deployable can be added to.
     * @param path
     *            The path where the deployable can be found.
     * @param deployableType
     *            The type of deployable.
     */
    private void addDeployable(final LocalConfiguration configuration, final String path, final DeployableType deployableType) {
        // retrieve deployable file
        final Deployable deployable = new DefaultDeployableFactory().createDeployable("jetty", path, deployableType);

        // add deployable
        configuration.addDeployable(deployable);
    }

    /**
     * Constructs the full path to a specific directory from the configuration.
     * 
     * @param dir
     *            The directory name.
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
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(msg);
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

    public void setTomcatVersion(final String tomcatVersion) {
        this.tomcatVersion = tomcatVersion;
    }

    public Integer getAjpPort() {
        return ajpPort;
    }

    @Required
    public void setAjpPort(final Integer ajpPort) {
        this.ajpPort = ajpPort;
    }

    public Integer getRmiPort() {
        return rmiPort;
    }

    @Required
    public void setRmiPort(final Integer rmiPort) {
        this.rmiPort = rmiPort;
    }

}
