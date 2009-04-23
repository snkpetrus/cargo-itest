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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nl.tranquilizedquality.itest.cargo.exception.ConfigurationException;
import nl.tranquilizedquality.itest.cargo.exception.DeployException;

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
 * AbstractTomcatContainerUtil is an implementation of {@link ContainerUtil}
 * which managaes a Tomcat servlet container. It can configure, start and stop
 * the Tomcat servlet container.
 * 
 * @author Salomo Petrus
 * 
 */
public abstract class AbstractTomcatContainerUtil implements ContainerUtil {

    /** Logger for this class */
    private static final Log log =
            LogFactory.getLog(AbstractTomcatContainerUtil.class);

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
     * The path where the Cargo log files will be written to.
     */
    protected String cargoLogFilePath;

    /** The system property that can be set to be used in the JVM. */
    protected Map<String, String> systemProperties;

    /** The URL where the container and configuration ZIP files are. */
    private String remoteLocation;

    /** The ZIP file of the container to use i.e. tomcat-6.zip. */
    private String containerFile;

    /** The deployable locations that will be used in the integration tests. */
    private Map<String, String> deployableLocations;

    /** The name of the Tomcat configuration to use. */
    protected String configurationName;

    /**
     * Default constructor that will detect which OS is used to make sure the
     * Tomcat will be downloaded in the correct location.
     */
    public AbstractTomcatContainerUtil() {
        String operatingSystem = System.getProperty("os.name");
        if (operatingSystem != null && operatingSystem.startsWith("Windows")) {
            containerHome = "C:/WINDOWS/Temp/tomcat/";
        } else {
            containerHome = "/tmp/tomcat/";
        }

        if (log.isInfoEnabled()) {
            log.info("Container HOME: " + containerHome);
        }

        systemProperties = new HashMap<String, String>();
        deployableLocations = new HashMap<String, String>();
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
            log.info("Cleaning up Tomcat...");
        }

        FileUtils.deleteDirectory(new File(containerHome));
        new File(containerHome).mkdir();

        if (log.isInfoEnabled()) {
            log.info("Installing Tomcat...");
            log.info("Downloading Tomcat from: " + remoteLocation);
            log.info("Container file: " + containerFile);
        }
        URL remoteLocation = new URL(this.remoteLocation + containerFile);
        String installDir = containerHome + "..//";
        ZipURLInstaller installer =
                new ZipURLInstaller(remoteLocation, installDir);
        installer.install();

        systemProperties.put("cargo.server.port", containerPort.toString());

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
                throw new DeployException("Tomcat doesn't support EAR files.");
            } else if (value.equals("WAR")) {
                deployableType = DeployableType.WAR;
            } else if (value.equals("EJB")) {
                throw new DeployException("Tomcat doesn't support EAR files.");
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
     * @param configurationName the configurationName to set
     */
    @Required
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * @param systemProperties the systemProperties to set
     */
    public void setSystemProperties(Map<String, String> systemProperties) {
        this.systemProperties = systemProperties;
    }

    /**
     * @return the deployableLocations
     */
    public Map<String, String> getDeployableLocations() {
        return deployableLocations;
    }

    /**
     * @param deployableLocations the deployableLocations to set
     */
    @Required
    public void setDeployableLocations(Map<String, String> deployableLocations) {
        this.deployableLocations = deployableLocations;
    }

    public void addDeployableLocation(final String location, final String type) {
        this.deployableLocations.put(type, location);
    }

    public Integer getContainerPort() {
        return containerPort;
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
