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
 * installedLocalContainer.
 * 
 * @author Salomo Petrus (sape)
 * @since 11/12/2008
 * 
 */
public abstract class AbstractJBossContainerUtil implements ContainerUtil {
	/** Logger for this class */
	private static final Log log = LogFactory.getLog(AbstractJBossContainerUtil.class);

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
	 * Default constructor that will detect which OS is used to make sure the
	 * JBOSS will be downloaded in the correct location.
	 */
	public AbstractJBossContainerUtil() {
		String operatingSystem = System.getProperty("os.name");
		if (operatingSystem != null && operatingSystem.startsWith("Windows")) {
			containerHome = "C:/WINDOWS/Temp/jboss/";
		}
		else {
			containerHome = "/tmp/jboss/";
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
	 * @throws Exception
	 *             Is thrown when something went wrong if the configuration
	 *             setup fails.
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
	 * @throws Exception
	 *             Is thrown when something goes wrong during the setup of the
	 *             container.
	 */
	protected void setupContainer() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Cleaning up JBoss...");
		}

		FileUtils.deleteDirectory(new File(containerHome));
		new File(containerHome).mkdir();

		if (log.isInfoEnabled()) {
			log.info("Installing JBoss...");
			log.info("Downloading JBoss & configuration from: " + remoteLocation);
			log.info("Container file: " + containerFile);
			log.info("Container configuration file: " + containerConfigurationFile);
		}
		URL remoteLocation = new URL(this.remoteLocation + containerFile);
		String installDir = containerHome + "..//";
		ZipURLInstaller installer = new ZipURLInstaller(remoteLocation, installDir);
		installer.install();

		if (log.isInfoEnabled()) {
			log.info("Installing [" + configurationName + "] configuration...");
		}
		remoteLocation = new URL(this.remoteLocation + containerConfigurationFile);
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
		ConfigurationFactory configurationFactory = new DefaultConfigurationFactory();

		// create JBoss configuration
		LocalConfiguration configuration = (LocalConfiguration) configurationFactory.createConfiguration("jboss4x", ContainerType.INSTALLED, ConfigurationType.EXISTING, containerHome
				+ "server/" + configurationName);

		// setup configuration
		StringBuilder args = new StringBuilder();
		for (String arg : jvmArguments) {
			args.append(arg);
			args.append(" ");
		}
		configuration.setProperty(GeneralPropertySet.JVMARGS, args.toString());
		configuration.setProperty(ServletPropertySet.PORT, containerPort.toString());

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
				deployableType = DeployableType.EAR;
			}
			else if (value.equals("WAR")) {
				deployableType = DeployableType.WAR;
			}
			else if (value.equals("EJB")) {
				deployableType = DeployableType.EJB;
			}
			else {
				// Default value is EAR file
				deployableType = DeployableType.EAR;
			}

			// retrieve deployable file
			Deployable deployable = new DefaultDeployableFactory().createDeployable(configurationName, entry.getKey(), deployableType);

			// add deployable
			configuration.addDeployable(deployable);
		}

		// create installedLocalContainer
		installedLocalContainer = (InstalledLocalContainer) new DefaultContainerFactory().createContainer("jboss4x", ContainerType.INSTALLED, configuration);

		// configure installedLocalContainer
		installedLocalContainer.setHome(containerHome);
		Logger fileLogger = new FileLogger(new File(cargoLogFilePath + "cargo.log"), true);
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
	public void setJvmArguments(List<String> jvmArguments) {
		this.jvmArguments = new ArrayList<String>(jvmArguments);
	}

	@Required
	public void setCargoLogFilePath(String cargoLogFilePath) {
		this.cargoLogFilePath = cargoLogFilePath;
	}

	/**
	 * @param containerPort
	 *            the containerPort to set
	 */
	@Required
	public void setContainerPort(Integer containerPort) {
		this.containerPort = containerPort;
	}

	/**
	 * @param remoteLocation
	 *            the remoteLocation to set
	 */
	@Required
	public void setRemoteLocation(String remoteLocation) {
		this.remoteLocation = remoteLocation;
	}

	/**
	 * @param containerFile
	 *            the containerFile to set
	 */
	@Required
	public void setContainerFile(String containerFile) {
		this.containerFile = containerFile;
	}

	/**
	 * @param containerConfigurationFile
	 *            the containerConfigurationFile to set
	 */
	@Required
	public void setContainerConfigurationFile(String containerConfigurationFile) {
		this.containerConfigurationFile = containerConfigurationFile;
	}

	/**
	 * @param systemProperties
	 *            the systemProperties to set
	 */
	public void setSystemProperties(Map<String, String> systemProperties) {
		this.systemProperties = systemProperties;
	}

	/**
	 * @param configurationName
	 *            the configurationName to set
	 */
	@Required
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}

	/**
	 * @return the deployableLocations
	 */
	public Map<String, String> getDeployableLocations() {
		return deployableLocations;
	}

	/**
	 * @param deployableLocations
	 *            the deployableLocations to set
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
	 * @param jnpPort the jnpPort to set
	 */
	@Required
	public void setJnpPort(Integer jnpPort) {
		this.jnpPort = jnpPort;
	}

}
