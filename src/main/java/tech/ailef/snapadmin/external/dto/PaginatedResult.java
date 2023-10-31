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


package tech.ailef.snapadmin.external.dto;

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

	/**
	 * Returns the pagination settings used to produce this output
	 * @return
	 */
	public PaginationInfo getPagination() {
		return pagination;
	}

	/**
	 * Returns the list of results in the current page
	 * @return
	 */
	public List<T> getResults() {
		return results;
	}
	
	/**
	 * Returns whether the results are empty
	 * @return
	 */
	public boolean isEmpty() {
		return results.isEmpty();
	}
	
	/**
	 * Returns the number of results for the current page
	 * @return
	 */
	public int getNumberOfResults() {
		return getResults().size();
	}
	
	
}
