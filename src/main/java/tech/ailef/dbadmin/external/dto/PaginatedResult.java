package tech.ailef.dbadmin.external.dto;

import java.util.List;

/**
 * A wrapper class that holds info about the current pagination and one page
 * of returned result.  
 */
public class PaginatedResult<T> {
	/**
	 * The pagination settings used to produce this output
	 */
	private PaginationInfo pagination;
	
	/**
	 * The list of results in the current page
	 */
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
