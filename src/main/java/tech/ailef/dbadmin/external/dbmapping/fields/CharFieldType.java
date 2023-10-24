package tech.ailef.dbadmin.external.dbmapping.fields;

import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class CharFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "char";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		if (value.toString().isBlank()) return null;
		return value.toString().charAt(0);
	}

	@Override
	public Class<?> getJavaClass() {
		return char.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.STRING_EQ);
	}
}