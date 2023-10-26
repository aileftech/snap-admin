package tech.ailef.dbadmin.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import tech.ailef.dbadmin.internal.model.ConsoleQuery;
import tech.ailef.dbadmin.internal.repository.ConsoleQueryRepository;

@Service
public class ConsoleQueryService {
	@Autowired
	private TransactionTemplate internalTransactionTemplate;
	
	@Autowired
	private ConsoleQueryRepository repo;
	
	public ConsoleQuery save(ConsoleQuery q) {
		return internalTransactionTemplate.execute((status) -> {
			return repo.save(q);
		});
	
	}
}
