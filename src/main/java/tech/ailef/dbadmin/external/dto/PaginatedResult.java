package tech.ailef.dbadmin.external.dto;

import java.util.List;

import tech.ailef.dbadmin.external.dbmapping.DbObject;

public class PaginatedResult<T> {
	private PaginationInfo pagination;
	
	private List<T> results;

	public PaginatedResult(PaginationInfo pagination, List<T> page) {
		this.pagination = pagination;
		this.results = page;
	}

	public PaginationInfo getPagination() {
		return pagination;
	}

	public List<T> getResults() {
		return results;
	}
	
	public boolean isEmpty() {
		return results.isEmpty();
	}
	
	public int getNumberOfResults() {
		return getResults().size();
	}
	
	
}
