/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
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


package tech.ailef.dbadmin.external.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.MultiValueMap;

import tech.ailef.dbadmin.external.misc.Utils;

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
	
	// TODO: Check if used
	private long maxElement;
	
	private FilterRequest filterRequest;
	
	private String query;
	
	public PaginationInfo(int currentPage, int maxPage, int pageSize, long maxElement, String query, FilterRequest request) {
		this.currentPage = currentPage;
		this.maxPage = maxPage;
		this.pageSize = pageSize;
		this.query = query;
		this.maxElement = maxElement;
		this.filterRequest = request;
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
		MultiValueMap<String, String> params = FilterRequest.empty();
		
		if (filterRequest != null)
			params = filterRequest.computeParams();
		
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
		MultiValueMap<String, String> params = FilterRequest.empty();
		
		if (filterRequest != null)
			params = filterRequest.computeParams();
		
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
//	
//	public String getSortKey() {
//		return sortKey;
//	}
//	
//	public String getSortOrder() {
//		return sortOrder;
//	}
	
	public boolean isLastPage() {
		return currentPage == maxPage;
	}
}
