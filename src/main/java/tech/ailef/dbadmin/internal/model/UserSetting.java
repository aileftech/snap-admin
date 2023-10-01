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


package tech.ailef.dbadmin.internal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * A single variable in the user settings.
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
