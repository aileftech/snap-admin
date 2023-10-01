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

import java.time.LocalDateTime;

import org.springframework.format.datetime.standard.DateTimeFormatterFactory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

/**
 * An action executed by any user from the web UI. 
 *
 */
@Entity
public class UserAction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Lob
	@Column(nullable = false)
	private String sql;
	
	@Column(nullable = false)
	private String javaClass;
	
	@Column(nullable = false)
	private String onTable;
	
	@Column(nullable = false)
	private String primaryKey;

	@Column(nullable = false)
	private String actionType;
	
	public UserAction() {
	}
	
	public UserAction(String onTable, String primaryKey, String actionType, String javaClass) {
		this.createdAt = LocalDateTime.now();
		this.sql = "SQL TODO";
		this.javaClass = javaClass;
		this.onTable = onTable;
		this.actionType = actionType;
		this.primaryKey = primaryKey;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getOnTable() {
		return onTable;
	}

	public void setOnTable(String onTable) {
		this.onTable = onTable;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getFormattedDate() {
		return new DateTimeFormatterFactory("YYYY-MM-dd HH:mm:ss").createDateTimeFormatter().format(createdAt);
	}
	
	public String getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}
}
