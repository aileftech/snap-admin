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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * A client request for the Action logs page where
 * several filtering parameters are present
 *
 */
public class LogsSearchRequest implements FilterRequest {
	/**
	 * The table name to filter on
	 */
	private String table;
	
	/**
	 * The action type to filter on (EDIT, CREATE, DELETE, ANY)
	 */
	private String actionType;
	
	/**
	 * The item id to filter on.
	 */
	private String itemId;
	
	/**
	 * The requested page number
	 */
	private int page;
	
	/**
	 * The requested number of elements per page
	 */
	private int pageSize;
	
	/**
	 * The requested sort key, possibly null
	 */
	private String sortKey;
	
	/**
	 * The requested sort order, possibly null
	 */
	private String sortOrder;

	/**
	 * Returns the table specified in this search request. If the value is blank or 'Any',
	 * the method returns null in order ignore the field in subsequent queries.
	 * @return
	 */
	public String getTable() {
		return table == null || table.isBlank() || table.equalsIgnoreCase("Any") ? null : table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * Returns the actionType specified in this search request. If the value is blank or 'Any',
	 * the method returns null in order ignore the field in subsequent queries.
	 * @return
	 */
	public String getActionType() {
		return actionType == null || actionType.isBlank() || actionType.equalsIgnoreCase("Any") ? null : actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * Returns the itemId specified in this search request. If the value is blank or 'Any',
	 * the method returns null in order ignore the field in subsequent queries.
	 * @return
	 */
	public String getItemId() {
		return itemId == null || itemId.isBlank() ? null : itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * Returns the requested page number
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Sets the page for this request
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Returns the requested number of elements per page
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size for this request
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns the requested sort key, possibly null
	 * @return
	 */
	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * Returns the requested sort order, possibly null
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * Sets the sort order for this request
	 * 
	 * @param sortOrder
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "LogsSearchRequest [table=" + table + ", actionType=" + actionType + ", itemId=" + itemId + ", page="
				+ page + ", pageSize=" + pageSize + ", sortKey=" + sortKey + ", sortOrder=" + sortOrder + "]";
	}
	
	/**
	 * Build a Spring PageRequest object from the parameters in this request
	 * @return a Spring PageRequest object
	 */
	public PageRequest toPageRequest() {
		int actualPage = page - 1 < 0 ? 0 : page - 1;
		int actualPageSize = pageSize <= 0 ? 50 : pageSize;
		if (sortKey == null)
			return PageRequest.of(actualPage, actualPageSize);

		if (sortOrder == null) sortOrder = "ASC";
		
		if (sortOrder.equals("DESC")) {
			return PageRequest.of(actualPage, actualPageSize, Sort.by(sortKey).descending());
		} else {
			return PageRequest.of(actualPage, actualPageSize, Sort.by(sortKey).ascending());
		}
	}

	@Override
	public MultiValueMap<String, String> computeParams() {
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		if (table != null)
			params.add("table", table);
		if (itemId != null)
			params.add("itemId", itemId);
		if (actionType != null)
			params.add("actionType", actionType);
		
		return params;
	}
	
}
