package tech.ailef.dbadmin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The 'dbadmin.*' properties that can be set in the properties file
 * to configure the behaviour of Spring Boot Admin Panel. 
 */
@ConfigurationProperties("dbadmin")
public class DbAdminProperties {
	/**
	 * Whether Spring Boot Database Admin is enabled.
	 */
	public boolean enabled = true;
	
	/**
	 * The prefix that is prepended to all routes registered by Spring Boot Database Admin.
	 */
	private String baseUrl;

	/**
	 * The path of the package that contains your JPA `@Entity` classes to be scanned.
	 */
	private String modelsPackage;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getModelsPackage() {
		return modelsPackage;
	}
	
	public void setModelsPackage(String modelsPackage) {
		this.modelsPackage = modelsPackage;
	}
	
	public Map<String, String> toMap() {
		Map<String, String> conf = new HashMap<>();
		conf.put("enabled", enabled + "");
		conf.put("baseUrl", baseUrl);
		conf.put("modelsPackage", modelsPackage);
		return conf;
	}
	
	
}
