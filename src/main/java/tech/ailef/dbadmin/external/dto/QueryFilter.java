package tech.ailef.dbadmin.external.dto;

import java.util.Objects;

public class QueryFilter {
	private String field;
	
	private CompareOperator op;
	
	private String value;
	
	public QueryFilter(String field, CompareOperator op, String value) {
		this.field = field;
		this.op = op;
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public CompareOperator getOp() {
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
		String displayValue = value;
		if (value.length() > 10) {
			displayValue = value.substring(0, 4) + "..." + value.substring(value.length() - 4);
		}
		return "'" + field + "' " + op.getDisplayName() + " '" + displayValue + "'";
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
