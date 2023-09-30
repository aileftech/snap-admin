package tech.ailef.dbadmin.external.dto;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * The filter request for faceted search. It is implemented as a
 * set of filters that can be stacked on top of each other. 
 *
 */
public class FacetedSearchRequest implements FilterRequest {
	private Set<QueryFilter> filters;
	
	public FacetedSearchRequest(Set<QueryFilter> filters) {
		this.filters = filters;
	}

	@Override
	public MultiValueMap<String, String> computeParams() {
		MultiValueMap<String, String> r = new LinkedMultiValueMap<>();
		if (filters == null)
			return r; 
		
		r.put("filter_field", new ArrayList<>());
		r.put("filter_op", new ArrayList<>());
		r.put("filter_value", new ArrayList<>());
		
		for (QueryFilter filter : filters) {
			r.get("filter_field").add(filter.getField().getJavaName());
			r.get("filter_op").add(filter.getOp().toString());
			r.get("filter_value").add(filter.getValue());
		}
			
		return r;
	}

}
