package tech.ailef.dbadmin;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@ConditionalOnProperty(name = "dbadmin.enabled", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties(DbAdminProperties.class)
//@AutoConfiguration
@EnableJpaRepositories(
	entityManagerFactoryRef = "internalEntityManagerFactory", 
	transactionManagerRef = "internalTransactionManager", 
	basePackages = { "tech.repo" }
)
public class DbAdminAutoConfiguration {
    @Autowired
    Environment env;
    
    @Bean
	public DataSource internalDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:file:./dbadmin_internal");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
	}
	
    @Bean
    public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory() {
		  LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		  factoryBean.setDataSource(internalDataSource());
		  factoryBean.setPersistenceUnitName("internal");
		  factoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
//		  factoryBean.setManagedTypes(new PersistenceManagedTypes() {
//			
//				@Override
//				public URL getPersistenceUnitRootUrl() {
//					return null;
//				}
//
//				@Override
//				public List<String> getManagedPackages() {
//					return List.of("tech.ailef.dbadmin.model", "tech.ailef.dbadmin.repository");
//				}
//
//				@Override
//				public List<String> getManagedClassNames() {
//					return List.of("tech.ailef.dbadmin.model.Action");
//				}
//		  });
		  factoryBean.setPackagesToScan("tech.repo"); //, "tech.ailef.dbadmin.repository");
//		  List<ManagedType> ts = new ArrayList<>();
//		  factoryBean.setManagedTypes(ts);
		  factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		  Properties properties = new Properties();
		  properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		  properties.setProperty("hibernate.hbm2ddl.auto", "update");
		  factoryBean.setJpaProperties(properties);
		  factoryBean.afterPropertiesSet();
		  return factoryBean;
    }
    
//    @Bean
//    public EntityManager getInternalEntityManager() {
////    	if (internalEntityManager == null) {
//    		LocalContainerEntityManagerFactoryBean emf = internalEntityManager();
//    		EntityManagerFactory factory = emf.getNativeEntityManagerFactory();
//    		return factory.createEntityManager();
////    	}
////    	return internalEntityManager;
//    }
    
    @Bean
    public PlatformTransactionManager internalTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(internalEntityManagerFactory().getObject());
        return transactionManager;
    }
    
    
}