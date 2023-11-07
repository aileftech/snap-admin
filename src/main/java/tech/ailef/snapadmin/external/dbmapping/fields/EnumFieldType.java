/* 
 * SnapAdmin - An automatically generated CRUD admin UI for Spring Boot apps
 * Copyright (C) 2023 Ailef (http://ailef.tech)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tech.ailef.snapadmin.external.dbmapping.fields;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EnumType;
import tech.ailef.snapadmin.external.dto.CompareOperator;
import tech.ailef.snapadmin.external.exceptions.SnapAdminException;

public class EnumFieldType extends DbFieldType {

	private EnumType type;
	
	private Class<?> klass;
	
	public EnumFieldType(Class<?> klass, EnumType type) {
		this.klass = klass;
		this.type = type;
	}
	
	@Override
	public String getFragmentName() {
		return "select";
	}
	
	@Override
	public List<?> getValues() {
		try {
			Method method = getJavaClass().getMethod("values");
			Object[] invoke = (Object[])method.invoke(null);
			return Arrays.stream(invoke).collect(Collectors.toList());
		} catch (NoSuchMethodException | SecurityException | InvocationTargetException 
				| IllegalAccessException | IllegalArgumentException e) {
			throw new SnapAdminException(e);
		}
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		
		try {
			Method valueOf = getJavaClass().getMethod("valueOf", String.class);
			return valueOf.invoke(null, value.toString());
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof IllegalArgumentException)
				throw new SnapAdminException("Invalid value " + value + " for enum type " + getJavaClass().getSimpleName());
			else
				throw new SnapAdminException(e);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
			throw new SnapAdminException(e);
		}
	}

	@Override
	public Class<?> getJavaClass() {
		return klass;
	}

	@Override
	public List<CompareOperator> getCompareOperators() {
		return List.of(CompareOperator.EQ);
	}
	
	public EnumType getType() {
		return type;
	}
}