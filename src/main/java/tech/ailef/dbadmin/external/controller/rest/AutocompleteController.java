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


package tech.ailef.dbadmin.external.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.dto.AutocompleteSearchResult;

/**
 * API controller for autocomplete results
 */
@RestController
@RequestMapping(value= {"/${dbadmin.baseUrl}/api/autocomplete", "/${dbadmin.baseUrl}/api/autocomplete/"})
public class AutocompleteController {
	@Autowired
	private DbAdmin dbAdmin;
	
	@Autowired
	private DbAdminRepository repository;
	
	/**
	 * Returns a list of entities from a given table that match an input query.
	 * @param className
	 * @param query
	 * @return
	 */
	@GetMapping("/{className}")
	public ResponseEntity<?> autocomplete(@PathVariable String className, @RequestParam String query) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		List<AutocompleteSearchResult> search = repository.search(schema, query)
					.stream().map(x -> new AutocompleteSearchResult(x))
					.collect(Collectors.toList());
		
		return ResponseEntity.ok(search);
	}
}
