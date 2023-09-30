package tech.ailef.dbadmin.internal.repository;

import java.util.List;

import tech.ailef.dbadmin.external.dto.LogsSearchRequest;
import tech.ailef.dbadmin.internal.model.UserAction;

public interface CustomActionRepository {
	public List<UserAction> findActions(LogsSearchRequest r);
	
	public long countActions(String table, String actionType, String itemId);

}
