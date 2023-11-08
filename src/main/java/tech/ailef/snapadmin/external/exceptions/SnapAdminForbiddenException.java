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


package tech.ailef.snapadmin.external.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SnapAdminForbiddenException extends ResponseStatusException {
	private static final long serialVersionUID = 4090093290330473479L;

	public SnapAdminForbiddenException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
	
	@Override
	public String getMessage() {
		return getReason();
	}

}
