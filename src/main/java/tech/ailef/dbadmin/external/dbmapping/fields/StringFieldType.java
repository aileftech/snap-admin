package tech.ailef.dbadmin.external.dbmapping.fields;
import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class StringFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "text";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		return value.toString();
	}

	@Override
	public Class<?> getJavaClass() {
		return String.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.CONTAINS, CompareOperator.STRING_EQ);
	}
}
