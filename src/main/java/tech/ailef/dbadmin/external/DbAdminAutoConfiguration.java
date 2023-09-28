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
 * The configuration class that adds and configures the "internal" data source.
 *
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