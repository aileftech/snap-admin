package tech.ailef.dbadmin.dto;

import java.util.Objects;

public class QueryFilter {
	private String field;
	
	private String op;
	
	private String value;
	
	public QueryFilter(String field, String op, String value) {
		this.field = field;
		this.op = op;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public String getOp() {
		return op;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, op, value);
	}

	@Override
	public String toString() {
		return field + " " + op + " '" + value + "'";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryFilter other = (QueryFilter) obj;
		return Objects.equals(field, other.field) && Objects.equals(op, other.op) && Objects.equals(value, other.value);
	}
	
	
}
