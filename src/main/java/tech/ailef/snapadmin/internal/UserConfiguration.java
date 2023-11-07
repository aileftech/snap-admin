/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
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


package tech.ailef.snapadmin.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.ailef.snapadmin.external.exceptions.SnapAdminException;
import tech.ailef.snapadmin.internal.model.UserSetting;
import tech.ailef.snapadmin.internal.repository.UserSettingsRepository;

/**
 * Wrapper class for the UserSettingsRepository that provides a better
 * way of handling user settings. 
 */
@Component
public class UserConfiguration {
	@Autowired
	private UserSettingsRepository repo;
	
	/**
	 * Returns the value of the specific setting
	 * @param settingName	the name of the setting
	 * @return	the value, if found, otherwise the default value if present, otherwise an empty string
	 */
	public String get(String settingName) {
		Optional<UserSetting> setting = repo.findById(settingName);
		if (setting.isPresent())
			return setting.get().getSettingValue();
		String settingDefaultValue = defaultValues().get(settingName);
		
		if (settingDefaultValue == null)
			throw new SnapAdminException("Trying to access setting `" + settingName + "` but it has no default value");
		
		return settingDefaultValue;
	}

	/**
	 * Returns a map filled with the default values of the settings.
	 * @return
	 */
	private Map<String, String> defaultValues() {
		Map<String, String> values = new HashMap<>();
		values.put("brandName", "SnapAdmin");
		values.put("additionalCss", "");
		return values;
	}
}
