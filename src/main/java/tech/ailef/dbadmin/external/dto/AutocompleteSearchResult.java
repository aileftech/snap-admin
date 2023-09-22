package tech.ailef.dbadmin.external.dto;

import tech.ailef.dbadmin.external.dbmapping.DbObject;

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
