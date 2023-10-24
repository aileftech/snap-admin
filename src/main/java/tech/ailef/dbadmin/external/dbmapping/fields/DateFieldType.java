package tech.ailef.dbadmin.external.dbmapping.fields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.ailef.dbadmin.external.dto.CompareOperator;

public class DateFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "date";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		try {
			return format.parse(value.toString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> getJavaClass() {
		return Date.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.AFTER, CompareOperator.STRING_EQ, CompareOperator.BEFORE);
	}
}