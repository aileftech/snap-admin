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

package tech.ailef.dbadmin.external.dto;


/**
 * Some fragments might need to be rendered differently depending
 * on their context. For example a TEXT field is usually rendered
 * as a text area, but if it has to fit in the faceted search right
 * bar it's rendered as a normal input type "text" field for space
 * reasons (and because the user just needs to search with a short
 * query).
 * 
 * This enum indicates the possible contexts and it is passed to the
 * getFragmentName() method which determines which actual fragment
 * to use.
 *
 */
public enum FragmentContext {
	DEFAULT,
	CREATE,
	SEARCH
}
