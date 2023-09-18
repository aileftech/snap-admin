package tech.ailef.dbadmin.dbmapping;

import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DbField {
	protected String dbName;

	protected String javaName;
	
	protected DbFieldType type;
	
	@JsonIgnore
	protected Field field;
	
	/**
	 * If this field is a foreign key, the class of the
	 * entity that is connected to it
	 */
	@JsonIgnore
	private Class<?> connectedType;
	
	private boolean primaryKey;
	
	private boolean nullable;
	
	private String format;
	
	@JsonIgnore
	private DbObjectSchema schema;
	
	public DbField(String javaName, String name, Field field, DbFieldType type, DbObjectSchema schema, String format) {
		this.javaName = javaName;
		this.dbName = name;
		this.schema = schema;
		this.field = field;
		this.type = type;
		this.format = format;
	}
	
	public String getJavaName() {
		return javaName;
	}
	
	public DbObjectSchema getSchema() {
		return schema;
	}
	
	public DbObjectSchema getConnectedSchema() {
		if (connectedType == null) return null;
		return schema.getDbAdmin().findSchemaByClass(connectedType);
	}
	
	public void setSchema(DbObjectSchema schema) {
		this.schema = schema;
	}
	
	@JsonIgnore
	public Field getPrimitiveField() {
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return dbName;
	}

	public void setName(String name) {
		this.dbName = name;
	}
	
	public DbFieldType getType() {
		return type;
	}
	
	public void setType(DbFieldType type) {
		this.type = type;
	}
	
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	
	public Class<?> getConnectedType() {
		return connectedType;
	}
	
	public void setConnectedType(Class<?> connectedType) {
		this.connectedType = connectedType;
	}

	public boolean isForeignKey() {
		return connectedType != null;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isBinary() {
		return type == DbFieldType.BYTE_ARRAY;
	}
	
	public String getFormat() {
		return format;
	}
	
	@Override
	public String toString() {
		return "DbField [name=" + dbName + ", javaName=" + javaName + ", type=" + type + ", field=" + field
				+ ", connectedType=" + connectedType + ", primaryKey=" + primaryKey + ", nullable=" + nullable
				+ ", schema=" + schema.getClassName() + "]";
	}

	

}
