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

import java.util.List;

import jakarta.persistence.ManyToMany;
import tech.ailef.snapadmin.external.dto.CompareOperator;
import tech.ailef.snapadmin.external.exceptions.DbAdminException;

public class ManyToManyFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object parseValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getJavaClass() {
		return ManyToMany.class;
	}
	
	@Override
	public boolean isRelationship() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Many to Many";
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		throw new DbAdminException();
	}
}
