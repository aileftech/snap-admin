package tech.ailef.dbadmin.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.ailef.dbadmin.internal.model.UserSetting;
import tech.ailef.dbadmin.internal.repository.UserSettingsRepository;

@Component
public class UserConfiguration {
	@Autowired
	private UserSettingsRepository repo;
	
	public String get(String settingName) {
		Optional<UserSetting> setting = repo.findById(settingName);
		if (setting.isPresent())
			return setting.get().getSettingValue();
		return defaultValues().get(settingName);
	}
	
	private Map<String, String> defaultValues() {
		Map<String, String> values = new HashMap<>();
		values.put("brandName", "Spring Boot Database Admin");
		return values;
	}
}
