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


package tech.ailef.snapadmin.external.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.MultiValueMap;

import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.snapadmin.external.dbmapping.fields.DbField;
import tech.ailef.snapadmin.external.dto.CompareOperator;
import tech.ailef.snapadmin.external.dto.QueryFilter;
import tech.ailef.snapadmin.external.exceptions.DbAdminException;

/**
 * Collection of utility functions used across the project
 *
 */
public interface Utils {
	/**
	 * Converts snake case to camel case
	 * @param text
	 * @return
	 */
	public static String snakeToCamel(String text) {
		boolean shouldConvertNextCharToLower = true;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
		    char currentChar = text.charAt(i);
		    if (currentChar == '_') {
		        shouldConvertNextCharToLower = false;
		    } else if (shouldConvertNextCharToLower) {
		        builder.append(Character.toLowerCase(currentChar));
		    } else {
		        builder.append(Character.toUpperCase(currentChar));
		        shouldConvertNextCharToLower = true;
		    }
		}
		return builder.toString();
	}

	/**
	 * Convers camel case to snake case
	 * @param v
	 * @return
	 */
	public static String camelToSnake(String v) {
		if (Character.isUpperCase(v.charAt(0))) {
			v = Character.toLowerCase(v.charAt(0)) + v.substring(1);
		}
		
		return v.replaceAll("([A-Z][a-z])", "_$1").toLowerCase();
		
	}
	
	/**
	 * Converts a multi value map of parameters containing query filters applied
	 * with the faceted search feature into a set of QueryFilter objects
	 * @param schema
	 * @param params
	 * @return
	 */
	public static Set<QueryFilter> computeFilters(DbObjectSchema schema, MultiValueMap<String, String> params) {
		if (params == null)
			return new HashSet<>();

        List<String> ops = params.get("filter_op");
		List<String> fields = params.get("filter_field");
		List<String> values = params.get("filter_value");

		if (ops == null || fields == null || values == null)
			return new HashSet<>();
		
		if (ops.size() != fields.size() || fields.size() != values.size()
			|| ops.size() != values.size()) {
			throw new DbAdminException("Filtering parameters must have the same size");
		}

		Set<QueryFilter> filters = new HashSet<>();
		for (int i = 0; i < ops.size(); i++) {
			String op = ops.get(i);
			String field = fields.get(i);
			String value = values.get(i);

			// Check if the field can actually be found before creating the filter
			// This shouldn't normally happen because this parameter is not provided
			// by the user; but there's the chance of a stale bookmarked link referring
			// to a non-existing schema or the user fiddling with the URL
			DbField dbField = schema.getFieldByJavaName(field);
			if (dbField != null) {
				QueryFilter queryFilter = new QueryFilter(dbField, CompareOperator.valueOf(op.toUpperCase()), value);
				filters.add(queryFilter);
			}
		}
		
		return filters;
			
	}

	
	public static String getQueryString(MultiValueMap<String, String> params) {
		Set<String> currentParams = params.keySet();
		List<String> paramValues = new ArrayList<>();
		for (String param : currentParams) {
			for (String v : params.get(param)) {
				paramValues.add(param + "=" + v.trim());
			}
		}
		
		if (paramValues.isEmpty()) return "";
		return "?" + String.join("&", paramValues);
	}
	
}
