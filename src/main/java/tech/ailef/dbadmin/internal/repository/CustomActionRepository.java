package tech.ailef.dbadmin.internal.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import tech.ailef.dbadmin.internal.model.UserAction;

public interface CustomActionRepository {
	public List<UserAction> findActions(String table, String actionType, String itemId, PageRequest pageRequest);
	
	public long countActions(String table, String actionType, String itemId);

}
