package tech.ailef.dbadmin;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility class the get the ApplicationContext 
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext appContext) {
		ctx = appContext;
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}