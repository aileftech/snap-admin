package tech.ailef.dbadmin.external.dto;

import java.util.Objects;

import tech.ailef.dbadmin.external.dbmapping.DbField;

public class QueryFilter {
	private DbField field;
	
	private CompareOperator op;
	
	private String value;
	
	public QueryFilter(DbField field, CompareOperator op, String value) {
		this.field = field;
		this.op = op;
		this.value = value;
	}

	public DbField getField() {
		return field;
	}

	public CompareOperator getOp() {
		return op;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (value != null && !value.toString().isBlank()) {
			String displayValue = value;
			if (value.length() > 10) {
				displayValue = value.substring(0, 4) + "..." + value.substring(value.length() - 4);
			}
			return "'" + field.getName() + "' " + op.getDisplayName() + " '" + displayValue + "'";
		} else {
			if (op != CompareOperator.STRING_EQ && op != CompareOperator.EQ) {
				return "'" + field.getName() + "' " + op.getDisplayName() + " NULL";
			} else {
				return "'" + field.getName() + "' IS NULL";
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, op, value);
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
		return Objects.equals(field, other.field) && op == other.op && Objects.equals(value, other.value);
	}
	

}
