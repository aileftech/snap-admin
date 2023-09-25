package tech.ailef.dbadmin.external.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.DbAdminProperties;
import tech.ailef.dbadmin.external.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.dto.PaginatedResult;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

@RestController
@RequestMapping(value = {"/${dbadmin.baseUrl}/api", "/${dbadmin.baseUrl}/api/"})
public class DefaultDbAdminRestController {
	@Autowired
	public DbAdmin dbAdmin;
	
	@Autowired
	private DbAdminProperties properties;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	//	@Autowired
//	@Qualifier("internalJdbc")
//	private JdbcTemplate internalJdbc;
	
//	@GetMapping("/configuration")
//	public ResponseEntity<?> conf() {
//		return ResponseEntity.ok(properties.toMap());
//	}
	
	@GetMapping
	public ResponseEntity<?> index(@RequestParam(required = false) String query) {
		checkInit();
		
		List<DbObjectSchema> schemas = dbAdmin.getSchemas();
		if (query != null && !query.isBlank()) {
			schemas = schemas.stream().filter(s -> {
				return s.getClassName().toLowerCase().contains(query.toLowerCase())
					|| s.getTableName().toLowerCase().contains(query.toLowerCase());
			}).collect(Collectors.toList());
		}

		return ResponseEntity.ok(schemas);
	}
	
	@GetMapping("/model/{className}")
	public ResponseEntity<?> list(@PathVariable String className,
			@RequestParam(required=false) Integer page, @RequestParam(required=false) Integer pageSize,
			@RequestParam(required=false) String sortKey, @RequestParam(required=false) String sortOrder) {
		checkInit();
		DbAdminRepository repository = new DbAdminRepository(jdbcTemplate);

		if (page == null) page = 1;
		if (pageSize == null) pageSize = 50;
		
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		PaginatedResult result = repository.findAll(schema, page, pageSize, sortKey, sortOrder);
		

		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/model/{className}/schema")
	public ResponseEntity<?> schema(@PathVariable String className) {
		checkInit();
		
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		return ResponseEntity.ok(schema);
	}
	
//	@GetMapping("/model/{className}/show/{id}")
//	public ResponseEntity<?> show(@PathVariable String className, @PathVariable String id,
//			@RequestParam(required = false) Boolean expand) {
//		checkInit();
//		DbAdminRepository repository = new DbAdminRepository(jdbcTemplate);
//		if (expand == null) expand = true;
//		
//		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
//		
//		DbObject object = repository.findById(schema, id).orElseThrow(() -> {
//			return new ResponseStatusException(
//			  HttpStatus.NOT_FOUND, "Object " + className + " with id " + id + " not found"
//			);
//		});
//
//		return ResponseEntity.ok(new DbObjectDTO(object, expand));
//	}
	
	private void checkInit() {
		if (dbAdmin == null)
			throw new DbAdminException("Not initialized correctly: DB_ADMIN object is null.");
	}
}
