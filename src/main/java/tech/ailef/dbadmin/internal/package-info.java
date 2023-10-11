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


/**
 * This is the root package for the "internal" data source, i.e. not the data source
 * that interacts with the user entities, but rather the one used internally by Spring
 * Boot Database Admin in order to save information. 
 * 
 * Due to the way Spring Boot component scanning works, it is needed to create this package and the
 * respective {@link tech.ailef.dbadmin.internal.InternalDbAdminConfiguration} in order to 
 * have the component scanning only pick the correct entities/repositories.
 */
package tech.ailef.dbadmin.internal;
