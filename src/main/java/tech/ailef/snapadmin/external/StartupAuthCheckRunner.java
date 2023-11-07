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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.ailef.snapadmin.external.exceptions.SnapAdminException;

/**
 * Runs at startup to determine if SnapAdmin is protected with authentication.
 * If this is not the case, it sets a flag that will display a warning in the
 * UI.
 */
@Configuration
public class StartupAuthCheckRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(StartupAuthCheckRunner.class);
	
	@Autowired
	private SnapAdmin snapAdmin;

	@Autowired
	private SnapAdminProperties properties;
	
	/**
	 * This event listener gets called after the server is initialized in order
	 * to have access to the value of the port (needed to build the URL at runtime).
	 * The method checks if SnapAdmin is accessible (status code == 200) to determine
	 * whether security is enabled, and if this is not the case it sets flags
	 * to display appropriate warnings. 
	 * @return
	 */
	@Bean
	ApplicationListener<ServletWebServerInitializedEvent> serverPortListenerBean() {
		return event -> {
			int serverPort = event.getWebServer().getPort();
			
			String link = "http://localhost:" + serverPort + "/" + properties.getBaseUrl();
			logger.info("Checking if SnapAdmin is protected with authentication at " + link);
			
			try {
				URL url = new URL(link);
				
				HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
				openConnection.setInstanceFollowRedirects(false);
				int statusCode = openConnection.getResponseCode();
				
				snapAdmin.setAuthenticated(statusCode != 200);
				if (statusCode == 200) {
					logger.warn("It appears that you have not enabled security so SnapAdmin is publicly accessible. "
							+ "Read here to learn how to secure SnapAdmin with Spring Security: https://www.snapadmin.dev/docs/#security");
				}

			} catch (IOException e) {
				throw new SnapAdminException(e);
			}
			
		};
	}
}