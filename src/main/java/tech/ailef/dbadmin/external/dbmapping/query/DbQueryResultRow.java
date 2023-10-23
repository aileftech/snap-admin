package tech.ailef.dbadmin.external.dbmapping.query;

import java.util.List;
import java.util.Map;

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
	
	
	
	
}
