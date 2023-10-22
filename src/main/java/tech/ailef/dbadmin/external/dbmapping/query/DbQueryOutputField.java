package tech.ailef.dbadmin.external.dbmapping.query;

import java.util.Objects;

import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.dbmapping.DbField;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public class DbQueryOutputField {
	private String name;
	
	private String table;

	private DbField dbField;
	
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

	public String getName() {
		return name;
	}
	
	public String getTable() {
		return table;
	}

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
	 * Returns the type of the field, only in the case the field
	 * has been mapped to a table
	 * @return
	 */
	public String getType() {
		if (dbField != null)
			return dbField.getType().toString();
		return "-";
	}
	
	public String getJavaName() {
		if (dbField == null) return null;
		return dbField.getJavaName();
	}
	
	public String getEntityClassName() {
		if (dbField == null) return null;
		return dbField.getSchema().getClassName();
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
