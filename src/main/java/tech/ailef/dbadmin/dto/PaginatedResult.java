package tech.ailef.dbadmin.dto;

import java.util.List;

import tech.ailef.dbadmin.dbmapping.DbObject;

public class PaginatedResult {
	private PaginationInfo pagination;
	
	private List<DbObject> results;

	public PaginatedResult(PaginationInfo pagination, List<DbObject> page) {
		this.pagination = pagination;
		this.results = page;
	}

	public PaginationInfo getPagination() {
		return pagination;
	}

	public List<DbObject> getResults() {
		return results;
	}
	
	public int getActualResults() {
		return getResults().size();
	}
	
	
}
