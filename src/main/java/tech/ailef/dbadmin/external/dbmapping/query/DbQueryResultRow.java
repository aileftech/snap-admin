package tech.ailef.dbadmin.external.dbmapping.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public class DbQueryResultRow {
	private Map<DbQueryOutputField, Object> values;
	
	private String query;

	public DbQueryResultRow(Map<DbQueryOutputField, Object> values, String query) {
		this.values = values;
		this.query = query;
	}
	
	public List<DbQueryOutputField> getSortedFields() {
		return values.keySet().stream().sorted((f1, f2) -> {
			if (f1.isPrimaryKey() && !f2.isPrimaryKey()) {
				return -1;
			} else if (!f1.isPrimaryKey() && f2.isPrimaryKey()) {
				return 1;
			} else {
				return f1.getName().compareTo(f2.getName());
			}
		}).toList();
	}
	
	public String getQuery() {
		return query;
	}
	
	public Object get(DbQueryOutputField field) {
		return values.get(field);
	}

	public Object getFieldByName(String field) {
		DbQueryOutputField key = 
			values.keySet().stream().filter(f -> f.getName().equals(field)).findFirst().orElse(null);
		if (key == null) {
			throw new DbAdminException("Field " + field + " not found");
		}
		return get(key);
	}
	
	public Map<String, Object> toMap(List<String> fields) {
		Map<String, Object> result = new HashMap<>();
		for (String field : fields) {
			result.put(field, getFieldByName(field));
		}
		return result;
		
	}
	
	
}
