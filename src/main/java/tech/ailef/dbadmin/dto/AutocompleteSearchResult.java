package tech.ailef.dbadmin.dto;

import tech.ailef.dbadmin.dbmapping.DbObject;

public class AutocompleteSearchResult {
	private Object id;
	
	private String value;

	public AutocompleteSearchResult() {
	}
	
	public AutocompleteSearchResult(DbObject o) {
		this.id = o.getPrimaryKeyValue();
		this.value = o.getDisplayName();
	}
	
	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
