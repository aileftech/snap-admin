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

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import tech.ailef.snapadmin.external.dto.CompareOperator;
import tech.ailef.snapadmin.external.exceptions.DbAdminException;

public class DateFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "date";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		try {
			LocalDate localDate = LocalDate.parse(value.toString());
			return Date.valueOf(localDate);
		} catch (DateTimeParseException e) {
			throw new DbAdminException("Invalid date " + value, e);
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