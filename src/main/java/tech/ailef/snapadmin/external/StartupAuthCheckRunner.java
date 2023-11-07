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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	

	@Bean
	ApplicationListener<ServletWebServerInitializedEvent> serverPortListenerBean() {
		return event -> {
			int serverPort = event.getWebServer().getPort();
			
			String url = "http://localhost:" + serverPort + "/" + properties.getBaseUrl();
			
			logger.info("Checking if SnapAdmin is protected with authentication at " + url);
			
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
			
			try {
				HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
		
				int statusCode = response.statusCode();
				if (statusCode == 200) {
					logger.warn("It seems SnapAdmin routes are not protected with authentication. The URL "
							+ url + " is publicly accessible: be careful!");
					snapAdmin.setAuthenticated(false);
				}
			} catch (IOException | InterruptedException e) {
				logger.warn("Unable to connect to server at " + url);
			}
		};
	}
}