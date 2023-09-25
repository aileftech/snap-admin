package tech.ailef.dbadmin.external.dbmapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DbFieldValue {
	private Object value;
	
	private DbField field;

	public DbFieldValue(Object value, DbField field) {
		this.value = value;
		this.field = field;
	}

	public Object getValue() {
		return value;
	}
	
	public String getFormattedValue() {
		if (value == null) return null;
		
		if (field.getFormat() == null) {
			return value.toString();
		} else {
			return String.format(field.getFormat(), value);
		}
	}

	public DbField getField() {
		return field;
	}
	
	@JsonIgnore
	public String getJavaName() {
		return field.getPrimitiveField().getName();
	}

	@Override
	public String toString() {
		return "DbFieldValue [value=" + value + ", field=" + field + "]";
	}
	

}
