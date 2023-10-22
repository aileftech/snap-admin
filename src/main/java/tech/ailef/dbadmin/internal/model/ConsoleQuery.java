package tech.ailef.dbadmin.internal.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class ConsoleQuery {
	@Id
	@UuidGenerator
	private String id;
	
	@Lob
	private String sql;
	
	private String title;

	public ConsoleQuery() {
		this.title = "Untitled Query";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
