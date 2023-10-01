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
