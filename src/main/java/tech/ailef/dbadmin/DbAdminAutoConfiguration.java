package tech.ailef.dbadmin;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@ConditionalOnProperty(name = "dbadmin.enabled", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties(DbAdminProperties.class)
@AutoConfiguration
public class DbAdminAutoConfiguration {
    @Autowired
    Environment env;
	
	private static DataSource internalDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:file:./dbadmin_internal");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
	}
	
    public static LocalContainerEntityManagerFactoryBean internalEntityManager() {
		  LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		  factoryBean.setDataSource(internalDataSource());
		  factoryBean.setPersistenceUnitName("internal");
		  factoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
		  factoryBean.setPackagesToScan("tech.ailef.dbadmin.model");
		  factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		  Properties properties = new Properties();
		  properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		  properties.setProperty("hibernate.hbm2ddl.auto", "update");
		  factoryBean.setJpaProperties(properties);
		  factoryBean.afterPropertiesSet();

		  return factoryBean;
    }
}