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
package nl.tranquilizedquality.itest;

import nl.tranquilizedquality.itest.configuration.CommonHibernateDBConfiguration;
import nl.tranquilizedquality.itest.configuration.SQLScriptsConfiguration;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the base class of a simple integration test. Extending from this
 * class will safe you some work and gets you up and running pretty quick. You
 * can also override the annotation configuration if you i.e. don't want to use
 * the {@link CommonHibernateDBConfiguration}. If you do not specify the list of
 * SQL scripts it will not execute any scripts so you are free to use SQL
 * scripts or any other method you like to use for creating test data.
 *
 * @author Salomo Petrus (sape)
 * @since 13 feb 2009
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CommonHibernateDBConfiguration.class, SQLScriptsConfiguration.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class AbstractDefaultHibernateDeploymentTest extends AbstractDefaultDeploymentTest {

}
