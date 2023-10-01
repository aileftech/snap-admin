/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package tech.ailef.dbadmin.external;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import tech.ailef.dbadmin.internal.InternalDbAdminConfiguration;

/**
 * The configuration class for "internal" data source. This is not the
 * source connected to the user's data/entities, but rather an internal
 * H2 database which is used by Spring Boot Database Admin to store user
 * settings and other information like operations history. 
 */
@ConditionalOnProperty(name = "dbadmin.enabled", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties(DbAdminProperties.class)
@Configuration
@EnableJpaRepositories(
	entityManagerFactoryRef = "internalEntityManagerFactory", 
	transactionManagerRef = "internalTransactionManager", 
	basePackages = { "tech.ailef.dbadmin.internal.repository" }
)
@EnableTransactionManagement
@Import(InternalDbAdminConfiguration.class)
public class DbAdminAutoConfiguration {
	@Autowired
	private DbAdminProperties props;

	@Bean
	public DataSource internalDataSource() {
		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName("org.h2.Driver");
		if (props.isTestMode()) {
			dataSourceBuilder.url("jdbc:h2:mem:test");
		} else {
			dataSourceBuilder.url("jdbc:h2:file:./dbadmin_internal");
		}
		
		dataSourceBuilder.username("sa");
		dataSourceBuilder.password("password");
		return dataSourceBuilder.build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(internalDataSource());
		factoryBean.setPersistenceUnitName("internal");
		factoryBean.setPackagesToScan("tech.ailef.dbadmin.internal.model");
		factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		factoryBean.setJpaProperties(properties);
		factoryBean.afterPropertiesSet();
		return factoryBean;
	}

	@Bean
	public PlatformTransactionManager internalTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(internalEntityManagerFactory().getObject());
		return transactionManager;
	}

}