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


package tech.ailef.snapadmin.external.exceptions;

/**
 * Thrown during the computation of pagination if the requested
 * page number is not valid within the current request (e.g. it is greater
 * than the maximum available page). Used internally to redirect the
 * user to a default page. 
 */
public class InvalidPageException extends DbAdminException {
	private static final long serialVersionUID = -8891734807568233099L;
	
	public InvalidPageException() {
	}
	
	public InvalidPageException(String msg) {
		super(msg);
	}

}
