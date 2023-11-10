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


package tech.ailef.snapadmin.external.dbmapping.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.ailef.snapadmin.external.exceptions.SnapAdminException;

/**
 * A single row of results coming from a user-provided SQL query
 * run via the SQL console.
 */
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
			throw new SnapAdminException("Field " + field + " not found");
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
