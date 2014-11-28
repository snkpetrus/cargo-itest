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


/**
 * This is a simple example of how the {@link AbstractGlassfishContainerUtil}
 * could be used. This test tests a simple test application called 'test-app'.
 *
 * As you can see you don't have to do much to get it to work. You only need to
 * do the following steps:
 * <ol>
 * <li>Extend from {@link AbstractGlassfishContainerUtil}.</li>
 * <li>Create a context file called <i>itest-context.xml</i> in the root of the
 * classpath.</li>
 * <li>Configure the container utility in the <i>itest-context.xml</i>. <b>Make
 * sure you give your bean the name 'containerUtil'.</b></li>
 * <li>Depending on the OS get the specific Glassfish application server to be
 * installed.</li>
 * </ol>
 *
 * After these steps you container utility should be setup correctly and all is
 * left is to create a unit test that uses the container. the 9011 is used.
 *
 * @author Vincenzo Vitale (vita)
 * @since 22 apr 2009
 *
 */
public class AppGlassfishContainerUtil extends AbstractGlassfishContainerUtil {

    /*
     * (non-Javadoc)
     *
     * @seenl.tranquilizedquality.itest.cargo.AbstractJOnasContainerUtil#
     * setupConfiguration()
     */
    @Override
    protected void setupConfiguration() {

    }
}
