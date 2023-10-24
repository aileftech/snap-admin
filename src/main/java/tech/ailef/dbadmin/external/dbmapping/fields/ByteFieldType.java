package tech.ailef.dbadmin.external.dbmapping.fields;

import java.util.List;

import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public class ByteFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "number";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		return value.toString().getBytes()[0];
	}

	@Override
	public Class<?> getJavaClass() {
		return byte.class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		throw new DbAdminException("Binary fields are not comparable");
	}
}