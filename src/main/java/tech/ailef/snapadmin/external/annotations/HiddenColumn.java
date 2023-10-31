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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Marks a column as hidden. This column and its values will not be shown in the
 * list and detail view for objects of this type. If the column is nullable, it
 * will be hidden in the create and edit forms as well (and this will result 
 * in the column always being NULL when creating/editing objects). If, instead,
 * it's not nullable column, it will be included in the create and edit forms
 * as it would otherwise prevent the creation of items.</p>
 * 
 * <p><strong>Please note that this is not meant as a security feature, </strong> but
 * rather to hide uninformative columns that clutter the interface. In fact, since
 * the create and edit form come pre-filled with all the information, these views
 * <b>will</b> show the value of the hidden column (if it's not nullable).
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HiddenColumn {
}