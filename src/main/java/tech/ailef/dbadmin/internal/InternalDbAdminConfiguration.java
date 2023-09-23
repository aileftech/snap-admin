package tech.ailef.dbadmin.internal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "dbadmin.enabled", matchIfMissing = true)
@ComponentScan
@Configuration
public class InternalDbAdminConfiguration {

}
