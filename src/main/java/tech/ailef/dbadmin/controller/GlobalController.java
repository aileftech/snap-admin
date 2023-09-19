package tech.ailef.dbadmin.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class registers some ModelAttribute objects that are
 * used in all templates. 
 */
@ControllerAdvice
public class GlobalController {

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
}