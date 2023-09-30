package tech.ailef.dbadmin.external.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Describes a request that contains parameters that are used
 * to filter results.  
 *
 */
public interface FilterRequest {
	/**
	 * Converts the request to a MultiValue map that can be 
	 * later converted into a query string
	 * @return
	 */
	public MultiValueMap<String, String> computeParams();
	
	/**
	 * Empty filtering request
	 * @return an empty map
	 */
	public static MultiValueMap<String, String> empty() {
		return new LinkedMultiValueMap<>();
	}
}
