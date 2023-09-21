package tech.ailef.dbadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dbadmin")
public class DbAdminProperties {
	public boolean enabled = true;
	
	private String baseUrl;

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
	
	
}
