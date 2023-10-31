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


package tech.ailef.snapadmin.external.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.MultiValueMap;

import tech.ailef.snapadmin.external.misc.Utils;

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
	 * The current requested page
	 */
	private int currentPage;
	
	/**
	 * The last page for which there are results available
	 */
	private int maxPage;
	
	/**
	 * The current number of elements per page
	 */
	private int pageSize;
	
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

	/**
	 * Returns the current requested page
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	/**
	 * Returns the last page for which there are results available
	 * @return
	 */
	public int getMaxPage() {
		return maxPage;
	}
	
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	/**
	 * Returns the current number of elements per page
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	/**
	 * Returns the total count of elements for all pages
	 * @return
	 */
	public long getMaxElement() {
		return maxElement;
	}
	
	/**
	 * Returns a link to the current page by preserving all the other
	 * filtering parameters but changing the sort order.
	 * 
	 * @param sortKey the field to use for sorting
	 * @param sortOrder the order, DESC or ASC
	 * @return a link to change the sort order for the current page
	 */
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
	
	/**
	 * Returns a link to the specified page by preserving all the other
	 * filtering parameters 
	 * 
	 * @param page the page to generate the link for
	 * @return	
	 */
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

	/**
	 * Returns the pages before the current one
	 * @return
	 */
	public List<Integer> getBeforePages() {
		return IntStream.range(Math.max(currentPage - PAGE_RANGE,  1), currentPage).boxed().collect(Collectors.toList());
	}
	
	/**
	 * Returns the pages after the current one
	 * @return
	 */
	public List<Integer> getAfterPages() {
		return IntStream.range(currentPage + 1, Math.min(currentPage + PAGE_RANGE,  maxPage + 1)).boxed().collect(Collectors.toList());
	}
	
	/**
	 * Returns whether the current page is the last one
	 * @return
	 */
	public boolean isLastPage() {
		return currentPage == maxPage;
	}
}
