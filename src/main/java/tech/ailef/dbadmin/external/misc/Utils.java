package tech.ailef.dbadmin.external.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.MultiValueMap;

import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.dto.QueryFilter;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

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
			
			QueryFilter queryFilter = new QueryFilter(schema.getFieldByJavaName(field), CompareOperator.valueOf(op.toUpperCase()), value);
			filters.add(queryFilter);
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
