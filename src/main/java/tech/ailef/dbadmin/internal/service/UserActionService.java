package tech.ailef.dbadmin.internal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.ailef.dbadmin.external.dto.LogsSearchRequest;
import tech.ailef.dbadmin.external.dto.PaginatedResult;
import tech.ailef.dbadmin.external.dto.PaginationInfo;
import tech.ailef.dbadmin.internal.model.UserAction;
import tech.ailef.dbadmin.internal.repository.CustomActionRepositoryImpl;
import tech.ailef.dbadmin.internal.repository.UserActionRepository;

@Service
public class UserActionService {
	@Autowired
	private UserActionRepository repo;
	
	@Autowired
	private CustomActionRepositoryImpl customRepo;
	
	@Transactional("internalTransactionManager")
	public UserAction save(UserAction a) {
		return repo.save(a);
	}
	
	public PaginatedResult<UserAction> findActions(LogsSearchRequest request) {
		String table = request.getTable();
		String actionType = request.getActionType();
		String itemId = request.getItemId();
		PageRequest page = request.toPageRequest();
		
		long count = customRepo.countActions(table, actionType, itemId);
		List<UserAction> actions = customRepo.findActions(request);
		int maxPage = (int)(Math.ceil ((double)count / page.getPageSize()));
		
		return new PaginatedResult<>(
			new PaginationInfo(page.getPageNumber() + 1, maxPage, page.getPageSize(), count, null, request),
			actions
		);
	}
	
}
