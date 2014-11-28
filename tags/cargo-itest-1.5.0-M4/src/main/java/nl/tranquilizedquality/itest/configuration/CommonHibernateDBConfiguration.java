/**
 * <pre>
 * Project: cargo-itest Created on: 26 nov. 2014 File: fCommonHibernateDBConfiguration.java
 * Package: nl.tranquilizedquality.itest.configuration
 *
 * Copyright (c) 2014 Tranquilized Quality www.tr-quality.com All rights
 * reserved.
 *
 * This software is the confidential and proprietary information of Dizizid
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Tranquilized Quality.
 * </pre>
 */
package nl.tranquilizedquality.itest.configuration;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

/**
 * @author Salomo Petrus (salomo.petrus@tr-quality.com)
 * @since 26 nov. 2014
 *
 */
@Configuration
@PropertySource("classpath:itest-db.properties")
public class CommonHibernateDBConfiguration extends DatasourceConfiguration {

    private Class<?>[] annotatedClasses;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.show_sql}")
    private String hibernateShowSQL;

    @Value("${hibernate.generate_statistics}")
    private String hibernateGenerateStatistics;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddl;

    @Bean(name = "transactionManager")
    public HibernateTransactionManager hibernateTransactionManager(final SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Lazy
    @Bean(name = "sessionFactory")
    public SessionFactory sessionFactory(final DataSource datasource) throws IOException {

        final LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(datasource);
        localSessionFactoryBean.setAnnotatedClasses(annotatedClasses);
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", hibernateDialect);
        hibernateProperties.setProperty("hibernate.show_sql", hibernateShowSQL);
        hibernateProperties.setProperty("hibernate.generate_statistics", hibernateGenerateStatistics);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddl);
        localSessionFactoryBean.setHibernateProperties(hibernateProperties);
        localSessionFactoryBean.afterPropertiesSet();
        return localSessionFactoryBean.getObject();
    }

}
