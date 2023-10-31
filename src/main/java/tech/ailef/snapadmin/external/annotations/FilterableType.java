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


package tech.ailef.snapadmin.external.annotations;

/**
 * Type of filters that can be used in the faceted search.
 * 
 */
public enum FilterableType {
	/**
	 * The default filter provides a list of standard operators
	 * customized to the field type (e.g. greater than/less than/equals for numbers,
	 * after/before/equals for dates, contains/equals for strings, etc...), with,
	 * if applicable, an autocomplete form if the field references a foreign key.
	 */
	DEFAULT, 
	/**
	 * The categorical filter provides the full list of possible values
	 * for the field, rendered as a list of clickable items (that will
	 * filter for equality). This provides a better UX if the field can take
	 * a limited number of values and it's more convenient to have them all
	 * on screen rather than typing them.
	 */
	CATEGORICAL;

}
