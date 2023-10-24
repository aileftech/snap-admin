package tech.ailef.dbadmin.external.dbmapping.fields;

import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class BooleanFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "text";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		return Boolean.parseBoolean(value.toString());
	}

	@Override
	public Class<?> getJavaClass() {
		return Boolean.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.EQ);
	}
}