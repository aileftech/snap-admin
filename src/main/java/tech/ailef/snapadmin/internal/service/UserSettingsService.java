/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
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

package tech.ailef.snapadmin.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import tech.ailef.snapadmin.internal.model.UserSetting;
import tech.ailef.snapadmin.internal.repository.UserSettingsRepository;

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
