/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
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


package tech.ailef.dbadmin.external;

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
	 * Set to true to prepend baseUrl to static resources path (css, js)
	 */
	private boolean prependBaseUrlToResourcesPath;

	/**
	 * The path of the package that contains your JPA `@Entity` classes to be scanned.
	 */
	private String modelsPackage;

	/**
	 * Set to true when running the tests to configure the "internal" data source as in memory
	 */
	private boolean testMode = false;
	
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

	public void setPrependBaseUrlToResourcesPath(boolean prependBaseUrlToResourcesPath) {
		this.prependBaseUrlToResourcesPath = prependBaseUrlToResourcesPath;
	}

	public boolean isPrependBaseUrlToResourcesPath() {
		return prependBaseUrlToResourcesPath;
	}

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

	public String getResourcesPath() {
		return prependBaseUrlToResourcesPath ? "/"+baseUrl : "";
	}

	public Map<String, String> toMap() {
		Map<String, String> conf = new HashMap<>();
		conf.put("enabled", enabled + "");
		conf.put("baseUrl", baseUrl);
		conf.put("resourcesPath", getResourcesPath());
		conf.put("modelsPackage", modelsPackage);
		conf.put("testMode", testMode + "");
		return conf;
	}
	
	
}
