package tech.ailef.dbadmin.internal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserSetting {
	@Id
	private String id;
	
	private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
