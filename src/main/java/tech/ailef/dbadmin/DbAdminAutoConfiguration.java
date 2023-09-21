package tech.ailef.dbadmin;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@ConditionalOnProperty(name = "dbadmin.enabled", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties(DbAdminProperties.class)
@AutoConfiguration
public class DbAdminAutoConfiguration {
    @Autowired
    Environment env;
	
//	@Bean
//	@Primary
////    @ConfigurationProperties("spring.datasource.dbadmin")
//    public DataSourceProperties internalDataSourceProperties() {
//        return new DataSourceProperties();
//    }
	
	@Bean
//	@Primary
	public DataSource internalDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:file:./dbadmin_internal");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
	}
	
	@Bean
//    @Primary
    public LocalContainerEntityManagerFactoryBean primeEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(internalDataSource());
        em.setPackagesToScan("tech.ailef.dbadmin.model");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.hibernate.ddl-auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
//        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);
        return em;
    }
 
    @Bean
//    @Primary
    public PlatformTransactionManager primeTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(primeEntityManager().getObject());
        return transactionManager;
    }


}