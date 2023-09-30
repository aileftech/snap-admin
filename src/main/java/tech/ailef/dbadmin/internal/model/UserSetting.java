package tech.ailef.dbadmin.internal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * A single variable in the settings.
 */
@Entity
public class UserSetting {
	/**
	 * The id of the variable (its name)
	 */
	@Id
	private String id;
	
	/**
	 * The value of the variable
	 */
	private String settingValue;
	
	public UserSetting() {
	}
	
	public UserSetting(String id, String settingValue) {
		this.id = id;
		this.settingValue = settingValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSettingValue() {
		return settingValue;
	}
	
	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}
	
}
