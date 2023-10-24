package tech.ailef.dbadmin.external.dbmapping.fields;
import java.time.LocalDate;
import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class LocalDateFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "date";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		return LocalDate.parse(value.toString());
	}

	@Override
	public Class<?> getJavaClass() {
		return Float.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
	}
}