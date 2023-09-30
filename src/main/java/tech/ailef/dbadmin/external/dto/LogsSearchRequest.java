package tech.ailef.dbadmin.external.dto;

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
	 * The requested page
	 */
	private int page;
	
	/**
	 * The requested page size
	 */
	private int pageSize;
	
	/**
	 * The requested sort key
	 */
	private String sortKey;
	
	/**
	 * The requested sort order
	 */
	private String sortOrder;

	public String getTable() {
		return table == null || table.isBlank() || table.equalsIgnoreCase("Any") ? null : table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getActionType() {
		return actionType == null || actionType.isBlank() || actionType.equalsIgnoreCase("Any") ? null : actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getItemId() {
		return itemId == null || itemId.isBlank() ? null : itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getSortOrder() {
		return sortOrder;
	}

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
