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


package tech.ailef.snapadmin.external.dbmapping.query;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.snapadmin.external.dbmapping.fields.DbField;
import tech.ailef.snapadmin.external.dbmapping.fields.DbFieldType;
import tech.ailef.snapadmin.external.exceptions.SnapAdminException;
import tech.ailef.snapadmin.external.exceptions.UnsupportedFieldTypeException;

/*
 * A class that holds output fields from a user-provided SQL query
 * run in the SQL console. If possible, this field is mapped to a proper
 * {@link Dbfield} object, otherwise it is left as a raw object.
 */
public class DbQueryOutputField {
	private String name;
	
	private String table;

	private DbField dbField;
	
	private DbQueryResultRow result;
	
	public DbQueryOutputField(String name, String table, SnapAdmin snapAdmin) {
		this.name = name;
		this.table = table;
		
		try {
			DbObjectSchema schema = snapAdmin.findSchemaByTableName(table);
			DbField dbField = schema.getFieldByName(name);
			this.dbField = dbField;
		} catch (SnapAdminException e) {
			// We were unable to map this result column to a table, this happens
			// for example with COUNT(*) results and similar. We ignore this
			// as the dbField will be null and handled as such in the rest of the code
		}
	}

	/**
	 * Returns the column name of the field
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the table name of the field
	 * @return
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Returns true if this field is a primary key
	 * @return
	 */
	public boolean isPrimaryKey() {
		return dbField != null && dbField.isPrimaryKey();
	}

	/**
	 * Returns true if this field is a foreign key, only in the case
	 * the field has been mapped to a table
	 */
	public boolean isForeignKey() {
		return dbField != null && dbField.isForeignKey();
	}

	public boolean isExportable() {
		if (dbField == null) return true;
		return dbField.isExportable();
	}
	
	public Class<?> getConnectedType() {
		if (dbField == null) return null;
		return dbField.getConnectedType();
	}

	/**
	 * Returns true if this field is a binary field (BLOB, etc.), only in the case
	 * the field has been mapped to a table
	 * @return
	 */
	public boolean isBinary() {
		return dbField != null && dbField.isBinary();
	}
	
	/**
	 * Returns true if this field is mapped to a table
	 * @return
	 */
	public boolean isMapped() {
		return dbField != null;
	}
	
	/**
	 * Returns the type of the field.
	 * If the field has been mapped to a table column returns the
	 * type of the column, otherwise tries to parse the field 
	 * field type from the raw value returned by the database.
	 * @return
	 */
	public String getType() {
		// If the field has been mapped to the database
		if (dbField != null)
			return dbField.getType().toString();
		
		// If the row this fields belongs to is defined
		if (result != null) {
			try {
				DbFieldType type = DbFieldType.fromClass(result.get(this).getClass()).getConstructor().newInstance();
				return type.toString();
			} catch (UnsupportedFieldTypeException | InstantiationException | IllegalAccessException | 
					IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				return "-";
			}
		}
		
		return "-";
	}
	
	/**
	 * Returns the Java name of the field, if mapped to a table column
	 * @return
	 */
	public String getJavaName() {
		if (dbField == null) return null;
		return dbField.getJavaName();
	}
	
	/**
	 * Returns the Java class of the field, if mapped to a table column
	 * @return
	 */
	public String getEntityClassName() {
		if (dbField == null) return null;
		return dbField.getSchema().getClassName();
	}

	/**
	 * Sets the row object this field belongs to 
	 * @param result
	 */
	public void setResult(DbQueryResultRow result) {
		this.result = result;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, table);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbQueryOutputField other = (DbQueryOutputField) obj;
		return Objects.equals(name, other.name) && Objects.equals(table, other.table);
	}
	
	
}
