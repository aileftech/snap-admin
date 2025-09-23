/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 

 */

package tech.ailef.snapadmin.external.dbmapping.fields;

import java.util.List;

import tech.ailef.snapadmin.external.dto.CompareOperator;

public class UUIDFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "text";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null) return null;
		String str = value.toString().trim();
		if (str.isEmpty()) return null;
		return java.util.UUID.fromString(str);
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
