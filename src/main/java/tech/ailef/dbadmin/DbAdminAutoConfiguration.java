package tech.ailef.dbadmin;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@ConditionalOnProperty(name = "crudadmin.enabled", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties(DbAdminProperties.class)
@Configuration
public class DbAdminAutoConfiguration {
	
}