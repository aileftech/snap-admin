package tech.ailef.dbadmin.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import tech.ailef.dbadmin.internal.model.UserSetting;
import tech.ailef.dbadmin.internal.repository.UserSettingsRepository;

@Service
public class UserSettingsService {
	@Autowired
	private TransactionTemplate internalTransactionTemplate;
	
	@Autowired
	private UserSettingsRepository repo;
	
	public UserSetting save(UserSetting q) {
		return internalTransactionTemplate.execute((status) -> {
			return repo.save(q);
		});
	
	}
}
