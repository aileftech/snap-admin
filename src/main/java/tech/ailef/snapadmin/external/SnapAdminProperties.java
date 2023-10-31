/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package tech.ailef.snapadmin.external;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The 'dbadmin.*' properties that can be set in the properties file
 * to configure the behaviour of Spring Boot Admin Panel. 
 */
@ConfigurationProperties("dbadmin")
public class SnapAdminProperties {
	/**
	 * Whether SnapAdmin is enabled.
	 */
	public boolean enabled = true;
	
	/**
	 * The prefix that is prepended to all routes registered by SnapAdmin.
	 */
	private String baseUrl;

	/**
	 * The path of the package that contains your JPA `@Entity` classes to be scanned.
	 */
	private String modelsPackage;

	/**
	 * Set to true when running the tests to configure the "internal" data source as in memory
	 */
	private boolean testMode = false;
	
	/**
	 * Whether the SQL console feature is enabled
	 */
	private boolean sqlConsoleEnabled = true;
	
	/**
	 * Whether SnapAdmin is enabled
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isSqlConsoleEnabled() {
		return sqlConsoleEnabled;
	}
	
	public void setSqlConsoleEnabled(boolean sqlConsoleEnabled) {
		this.sqlConsoleEnabled = sqlConsoleEnabled;
	}

	/**
	 * Returns the prefix that is prepended to all routes registered by SnapAdmin.
	 * @return
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Returns the path of the package that contains your JPA `@Entity` classes to be scanned.
	 * @return
	 */
	public String getModelsPackage() {
		return modelsPackage;
	}
	
	public void setModelsPackage(String modelsPackage) {
		this.modelsPackage = modelsPackage;
	}
	
	public boolean isTestMode() {
		return testMode;
	}
	
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
//	public Map<String, String> toMap() {
//		Map<String, String> conf = new HashMap<>();
//		conf.put("enabled", enabled + "");
//		conf.put("baseUrl", baseUrl);
//		conf.put("modelsPackage", modelsPackage);
//		conf.put("testMode", testMode + "");
//		conf.put("sqlConsoleEnabled", sqlConsoleEnabled + "");
//		return conf;
//	}
	
	
}
