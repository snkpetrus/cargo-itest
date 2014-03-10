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
import java.util.Iterator;
import java.util.List;
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
public abstract class AbstractJBossContainerUtil extends AbstractInstalledContainerUtil {
    /** Logger for this class */
    private static final Log LOGGER = LogFactory.getLog(AbstractJBossContainerUtil.class);

    /**
     * The suffix of properties files that will be picked up when they are
     * located in the configuration resource directory which by default is
     * src/test/resources/.
     */
    private static final String PROPERTIES_FILES_SUFFIX = ".properties";

    /**
     * The JBoss log4j XML file for logging configuration that will be picked up
     * automatically by this container utility if it exists in the configuration
     * resource directory which by default is src/test/resources. So if there is
     * a log4j.xml file in there this will be used.
     */
    private static final String LOG4J_XML = "log4j.xml";

    /**
     * The suffix of JBoss data source files that will be picked up when they
     * are located in the configuration resource directory which by default is
     * src/test/resources/.
     */
    private static final String DATA_SOURCE_FILES_SUFFIX = "-ds.xml";

    /**
     * The port where the JNP service will run on. This service is used to be
     * able to stop JBoss in a graceful way. Use the property ${cargo.jnp.port}
     * to set the port dynamically and set the system properties with this
     * value. Cargo seems to search for this service on port 1299.
     */
    private Integer jnpPort;

    /** The ZIP file containing the JBoss configuration. */
    private String containerConfigurationFile;

    /**
     * Determines if the auto detection of configuration files is enabled or
     * not.
     */
    private boolean autoDetect;

    /** The name of the JBOSS configuration to use. */
    protected String configurationName;

    /**
     * Default constructor that will detect which OS is used to make sure the
     * JBOSS will be downloaded in the correct location.
     */
    public AbstractJBossContainerUtil() {
        autoDetect = true;

        setContainerName("JBoss");

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

        /*
         * Provide configuration information.
         */
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Downloading configuration from: " + remoteLocation);
            LOGGER.info("Container configuration file: " + containerConfigurationFile);
        }

        /*
         * Download and configure the JBoss configuration.
         */
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Installing [" + configurationName + "] configuration...");
        }
        final URL remoteLocation = new URL(this.remoteLocation + containerConfigurationFile);
        final String installDir = containerHome + "server/";
        final ZipURLInstaller installer = new ZipURLInstaller(remoteLocation, installDir, installDir);
        installer.install();

        /*
         * Setup the system properties.
         */
        systemProperties.put("jboss.server.lib.url:lib", "file:lib/");
        systemProperties.put("cargo.jnp.port", jnpPort.toString());

        /*
         * Setup JBoss specific configuration like JNDI properties, JNDI data
         * source files etc.
         */
        if (autoDetect) {
            copyResourceFileToConfDir(LOG4J_XML);

            final List<String> dataSourceFiles = findConfigurationFiles(DATA_SOURCE_FILES_SUFFIX);
            for (final String fileName : dataSourceFiles) {
                final String deployDirectory = getContainerDirectory("deploy/");
                copyResourceFile(fileName, deployDirectory);
            }

            final List<String> propertiesFiles = findConfigurationFiles(PROPERTIES_FILES_SUFFIX);
            for (final String fileName : propertiesFiles) {
                copyResourceFileToConfDir(fileName);
            }
        }

        /*
         * Do custom configuration.
         */
        setupConfiguration();
    }

    /**
     * Searches for configuration files with the specified suffix.
     * 
     * @param suffix
     *            The file suffix.
     * @return Returns a list of file names that end with the specified suffix.
     */
    protected List<String> findConfigurationFiles(final String suffix) {
        final List<String> files = new ArrayList<String>();

        final File directory = new File(configResourcesPath);
        final File[] listFiles = directory.listFiles();

        for (final File file : listFiles) {
            final String name = file.getName();

            if (org.springframework.util.StringUtils.endsWithIgnoreCase(name, suffix)) {
                files.add(name);

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Added configuration file called: " + name);
                }
            }
        }

        return files;
    }

    /**
     * Copies the specified resource file to the configuration directory of
     * JBoss.
     * 
     * @param fileName
     *            The file name that needs to be copied.
     */
    protected void copyResourceFileToConfDir(final String fileName) {
        copyResourceFile(fileName, getConfDirectory());
    }

    protected void copyResourceFile(final String fileName, final String destinationDirectory) {
        final String originalFile = configResourcesPath + fileName;
        final File srcFile = new File(originalFile);

        final String newFile = destinationDirectory + fileName;
        final File destFile = new File(newFile);

        try {
            FileUtils.copyFile(srcFile, destFile);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Copied file " + fileName + " to " + destFile.getAbsolutePath());
            }
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Failed to copy resource file: " + fileName);
            }
        }
    }

    @Override
    protected void deploy() {
        // create configuration factory
        final ConfigurationFactory configurationFactory = new DefaultConfigurationFactory();

        // create JBoss configuration
        final LocalConfiguration configuration = (LocalConfiguration) configurationFactory.createConfiguration("jboss4x", ContainerType.INSTALLED, ConfigurationType.EXISTING, containerHome
                + "server/" + configurationName);

        // setup configuration
        final StringBuilder args = new StringBuilder();
        for (final String arg : jvmArguments) {
            args.append(arg);
            args.append(" ");

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Added JVM argument: " + arg);
            }
        }
        configuration.setProperty(GeneralPropertySet.JVMARGS, args.toString());
        configuration.setProperty(ServletPropertySet.PORT, containerPort.toString());

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
        installedLocalContainer = (InstalledLocalContainer) new DefaultContainerFactory().createContainer("jboss4x", ContainerType.INSTALLED, configuration);

        // configure installedLocalContainer
        installedLocalContainer.setHome(containerHome);
        final Logger fileLogger = new FileLogger(new File(cargoLogFilePath + "cargo.log"), true);
        fileLogger.setLevel(LogLevel.DEBUG);
        installedLocalContainer.setLogger(fileLogger);
        installedLocalContainer.setOutput(cargoLogFilePath + "output.log");

        // set the system properties
        installedLocalContainer.setSystemProperties(systemProperties);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting JBoss [" + configurationName + "]...");
        }

        // startup installedLocalContainer
        installedLocalContainer.start();

        // Here you are assured the container is started.
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("JBoss up and running!");
        }
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
     * @param configuration
     *            The configuration where a deployable can be added to.
     * @param path
     *            The path where the deployable can be found.
     * @param deployableType
     *            The type of deployable.
     */
    private void addDeployable(final LocalConfiguration configuration, final String path, final DeployableType deployableType) {
        // retrieve deployable file
        final Deployable deployable = new DefaultDeployableFactory().createDeployable(configurationName, path, deployableType);

        // add deployable
        configuration.addDeployable(deployable);
    }

    /**
     * @param containerConfigurationFile
     *            the containerConfigurationFile to set
     */
    @Required
    public void setContainerConfigurationFile(final String containerConfigurationFile) {
        this.containerConfigurationFile = containerConfigurationFile;
    }

    /**
     * @param configurationName
     *            the configurationName to set
     */
    @Required
    public void setConfigurationName(final String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * @param jnpPort
     *            the jnpPort to set
     */
    @Required
    public void setJnpPort(final Integer jnpPort) {
        this.jnpPort = jnpPort;
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
        fullPath.append("server/");
        fullPath.append(this.configurationName);
        fullPath.append("/");
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

    /**
     * @param autoDetect
     *            the autoDetect to set
     */
    public void setAutoDetect(final boolean autoDetect) {
        this.autoDetect = autoDetect;
    }

}
