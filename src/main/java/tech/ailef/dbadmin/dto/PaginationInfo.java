package tech.ailef.dbadmin.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.MultiValueMap;

import tech.ailef.dbadmin.misc.Utils;

/**
 * Attached as output to requests that have a paginated response,
 * holds information about the current pagination.
 */
public class PaginationInfo {
	/**
	 * How many previous and next pages to generate, used in the front-end navigation
	 */
	private static final int PAGE_RANGE = 3;
	
	/**
	 * The current page of results
	 */
	private int currentPage;
	
	/**
	 * The last page for which there are results
	 */
	private int maxPage;
	
	/**
	 * The current number of elements per page
	 */
	private int pageSize;
	
	private long maxElement;
	
	private Set<QueryFilter> queryFilters;
	
	private String query;
	
	private String sortKey;
	
	private String sortOrder;

	public PaginationInfo(int currentPage, int maxPage, int pageSize, long maxElement, String query, 
			String sortKey, String sortOrder, Set<QueryFilter> queryFilters) {
		this.currentPage = currentPage;
		this.maxPage = maxPage;
		this.pageSize = pageSize;
		this.query = query;
		this.maxElement = maxElement;
		this.queryFilters = queryFilters;
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getMaxPage() {
		return maxPage;
	}
	
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public long getMaxElement() {
		return maxElement;
	}
	
	public String getSortedPageLink(String sortKey, String sortOrder) {
		MultiValueMap<String, String> params = Utils.computeParams(queryFilters);
		
		if (query != null) {
			params.put("query", new ArrayList<>());
			params.get("query").add(query);
		}
		
		params.add("pageSize", "" + pageSize);
		params.add("page", "" + currentPage);
		params.add("sortKey", sortKey);
		params.add("sortOrder", sortOrder);
		
		return Utils.getQueryString(params);
	}
	
	public String getLink(int page) {
		MultiValueMap<String, String> params = Utils.computeParams(queryFilters);
		
		if (query != null) {
			params.put("query", new ArrayList<>());
			params.get("query").add(query);
		}
		
		params.add("pageSize", "" + pageSize);
		params.add("page", "" + page);
		
		return Utils.getQueryString(params);
	}

	public List<Integer> getBeforePages() {
		return IntStream.range(Math.max(currentPage - PAGE_RANGE,  1), currentPage).boxed().collect(Collectors.toList());
	}
	
	public List<Integer> getAfterPages() {
		return IntStream.range(currentPage + 1, Math.min(currentPage + PAGE_RANGE,  maxPage + 1)).boxed().collect(Collectors.toList());
	}
	
	
	public boolean isLastPage() {
		return currentPage == maxPage;
	}
}
