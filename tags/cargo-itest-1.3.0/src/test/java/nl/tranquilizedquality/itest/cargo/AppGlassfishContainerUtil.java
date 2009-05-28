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
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * This is a simple example of how the {@link AbstractGlassfishContainerUtil} could
 * be used. This test tests a simple test application called 'test-app'.
 * 
 * As you can see you don't have to do much to get it to work. You only need to
 * do the following steps:
 * <ol>
 * <li>Extend from {@link AbstractGlassfishContainerUtil}.</li>
 * <li>Create a context file called <i>itest-context.xml</i> in the root of the
 * classpath.</li>
 * <li>Configure the container utility in the <i>itest-context.xml</i>. <b>Make
 * sure you give your bean the name 'containerUtil'.</b></li>
 * <li>Depending on the OS get the specific Glassfish application server to be installed.</li>
 * </ol>
 * 
 * After these steps you container utility should be setup correctly and all is
 * left is to create a unit test that uses the container.
 * 
 * @author Vincenzo Vitale (vita)
 * @since 22 apr 2009
 * 
 */
public class AppGlassfishContainerUtil extends AbstractGlassfishContainerUtil {

	@SuppressWarnings("unchecked")
	@Override
	protected void setupConfiguration() throws Exception {
		// We need to append the java home value to the env script.
		final String operatingSystem = System.getProperty("os.name");
		File destFile = null;
		if (operatingSystem != null && operatingSystem.startsWith("Windows")) {

			destFile = new File("C:/WINDOWS/Temp/glassfish/config/asenv.bat");

			List readLines = FileUtils.readLines(destFile);
			readLines.add("set AS_JAVA=" + System.getProperty("java.home"));
			FileUtils.writeLines(destFile, readLines);

		}
		else {
			destFile = new File("/tmp/glassfish/config/asenv.conf");

			List readLines = FileUtils.readLines(destFile);
			readLines.add("AS_JAVA=\"" + System.getProperty("java.home") + "\"");
			FileUtils.writeLines(destFile, readLines);

			// There is a problem in Linux executing the file... probably is a
			// bug in the glassfish plugin

			// We must use the Runtime call (not really portable) since the
			// setExecutable method in the File class is only available from
			// Java 6
			// File executable = new File("/tmp/glassfish/bin/asadmin");
			// executable.setExecutable(true);
			Runtime.getRuntime().exec("chmod 754 " + "/tmp/glassfish/bin/asadmin");

		}

	}
}
