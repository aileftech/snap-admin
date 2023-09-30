package tech.ailef.dbadmin.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.ailef.dbadmin.internal.model.UserSetting;
import tech.ailef.dbadmin.internal.repository.UserSettingsRepository;

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
		return defaultValues().getOrDefault(settingName, "");
	}

	/**
	 * Returns a map filled with the default values of the settings.
	 * @return
	 */
	private Map<String, String> defaultValues() {
		Map<String, String> values = new HashMap<>();
		values.put("brandName", "Spring Boot Database Admin");
		return values;
	}
}
