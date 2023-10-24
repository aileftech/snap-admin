package tech.ailef.dbadmin.external.dbmapping.fields;

import java.time.LocalDateTime;
import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class LocalDateTimeFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "datetime";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		return LocalDateTime.parse(value.toString());
	}

	@Override
	public Class<?> getJavaClass() {
		return LocalDateTime.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
	}
}