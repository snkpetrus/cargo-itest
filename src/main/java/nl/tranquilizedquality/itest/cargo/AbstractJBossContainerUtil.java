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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.util.log.FileLogger;
import org.codehaus.cargo.util.log.LogLevel;
import org.codehaus.cargo.util.log.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * This is a base utility class that can be used to use Cargo with an installed
 * installedLocalContainer. It can configure, start and stop the
 * installedLocalContainer. In this case a JBoss instance.
 * 
 * @author Salomo Petrus (sape)
 * @since 11/12/2008
 * 
 */
public abstract class AbstractJBossContainerUtil implements ContainerUtil {
    /** Logger for this class */
    private static final Log log =
            LogFactory.getLog(AbstractJBossContainerUtil.class);

    /**
     * The installedLocalContainer where the server application will be run in.
     */
    protected InstalledLocalContainer installedLocalContainer;

    /** The JVM arguments to use when starting up the installedLocalContainer */
    protected List<String> jvmArguments = new ArrayList<String>();

    /**
     * The path where the installedLocalContainer server is installed.
     */
    protected String containerHome;

    /**
     * The port where the container will run on. Use the property
     * ${cargo.server.port} to set the port dynamically and set the system
     * properties with this value.
     */
    protected Integer containerPort;

    /**
     * The port where the JNP service will run on. This service is used to be
     * able to stop JBoss in a graceful way. Use the property ${cargo.jnp.port}
     * to set the port dynamically and set the system properties with this
     * value. Cargo seems to search for this service on port 1299.
     */
    protected Integer jnpPort;

    /**
     * The path where the Cargo log files will be written to.
     */
    protected String cargoLogFilePath;

    /** The system property that can be set to be used in the JVM. */
    protected Map<String, String> systemProperties;

    /** The URL where the container and configuration ZIP files are. */
    private String remoteLocation;

    /** The ZIP file of the container to use i.e. jboss-4.0.4.GA.zip. */
    private String containerFile;

    /** The ZIP file containing the JBoss configuration. */
    private String containerConfigurationFile;

    /** The name of the JBOSS configuration to use. */
    protected String configurationName;

    /** The deployable locations that will be used in the integration tests. */
    private Map<String, String> deployableLocations;

    /**
     * The deployable location configurations that will be used in the
     * integration tests.
     */
    private List<DeployableLocationConfiguration> deployableLocationConfigurations;

    /**
     * Default constructor that will detect which OS is used to make sure the
     * JBOSS will be downloaded in the correct location.
     */
    public AbstractJBossContainerUtil() {
        String operatingSystem = System.getProperty("os.name");
        if (operatingSystem != null && operatingSystem.startsWith("Windows")) {
            containerHome = "C:/WINDOWS/Temp/jboss/";
        } else {
            containerHome = "/tmp/jboss/";
        }

        if (log.isInfoEnabled()) {
            log.info("Container HOME: " + containerHome);
        }

        systemProperties = new HashMap<String, String>();
        deployableLocations = new LinkedHashMap<String, String>();
        deployableLocationConfigurations =
                new ArrayList<DeployableLocationConfiguration>();
    }

    /**
     * Sets up the configuration needed for the deployable to be able to run
     * correctly.
     * 
     * @throws Exception Is thrown when something went wrong if the
     *         configuration setup fails.
     */
    protected abstract void setupConfiguration() throws Exception;

    public void start() throws Exception {
        setupContainer();

        deploy();
    }

    public void stop() {
        installedLocalContainer.stop();
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
        if (log.isInfoEnabled()) {
            log.info("Cleaning up JBoss...");
        }

        FileUtils.deleteDirectory(new File(containerHome));
        new File(containerHome).mkdir();

        if (log.isInfoEnabled()) {
            log.info("Installing JBoss...");
            log.info("Downloading JBoss & configuration from: "
                    + remoteLocation);
            log.info("Container file: " + containerFile);
            log.info("Container configuration file: "
                    + containerConfigurationFile);
        }
        URL remoteLocation = new URL(this.remoteLocation + containerFile);
        String installDir = containerHome + "..//";
        ZipURLInstaller installer =
                new ZipURLInstaller(remoteLocation, installDir);
        installer.install();

        if (log.isInfoEnabled()) {
            log.info("Installing [" + configurationName + "] configuration...");
        }
        remoteLocation =
                new URL(this.remoteLocation + containerConfigurationFile);
        installDir = containerHome + "server/";
        installer = new ZipURLInstaller(remoteLocation, installDir);
        installer.install();

        systemProperties.put("jboss.server.lib.url:lib", "file:lib/");
        systemProperties.put("cargo.server.port", containerPort.toString());
        systemProperties.put("cargo.jnp.port", jnpPort.toString());

        setupConfiguration();
    }

    /**
     * Deploys the application to the correct
     */
    protected void deploy() {
        // create configuration factory
        final ConfigurationFactory configurationFactory =
                new DefaultConfigurationFactory();

        // create JBoss configuration
        final LocalConfiguration configuration =
                (LocalConfiguration) configurationFactory.createConfiguration(
                        "jboss4x", ContainerType.INSTALLED,
                        ConfigurationType.EXISTING, containerHome + "server/"
                                + configurationName);

        // setup configuration
        final StringBuilder args = new StringBuilder();
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
        Iterator<Entry<String, String>> iterator = entrySet.iterator();

        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
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
        for (DeployableLocationConfiguration config : deployableLocationConfigurations) {
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
                    final File destFile =
                            new File("target/" + contextName + ".war");

                    try {
                        FileUtils.copyFile(srcFile, destFile);
                    } catch (IOException e) {
                        throw new DeployException("Failed to copy WAR file: "
                                + path, e);
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
        installedLocalContainer =
                (InstalledLocalContainer) new DefaultContainerFactory()
                        .createContainer("jboss4x", ContainerType.INSTALLED,
                                configuration);

        // configure installedLocalContainer
        installedLocalContainer.setHome(containerHome);
        final Logger fileLogger =
                new FileLogger(new File(cargoLogFilePath + "cargo.log"), true);
        fileLogger.setLevel(LogLevel.DEBUG);
        installedLocalContainer.setLogger(fileLogger);
        installedLocalContainer.setOutput(cargoLogFilePath + "output.log");

        // set the system properties
        installedLocalContainer.setSystemProperties(systemProperties);

        if (log.isInfoEnabled()) {
            log.info("Starting JBoss [" + configurationName + "]...");
        }

        // startup installedLocalContainer
        installedLocalContainer.start();

        // Here you are assured the container is started.
        if (log.isInfoEnabled()) {
            log.info("JBoss up and running!");
        }
    }

    /**
     * Determines the type of deployable.
     * 
     * @param type A string representation of the deployable type.
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
            deployableType = DeployableType.EAR;
        } else if ("WAR".equals(type)) {
            deployableType = DeployableType.WAR;
        } else if ("EJB".equals(type)) {
            deployableType = DeployableType.EJB;
        } else {
            // Default value is EAR file
            deployableType = DeployableType.EAR;
        }

        return deployableType;
    }

    /**
     * Adds a deployable to the {@link LocalConfiguration}.
     * 
     * @param configuration The configuration where a deployable can be added
     *        to.
     * @param path The path where the deployable can be found.
     * @param deployableType The type of deployable.
     */
    private void addDeployable(LocalConfiguration configuration, String path,
            DeployableType deployableType) {
        // retrieve deployable file
        Deployable deployable =
                new DefaultDeployableFactory().createDeployable(
                        configurationName, path, deployableType);

        // add deployable
        configuration.addDeployable(deployable);
    }

    /**
     * Retrieves the JVM arguments
     * 
     * @return Returns a unmodifiable list containing the current JVM arguments.
     */
    public List<String> getJvmArguments() {
        return Collections.unmodifiableList(jvmArguments);
    }

    /**
     * @param jvmArguments the jvmArguments to set
     */
    @Required
    public void setJvmArguments(List<String> jvmArguments) {
        this.jvmArguments = new ArrayList<String>(jvmArguments);
    }

    @Required
    public void setCargoLogFilePath(String cargoLogFilePath) {
        this.cargoLogFilePath = cargoLogFilePath;
    }

    /**
     * @param containerPort the containerPort to set
     */
    @Required
    public void setContainerPort(Integer containerPort) {
        this.containerPort = containerPort;
    }

    /**
     * @param remoteLocation the remoteLocation to set
     */
    @Required
    public void setRemoteLocation(String remoteLocation) {
        this.remoteLocation = remoteLocation;
    }

    /**
     * @param containerFile the containerFile to set
     */
    @Required
    public void setContainerFile(String containerFile) {
        this.containerFile = containerFile;
    }

    /**
     * @param containerConfigurationFile the containerConfigurationFile to set
     */
    @Required
    public void setContainerConfigurationFile(String containerConfigurationFile) {
        this.containerConfigurationFile = containerConfigurationFile;
    }

    /**
     * @param systemProperties the systemProperties to set
     */
    public void setSystemProperties(Map<String, String> systemProperties) {
        this.systemProperties = systemProperties;
    }

    /**
     * @param configurationName the configurationName to set
     */
    @Required
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * @param deployableLocations the deployableLocations to set
     */
    public void setDeployableLocations(Map<String, String> deployableLocations) {
        this.deployableLocations = deployableLocations;
    }

    /**
     * @param locations the deployable configuration locations that will be set.
     */
    public void setDeployableLocationConfigurations(
            List<DeployableLocationConfiguration> deployableLocationConfigurations) {
        this.deployableLocationConfigurations =
                deployableLocationConfigurations;
    }

    public void addDeployableLocation(final String location, final String type) {
        this.deployableLocations.put(type, location);
    }

    public Integer getContainerPort() {
        return containerPort;
    }

    /**
     * @param jnpPort the jnpPort to set
     */
    @Required
    public void setJnpPort(Integer jnpPort) {
        this.jnpPort = jnpPort;
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
        fullPath.append("server/");
        fullPath.append(this.configurationName);
        fullPath.append("/");
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
