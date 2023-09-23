package tech.ailef.dbadmin.external.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.dto.QueryFilter;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public interface Utils {
	public static String camelToSnake(String v) {
		if (Character.isUpperCase(v.charAt(0))) {
			v = Character.toLowerCase(v.charAt(0)) + v.substring(1);
		}
		
		return v.replaceAll("([A-Z][a-z])", "_$1").toLowerCase();
		
	}
	
	public static MultiValueMap<String, String> computeParams(Set<QueryFilter> filters) {
		MultiValueMap<String, String> r = new LinkedMultiValueMap<>();
		if (filters == null)
			return r; 
		
		r.put("filter_field", new ArrayList<>());
		r.put("filter_op", new ArrayList<>());
		r.put("filter_value", new ArrayList<>());
		
		for (QueryFilter filter : filters) {
			r.get("filter_field").add(filter.getField());
			r.get("filter_op").add(filter.getOp().toString());
			r.get("filter_value").add(filter.getValue());
		}
			
		return r;
	}
	
	public static Set<QueryFilter> computeFilters(MultiValueMap<String, String> params) {
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
			
			QueryFilter queryFilter = new QueryFilter(field, CompareOperator.valueOf(op.toUpperCase()), value);
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
}
