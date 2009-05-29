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
import java.util.List;
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
 * Implementation of a {@link ContainerUtil} for the Glassfish application
 * server.
 * 
 * @author Vincenzo Vitale (vita)
 * @since 20 May 2009
 * 
 */
public abstract class AbstractGlassfishContainerUtil extends
        AbstractInstalledContainerUtil {
    /** Logger for this class */
    private static final Log log =
            LogFactory.getLog(AbstractGlassfishContainerUtil.class);

    /** The name of the Glassfish configuration to use. */
    protected String configurationName;

    /**
     * Default constructor that will detect which OS is used to make sure the
     * Glassfish will be downloaded in the correct location.
     */
    public AbstractGlassfishContainerUtil() {
        setContainerName("glassfish");

        cleanUpContainer();

    }

    /**
     * Installs the container and the application configuration. It also sets
     * some system properties so the container can startup properly. Finally it
     * sets up additional configuration like jndi.properties files etc.
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
        final ConfigurationFactory configurationFactory =
                new DefaultConfigurationFactory();

        // create Glassfish configuration
        final LocalConfiguration configuration =
                (LocalConfiguration) configurationFactory.createConfiguration(
                        "glassfish2", ContainerType.INSTALLED,
                        ConfigurationType.STANDALONE, containerHome
                                + "cargo-conf/");

        // setup configuration
        final StringBuilder args = new StringBuilder();
        for (String arg : jvmArguments) {
            args.append(arg);
            args.append(" ");

            if (log.isInfoEnabled()) {
                log.info("Added JVM argument: " + arg);
            }
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
                        .createContainer("glassfish2", ContainerType.INSTALLED,
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
        try {
            completeGlassfishConfiguration();
        } catch (IOException e) {
            throw new DeployException(
                    "Failed to complete the Glassfish configuration while setting the env files",
                    e);
        }

        if (log.isInfoEnabled()) {
            log.info("Starting Glassfish [" + configurationName + "]...");
        }

        // startup installedLocalContainer
        installedLocalContainer.start();

        // Here you are assured the container is started.
        if (log.isInfoEnabled()) {
            log.info("Glassfish up and running!");
        }
    }

    /**
     * Complete the Glassfish configuration.
     * 
     * @throws IOException
     * 
     */
    private void completeGlassfishConfiguration() throws IOException {

        // FIXME: Complete setting also the port to the value
        // ${cargo.server.port}

        // We need to append the java home value to the env script.
        final String operatingSystem = System.getProperty("os.name");
        File destFile = null;
        if (operatingSystem != null && operatingSystem.startsWith("Windows")) {

            destFile = new File("C:/WINDOWS/Temp/glassfish/config/asenv.bat");

            List readLines = FileUtils.readLines(destFile);
            readLines.add("set AS_JAVA=" + System.getProperty("java.home"));
            FileUtils.writeLines(destFile, readLines);

        } else {
            destFile = new File("/tmp/glassfish/config/asenv.conf");

            List readLines = FileUtils.readLines(destFile);
            readLines
                    .add("AS_JAVA=\"" + System.getProperty("java.home") + "\"");
            FileUtils.writeLines(destFile, readLines);

            // There is a problem in Linux executing the file... probably is a
            // bug in the glassfish plugin

            // We must use the Runtime call (not really portable) since the
            // setExecutable method in the File class is only available from
            // Java 6
            // File executable = new File("/tmp/glassfish/bin/asadmin");
            // executable.setExecutable(true);
            Runtime.getRuntime().exec(
                    "chmod 754 " + "/tmp/glassfish/bin/asadmin");

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
     * @param configurationName the configurationName to set
     */
    @Required
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
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
