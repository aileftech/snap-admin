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


package tech.ailef.dbadmin.external.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.DbAdminProperties;
import tech.ailef.dbadmin.external.exceptions.DbAdminNotFoundException;
import tech.ailef.dbadmin.internal.UserConfiguration;

/**
 * This class registers some global ModelAttributes and exception handlers.
 * 
 */
@ControllerAdvice
public class GlobalController {

	@Autowired
	private DbAdminProperties props;

	@Autowired
	private UserConfiguration userConf;
	
	@Autowired
	private DbAdmin dbAdmin;
	
	@ExceptionHandler(DbAdminNotFoundException.class)
	public String handleNotFound(Exception e, Model model) {
		model.addAttribute("status", "404");
		model.addAttribute("error", "Error");
		model.addAttribute("message", e.getMessage());
		model.addAttribute("dbadmin_userConf", userConf);
		model.addAttribute("dbadmin_baseUrl", getBaseUrl());
		model.addAttribute("dbadmin_version", dbAdmin.getVersion());
		return "other/error";
	}
	
	@ModelAttribute("dbadmin_version")
	public String getVersion() {
		return dbAdmin.getVersion();
	}
	
	/**
	 * A multi valued map containing the query parameters. It is used primarily
	 * in building complex URL when performing faceted search with multiple filters.
	 * @param request the incoming request
	 * @return multi valued map of request parameters
	 */
	@ModelAttribute("dbadmin_queryParams")
	public Map<String, String[]> getQueryParams(HttpServletRequest request) {
		return request.getParameterMap();
	}
	
	/**
	 * The baseUrl as specified in the properties file by the user
	 * @return
	 */
	@ModelAttribute("dbadmin_baseUrl")
	public String getBaseUrl() {
		return props.getBaseUrl();
	}
	
	/**
	 * The full request URL, not including the query string
	 * @param request
	 * @return
	 */
	@ModelAttribute("dbadmin_requestUrl")
	public String getRequestUrl(HttpServletRequest request) {
		return request.getRequestURI();
	}
	
	/**
	 * The UserConfiguration object used to retrieve values specified
	 * in the settings table.
	 * @return
	 */
	@ModelAttribute("dbadmin_userConf")
	public UserConfiguration getUserConf() {
		return userConf;
	}
}

