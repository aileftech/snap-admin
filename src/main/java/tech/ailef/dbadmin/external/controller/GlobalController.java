package tech.ailef.dbadmin.external.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import tech.ailef.dbadmin.external.DbAdminProperties;

/**
 * This class registers some ModelAttribute objects that are
 * used in all templates. 
 */
@ControllerAdvice
public class GlobalController {

	@Autowired
	private DbAdminProperties props;
	
	/**
	 * A multi valued map containing the query parameters. It is used primarily
	 * in building complex URL when performing faceted search with multiple filters.
	 * @param request the incoming request
	 * @return multi valued map of request parameters
	 */
	@ModelAttribute("queryParams")
	public Map<String, String[]> getQueryParams(HttpServletRequest request) {
		return request.getParameterMap();
	}
	
	/**
	 * The baseUrl as specified in the properties file by the user
	 * @param request
	 * @return
	 */
	@ModelAttribute("baseUrl")
	public String getBaseUrl(HttpServletRequest request) {
		return props.getBaseUrl();
	}
}