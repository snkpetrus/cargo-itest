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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.tranquilizedquality.itest.cargo.exception.ConfigurationException;
import nl.tranquilizedquality.itest.domain.DeployableLocationConfiguration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.springframework.beans.factory.annotation.Required;

/**
 * Base container class where every container should extend from. It contains
 * all the basic stuff which every container utility should have like cleaning
 * up the container before doing a fresh run.
 * 
 * @author Salomo Petrus (sape)
 * @since 23 apr 2009
 * 
 */
public abstract class AbstractInstalledContainerUtil implements ContainerUtil {

    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(AbstractInstalledContainerUtil.class);

    /** The name of the container. */
    private String containerName;

    /** The path where all configuration resource files are */
    protected String configResourcesPath;

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
     * The path where the Cargo LOGGER files will be written to.
     */
    protected String cargoLogFilePath;

    /** The system property that can be set to be used in the JVM. */
    protected Map<String, String> systemProperties;

    /** The URL where the container and configuration ZIP files are. */
    protected String remoteLocation;

    /** The ZIP file of the container to use i.e. jboss-4.0.4.GA.zip. */
    protected String containerFile;

    /** The deployable locations that will be used in the integration tests. */
    protected Map<String, String> deployableLocations;

    /**
     * The deployable location configurations that will be used in the
     * integration tests.
     */
    protected List<DeployableLocationConfiguration> deployableLocationConfigurations;

    /**
     * Determines if the extracted container in the temporary directory of the
     * OS should be cleaned up after the container was stopped.
     */
    private boolean cleanUpAfterContainerStopped = true;

    /** The root folder where the container will be extracted and started. */
    private String containerRootFolderName;

    /**
     * Default constructor.
     */
    public AbstractInstalledContainerUtil() {
        configResourcesPath = "src/test/resources/";

        systemProperties = new HashMap<String, String>();
        deployableLocations = new LinkedHashMap<String, String>();
        deployableLocationConfigurations = new ArrayList<DeployableLocationConfiguration>();
    }

    /**
     * Cleans up the container if there is any to save disk space.
     */
    private void cleanUpContainer() {

        /*
         * Delete container directory.
         */
        try {
            FileUtils.deleteDirectory(new File(containerRootFolderName));
        } catch (final Exception exceptionOnDelete) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Failed to delete the directory: " + containerHome + ".", exceptionOnDelete);
            }
        }
    }

    /**
     * Sets up the container home directory so the container can be extracted
     * and started there.
     */
    protected void setupContainerHome() {
        /*
         * Retrieve the operating system and current logged in user to create a
         * unique storage location for the container to start up in. This way
         * clashing of containers will be kept to the minimum.
         */
        final String operatingSystem = System.getProperty("os.name");
        final StringBuilder builder = new StringBuilder();
        if (operatingSystem != null && operatingSystem.startsWith("Windows")) {
            builder.append("C:/WINDOWS/Temp/");
        } else {
            builder.append("/tmp/");
        }

        /*
         * Get the time in millis so concurrent builds using the cargo-itest
         * utility can be executed without clashing with eachother.
         */
        final Long timeStamp = Calendar.getInstance().getTimeInMillis();
        builder.append(timeStamp);
        builder.append("/");
        containerRootFolderName = builder.toString();
        builder.append(containerName);
        builder.append("/");
        containerHome = builder.toString();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Container HOME: " + containerHome);
        }
    }

    /**
     * Sets up the configuration needed for the deployable to be able to run
     * correctly.
     * 
     * @throws Exception
     *             Is thrown when something went wrong if the configuration
     *             setup fails.
     */
    protected abstract void setupConfiguration() throws Exception;

    /**
     * Installs the container and the application configuration. It also sets
     * some system properties so the container can startup properly. Finally it
     * sets up additional configuration like jndi.proprties files etc.
     * 
     * @throws Exception
     *             Is thrown when something goes wrong during the setup of the
     *             container.
     */
    protected void setupContainer() throws Exception {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Cleaning up " + containerName + "...");
        }

        // In windows the renaming causes problem when:
        // - The zip file has not the same name of the installed directory.
        // - The ZipURLInstaller fails.
        final String operatingSystem = System.getProperty("os.name");
        if (operatingSystem != null && !operatingSystem.startsWith("Windows")) {

            try {
                new File(containerHome).mkdir();
            } catch (final Exception exceptionOnMkDir) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(
                            "Failed to create the directory: " + containerHome + ". Details: " + exceptionOnMkDir.getMessage(),
                            exceptionOnMkDir);
                }
                throw new ConfigurationException("Failed to create the directory: " + containerHome + ". Details: "
                        + exceptionOnMkDir.getMessage(), exceptionOnMkDir);
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Installing " + containerName + "...");
            LOGGER.info("Downloading container from: " + remoteLocation);
            LOGGER.info("Container file: " + containerFile);
        }

        /*
         * Download and configure the container.
         */
        final URL remoteLocation = new URL(this.remoteLocation + containerFile);
        final String installDir = StringUtils.substringBeforeLast(StringUtils.chomp(containerHome, "/"), "/");
        final ZipURLInstaller installer = new ZipURLInstaller(remoteLocation, installDir, installDir);
        installer.install();

        /*
         * Rename the install directory to the container home directory so it
         * doesn't matter what the name is of the zip file and avoid case
         * sensitive issues on Linux.
         */
        final String containerDir = StringUtils.stripEnd(containerFile, ".zip");
        final File installedDir = new File(installDir + "/" + containerDir + "/");
        final File destenationDir = new File(containerHome);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Renaming: " + installedDir.getPath());
            LOGGER.info("To: " + destenationDir.getPath());
        }

        final boolean renamed = installedDir.renameTo(destenationDir);

        if (!renamed) {
            final String msg = "Failed to rename container install directory to home directory name!";
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(msg);
            }

            throw new ConfigurationException(msg);
        }

        /*
         * Setup the system properties.
         */
        systemProperties.put("cargo.server.port", containerPort.toString());

    }

    /**
     * Deploys the application to the correct
     */
    protected abstract void deploy();

    public void start() throws Exception {
        setupContainer();

        deploy();
    }

    public void stop() {
        installedLocalContainer.stop();

        if (cleanUpAfterContainerStopped) {
            cleanUpContainer();
        }
    }

    /**
     * @param configResourcesPath
     *            the configResourcesPath to set
     */
    public void setConfigResourcesPath(final String configResourcesPath) {
        this.configResourcesPath = configResourcesPath;
    }

    /**
     * @return the configResourcesPath
     */
    public String getConfigResourcesPath() {
        return configResourcesPath;
    }

    /**
     * @param deployableLocations
     *            the deployableLocations to set
     */
    public void setDeployableLocations(final Map<String, String> deployableLocations) {
        this.deployableLocations = deployableLocations;
    }

    /**
     * @param locations
     *            the deployable configuration locations that will be set.
     */
    public void setDeployableLocationConfigurations(final List<DeployableLocationConfiguration> deployableLocationConfigurations) {
        this.deployableLocationConfigurations = deployableLocationConfigurations;
    }

    public void addDeployableLocation(final String location, final String type) {
        this.deployableLocations.put(type, location);
    }

    public Integer getContainerPort() {
        return containerPort;
    }

    /**
     * @param systemProperties
     *            the systemProperties to set
     */
    public void setSystemProperties(final Map<String, String> systemProperties) {
        this.systemProperties = systemProperties;
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
     * @param jvmArguments
     *            the jvmArguments to set
     */
    @Required
    public void setJvmArguments(final List<String> jvmArguments) {
        this.jvmArguments = new ArrayList<String>(jvmArguments);
    }

    @Required
    public void setCargoLogFilePath(final String cargoLogFilePath) {
        this.cargoLogFilePath = cargoLogFilePath;
    }

    /**
     * @param containerPort
     *            the containerPort to set
     */
    @Required
    public void setContainerPort(final Integer containerPort) {
        this.containerPort = containerPort;
    }

    /**
     * @param remoteLocation
     *            the remoteLocation to set
     */
    @Required
    public void setRemoteLocation(final String remoteLocation) {
        this.remoteLocation = remoteLocation;
    }

    /**
     * @param containerFile
     *            the containerFile to set
     */
    @Required
    public void setContainerFile(final String containerFile) {
        this.containerFile = containerFile;
    }

    public void setCleanUpAfterContainerStopped(final boolean cleanUpAfterContainerStopped) {
        this.cleanUpAfterContainerStopped = cleanUpAfterContainerStopped;
    }

    /**
     * @param containerName
     *            the containerName to set
     */
    protected void setContainerName(final String containerName) {
        this.containerName = containerName;
    }

}
