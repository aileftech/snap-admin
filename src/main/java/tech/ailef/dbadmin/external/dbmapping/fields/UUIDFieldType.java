package tech.ailef.dbadmin.external.dbmapping.fields;

import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class UUIDFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "text";
	}

	@Override
	public Object parseValue(Object value) {
		return java.util.UUID.fromString(value.toString());
	}

	@Override
	public Class<?> getJavaClass() {
		return java.util.UUID.class;
	}

	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.STRING_EQ, CompareOperator.CONTAINS);
	}
}
