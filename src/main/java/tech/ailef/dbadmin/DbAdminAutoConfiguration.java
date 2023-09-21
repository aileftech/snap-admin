package tech.ailef.dbadmin;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ConditionalOnProperty(name = "dbadmin.enabled", matchIfMissing = true)
@ComponentScan
@EnableConfigurationProperties(DbAdminProperties.class)
@AutoConfiguration
public class DbAdminAutoConfiguration {
	
}