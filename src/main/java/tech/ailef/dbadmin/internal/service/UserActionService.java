package tech.ailef.dbadmin.internal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.ailef.dbadmin.external.dto.PaginatedResult;
import tech.ailef.dbadmin.external.dto.PaginationInfo;
import tech.ailef.dbadmin.internal.model.UserAction;
import tech.ailef.dbadmin.internal.repository.ActionRepository;
import tech.ailef.dbadmin.internal.repository.CustomActionRepositoryImpl;

@Service
public class UserActionService {
	@Autowired
	private ActionRepository repo;
	
	@Autowired
	private CustomActionRepositoryImpl customRepo;
	
	@Transactional("internalTransactionManager")
	public UserAction save(UserAction a) {
		return repo.save(a);
	}
	
	public PaginatedResult<UserAction> findActions(String table, String actionType, String userId, PageRequest page) {
		long count = customRepo.countActions(table, actionType, userId);
		List<UserAction> actions = customRepo.findActions(table, actionType, userId, page);
		int maxPage = (int)(Math.ceil ((double)count / page.getPageSize()));
		
		return new PaginatedResult<>(
			new PaginationInfo(page.getPageNumber() + 1, maxPage, page.getPageSize(), count, null, null),
			actions
		);
	}
	
}
