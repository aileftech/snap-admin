/* 
 * Spring Boot Database Admin - An automatically generated CRUD admin UI for Spring Boot apps
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

package tech.ailef.dbadmin.external.dbmapping.fields;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public class ByteArrayFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		return "file";
	}

	@Override
	public Object parseValue(Object value) {
		if (value == null || value.toString().isBlank()) return null;
		try {
			return ((MultipartFile)value).getBytes();
		} catch (IOException e) {
			throw new DbAdminException(e);
		}
	}

	@Override
	public Class<?> getJavaClass() {
		return byte[].class;
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		throw new DbAdminException("Binary fields are not comparable");
	}
}
