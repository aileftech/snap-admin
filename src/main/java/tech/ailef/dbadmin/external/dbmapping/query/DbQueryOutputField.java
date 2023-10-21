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
	
	public boolean isForeignKey() {
		return dbField != null && dbField.isForeignKey();
	}
	
	public boolean isBinary() {
		return dbField != null && dbField.isBinary();
	}
	
	public String getType() {
		return "TODO TYPE";
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
