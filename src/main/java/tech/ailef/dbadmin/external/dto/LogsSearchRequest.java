package tech.ailef.dbadmin.external.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class LogsSearchRequest {
	private String table;
	
	private String actionType;
	
	private String itemId;
	
	private int page;
	
	private int pageSize;
	
	private String sortKey;
	
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
	
}
