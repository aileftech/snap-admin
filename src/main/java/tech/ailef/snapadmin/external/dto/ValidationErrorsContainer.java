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

package tech.ailef.snapadmin.external.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

public class ValidationErrorsContainer {
	private Map<String, List<ConstraintViolation<?>>> errors = new HashMap<>();
	
	public ValidationErrorsContainer(ConstraintViolationException e) {
		e.getConstraintViolations().forEach(c -> {
			errors.putIfAbsent(c.getPropertyPath().toString(), new ArrayList<>());
			errors.get(c.getPropertyPath().toString()).add(c);
		});
	}
	
	public List<ConstraintViolation<?>> forField(String name) {
		return errors.getOrDefault(name, new ArrayList<>());
	}
	
	public boolean hasErrors(String name) {
		return forField(name).size() > 0;
	}
	
	public boolean isEmpty() {
		return errors.isEmpty();
	}

	@Override
	public String toString() {
		return "ValidationErrorsContainer [errors=" + errors + "]";
	}
	
	
}
