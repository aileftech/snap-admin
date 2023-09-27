package tech.ailef.dbadmin.external.dbmapping;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tech.ailef.dbadmin.external.annotations.DisplayImage;
import tech.ailef.dbadmin.external.annotations.Filterable;
import tech.ailef.dbadmin.external.annotations.FilterableType;

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
	
	public boolean isImage() {
		return field.getAnnotation(DisplayImage.class) != null;
	}
	
	public String getFormat() {
		return format;
	}
	
	public boolean isText() {
		return type == DbFieldType.TEXT;
	}
	
	public boolean isFilterable() {
		return getPrimitiveField().getAnnotation(Filterable.class) != null;
	}
	
	public boolean isFilterableCategorical() {
		Filterable filterable = getPrimitiveField().getAnnotation(Filterable.class);
		return filterable != null && filterable.type() == FilterableType.CATEGORICAL;
	}
	
	public Set<DbFieldValue> getAllValues() {
		List findAll = schema.getJpaRepository().findAll();
		Set<DbFieldValue> allValues = new HashSet<>();
		for (Object o : findAll) {
			DbFieldValue val = new DbObject(o, schema).get(this);
			allValues.add(val);
		}
		return allValues;
	}
	
	@Override
	public String toString() {
		return "DbField [name=" + dbName + ", javaName=" + javaName + ", type=" + type + ", field=" + field
				+ ", connectedType=" + connectedType + ", primaryKey=" + primaryKey + ", nullable=" + nullable
				+ ", schema=" + schema.getClassName() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(dbName, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbField other = (DbField) obj;
		return Objects.equals(dbName, other.dbName) && type == other.type;
	}

	

}
