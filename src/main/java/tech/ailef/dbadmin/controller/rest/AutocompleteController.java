package tech.ailef.dbadmin.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.ailef.dbadmin.DbAdmin;
import tech.ailef.dbadmin.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.dto.AutocompleteSearchResult;

/**
 * API controller for autocomplete results
 */
@RestController
@RequestMapping("/dbadmin/api/autocomplete")
public class AutocompleteController {
	@Autowired
	private DbAdmin dbAdmin;
	
	@Autowired
	private DbAdminRepository repository;
	
	@GetMapping("/{className}")
	public ResponseEntity<?> autocomplete(@PathVariable String className, @RequestParam String query) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		List<AutocompleteSearchResult> search = repository.search(schema, query)
					.stream().map(x -> new AutocompleteSearchResult(x))
					.collect(Collectors.toList());
		
		return ResponseEntity.ok(search);
	}
}
