package tech.ailef.dbadmin.external.dbmapping.query;

import java.util.Objects;

import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.dbmapping.DbField;
import tech.ailef.dbadmin.external.dbmapping.DbFieldType;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;
import tech.ailef.dbadmin.external.exceptions.UnsupportedFieldTypeException;

public class DbQueryOutputField {
	private String name;
	
	private String table;

	private DbField dbField;
	
	private DbQueryResultRow result;
	
	public DbQueryOutputField(String name, String table, DbAdmin dbAdmin) {
		this.name = name;
		this.table = table;
		
		try {
			DbObjectSchema schema = dbAdmin.findSchemaByTableName(table);
			DbField dbField = schema.getFieldByName(name);
			this.dbField = dbField;
		} catch (DbAdminException e) {
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
				DbFieldType type = DbFieldType.fromClass(result.get(this).getClass());
				return type.toString();
			} catch (UnsupportedFieldTypeException e) {
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
