package tech.ailef.dbadmin.internal.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.ailef.dbadmin.internal.model.UserAction;

@Repository
public interface ActionRepository extends JpaRepository<UserAction, Integer>, CustomActionRepository {
	public List<UserAction> findAllByOnTableAndActionTypeAndPrimaryKey(String table, String actionType, String primaryKey, PageRequest pageRequest);
	
}
