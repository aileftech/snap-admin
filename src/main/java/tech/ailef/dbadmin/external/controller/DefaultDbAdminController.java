package tech.ailef.dbadmin.external.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.DbAdminProperties;
import tech.ailef.dbadmin.external.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.external.dbmapping.DbObject;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.dto.PaginatedResult;
import tech.ailef.dbadmin.external.dto.QueryFilter;
import tech.ailef.dbadmin.external.exceptions.InvalidPageException;
import tech.ailef.dbadmin.external.misc.Utils;
import tech.ailef.dbadmin.internal.model.Action;
import tech.ailef.dbadmin.internal.repository.ActionRepository;

/**
 * The main DbAdmin controller that register most of the routes of the web interface.
 */
@Controller
@RequestMapping(value= {"/${dbadmin.baseUrl}", "/${dbadmin.baseUrl}/"})
public class DefaultDbAdminController {
	@Autowired
	private DbAdminProperties properties;
	
	@Autowired
	private DbAdminRepository repository;
	
	@Autowired
	private DbAdmin dbAdmin;
	
	@Autowired
	private ActionRepository repo;
	
	/**
	 * Home page with list of schemas
	 * @param model
	 * @param query
	 * @return
	 */
	@GetMapping
	@Transactional("internalTransactionManager")
	public String index(Model model, @RequestParam(required = false) String query) {
		Action a = new Action();
		a.setDescription("ciao");
//		a.setId(1);
//		entityManagerFactory.createEntityManager().persist(a);
//		entityManager.persist(a);
		Action save = repo.save(a);
		System.out.println(save);
		
//		repo.save(a);
//		displayAllBeans();
		
		List<DbObjectSchema> schemas = dbAdmin.getSchemas();
		if (query != null && !query.isBlank()) {
			schemas = schemas.stream().filter(s -> {
				return s.getClassName().toLowerCase().contains(query.toLowerCase())
					|| s.getTableName().toLowerCase().contains(query.toLowerCase());
			}).collect(Collectors.toList());
		}
		
		Map<String, Long> counts = 
			schemas.stream().collect(Collectors.toMap(s -> s.getClassName(), s -> repository.count(s)));
		
		model.addAttribute("schemas", schemas);
		model.addAttribute("query", query);
		model.addAttribute("counts", counts);
		model.addAttribute("activePage", "home");
		model.addAttribute("title", "Entities | Index");
		
		return "home";
	}
	
	/**
	 * Lists the items of a schema by applying a variety of filters:
	 *  - query: fuzzy search
	 *  - otherParams: filterable fields
	 * Includes pagination and sorting options.
	 *  
	 * @param model
	 * @param className
	 * @param page
	 * @param query
	 * @param pageSize
	 * @param sortKey
	 * @param sortOrder
	 * @param otherParams
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/model/{className}")
	public String list(Model model, @PathVariable String className,
			@RequestParam(required=false) Integer page, @RequestParam(required=false) String query,
			@RequestParam(required=false) Integer pageSize, @RequestParam(required=false) String sortKey, 
			@RequestParam(required=false) String sortOrder, @RequestParam MultiValueMap<String, String> otherParams,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		if (page == null) page = 1;
		if (pageSize == null) pageSize = 50;
		
		Set<QueryFilter> queryFilters = Utils.computeFilters(otherParams);
		if (otherParams.containsKey("remove_field")) {
			List<String> fields = otherParams.get("remove_field");
			
			for (int i = 0; i < fields.size(); i++) {
				QueryFilter toRemove = 
					new QueryFilter(
						fields.get(i), 
						CompareOperator.valueOf(otherParams.get("remove_op").get(i).toUpperCase()), 
						otherParams.get("remove_value").get(i)
					);
				queryFilters.removeIf(f -> f.equals(toRemove));
			}
			
			MultiValueMap<String, String> parameterMap = Utils.computeParams(queryFilters);
			
			MultiValueMap<String, String> filteredParams = new LinkedMultiValueMap<>();
 			request.getParameterMap().entrySet().stream()
				.filter(e -> !e.getKey().startsWith("remove_") && !e.getKey().startsWith("filter_"))
				.forEach(e -> {
					filteredParams.putIfAbsent(e.getKey(), new ArrayList<>());
					for (String v : e.getValue()) {
						if (filteredParams.get(e.getKey()).isEmpty()) {
							filteredParams.get(e.getKey()).add(v);
						} else {
							filteredParams.get(e.getKey()).set(0, v);
						}
					}
				});
 			
 			filteredParams.putAll(parameterMap);
 			String queryString = Utils.getQueryString(filteredParams);
			String redirectUrl = request.getServletPath() + queryString; 
			return "redirect:" + redirectUrl.trim();
		}
		
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		try {
			PaginatedResult result = null;
			if (query != null || !otherParams.isEmpty()) {
				result = repository.search(schema, query, page, pageSize, sortKey, sortOrder, queryFilters);
			} else {
				result = repository.findAll(schema, page, pageSize, sortKey, sortOrder);
			}
				
			model.addAttribute("title", "Entities | " + schema.getJavaClass().getSimpleName() + " | Index");
			model.addAttribute("page", result);
			model.addAttribute("schema", schema);
			model.addAttribute("activePage", "entities");
			model.addAttribute("sortKey", sortKey);
			model.addAttribute("query", query);
			model.addAttribute("sortOrder", sortOrder);
			model.addAttribute("activeFilters", queryFilters);
			return "model/list";
			
		} catch (InvalidPageException e) {
			return "redirect:/" + properties.getBaseUrl() + "/model/" + className;
		}
	}
	
	/**
	 * Displays information about the schema
	 * @param model
	 * @param className
	 * @return
	 */
	@GetMapping("/model/{className}/schema")
	public String schema(Model model, @PathVariable String className) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		model.addAttribute("activePage", "entities");
		model.addAttribute("schema", schema);
		
		return "model/schema";
	}
	
	/**
	 * Shows a single item
	 * @param model
	 * @param className
	 * @param id
	 * @return
	 */
	@GetMapping("/model/{className}/show/{id}")
	public String show(Model model, @PathVariable String className, @PathVariable String id) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		DbObject object = repository.findById(schema, id).orElseThrow(() -> {
			return new ResponseStatusException(
			  HttpStatus.NOT_FOUND, "Object " + className + " with id " + id + " not found"
			);
		});
		
		model.addAttribute("title", "Entities | " + schema.getJavaClass().getSimpleName() + " | " + object.getDisplayName());
		model.addAttribute("object", object);
		model.addAttribute("activePage", "entities");
		model.addAttribute("schema", schema);
		
		return "model/show";
	}
	
	
	@GetMapping("/model/{className}/create")
	public String create(Model model, @PathVariable String className) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		model.addAttribute("className", className);
		model.addAttribute("schema", schema);
		model.addAttribute("title", "Entities | " + schema.getJavaClass().getSimpleName() + " | Create");
		model.addAttribute("activePage", "entities");
		model.addAttribute("create", true);
		
		return "model/create";
	}
	
	@GetMapping("/model/{className}/edit/{id}")
	public String edit(Model model, @PathVariable String className, @PathVariable String id) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		DbObject object = repository.findById(schema, id).orElseThrow(() -> {
			return new ResponseStatusException(
			  HttpStatus.NOT_FOUND, "Object " + className + " with id " + id + " not found"
			);
		});
		
		model.addAttribute("title", "Entities | " + schema.getJavaClass().getSimpleName() + " | Edit | " + object.getDisplayName());
		model.addAttribute("className", className);
		model.addAttribute("object", object);
		model.addAttribute("schema", schema);
		model.addAttribute("activePage", "entities");
		model.addAttribute("create", false);
		
		return "model/create";
	}
	
	@PostMapping(value="/model/{className}/delete/{id}")
	/**
	 * Delete a single row based on its primary key value
	 * @param className
	 * @param id
	 * @param attr
	 * @return
	 */
	public String delete(@PathVariable String className, @PathVariable String id, RedirectAttributes attr) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		try {
			repository.delete(schema, id);
		} catch (DataIntegrityViolationException e) {
			attr.addFlashAttribute("errorTitle", "Unable to DELETE row");
			attr.addFlashAttribute("error", e.getMessage());
		}
		
		return "redirect:/" + properties.getBaseUrl() + "/model/" + className;
	}
	
	@PostMapping(value="/model/{className}/delete")
	/**
	 * Delete multiple rows based on their primary key values
	 * @param className
	 * @param ids
	 * @param attr
	 * @return
	 */
	public String delete(@PathVariable String className, @RequestParam String[] ids, RedirectAttributes attr) {
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		int countDeleted = 0;
		for (String id : ids) {
			try {
				repository.delete(schema, id);
				countDeleted += 1;
			} catch (DataIntegrityViolationException e) {
				attr.addFlashAttribute("error", e.getMessage());
			}
		}
		
		if (countDeleted > 0)
			attr.addFlashAttribute("message", "Deleted " + countDeleted + " of " + ids.length + " items");
		
		return "redirect:/" + properties.getBaseUrl() + "/model/" + className;
	}
	
	@PostMapping(value="/model/{className}/create")
	public String store(@PathVariable String className,
			@RequestParam MultiValueMap<String, String> formParams,
			@RequestParam Map<String, MultipartFile> files,
			RedirectAttributes attr) {
		// Extract all parameters that have exactly 1 value,
		// as these will be the raw values for the object that is being
		// created.
		// The remaining parmeters which have more than 1 value
		// are IDs in a many-to-many relationship and need to be
		// handled separately
		Map<String, String> params = new HashMap<>();
		for (String param : formParams.keySet()) {
			if (!param.endsWith("[]")) {
				params.put(param, formParams.getFirst(param));
			}
		}
		
		Map<String, List<String>> multiValuedParams = new HashMap<>();
		for (String param : formParams.keySet()) {
			if (param.endsWith("[]")) {
				List<String> list = formParams.get(param);
				// If the request contains only 1 parameter value, it's the empty
				// value that signifies just the presence of the field (e.g. the
				// user might've deleted all the value)
				if (list.size() == 1) {
					multiValuedParams.put(param, new ArrayList<>());
				} else {
					list.removeIf(f -> f.isBlank());
					multiValuedParams.put(
						param, 
						list
					);
				}
			}
		}
		
 		String c = params.get("__dbadmin_create");
		if (c == null) {
			throw new ResponseStatusException(
			  HttpStatus.INTERNAL_SERVER_ERROR, "Missing required param __dbadmin_create"
			);
		}
		
		boolean create = Boolean.parseBoolean(c);
		
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);

		String pkValue = params.get(schema.getPrimaryKey().getName());
		if (pkValue == null || pkValue.isBlank()) {
			pkValue = null;
		}
		
		if (pkValue == null) {
			try {
				Object newPrimaryKey = repository.create(schema, params, files, pkValue);
				repository.attachManyToMany(schema, newPrimaryKey, multiValuedParams);				
				pkValue = newPrimaryKey.toString();
				attr.addFlashAttribute("message", "Item created successfully.");
			} catch (DataIntegrityViolationException e) {
				attr.addFlashAttribute("errorTitle", "Unable to INSERT row");
				attr.addFlashAttribute("error", e.getMessage());
				attr.addFlashAttribute("params", params);
			} catch (UncategorizedSQLException e) {
				attr.addFlashAttribute("errorTitle", "Unable to INSERT row");
				attr.addFlashAttribute("error", e.getMessage());
				attr.addFlashAttribute("params", params);
			}
			
		} else {
			Optional<DbObject> object = repository.findById(schema, pkValue);
			
			if (!object.isEmpty()) {
				if (create) {
					attr.addFlashAttribute("errorTitle", "Unable to create item");
					attr.addFlashAttribute("error", "Item with id " + object.get().getPrimaryKeyValue() + " already exists.");
					attr.addFlashAttribute("params", params);
				} else {
					try {
						repository.update(schema, params, files);
						repository.attachManyToMany(schema, pkValue, multiValuedParams);
						attr.addFlashAttribute("message", "Item saved successfully.");
					} catch (DataIntegrityViolationException e) {
						attr.addFlashAttribute("errorTitle", "Unable to UPDATE row (no changes applied)");
						attr.addFlashAttribute("error", e.getMessage());
						attr.addFlashAttribute("params", params);
					} catch (IllegalArgumentException e) {
						attr.addFlashAttribute("errorTitle", "Unable to UPDATE row (no changes applied)");
						attr.addFlashAttribute("error", e.getMessage());
						attr.addFlashAttribute("params", params);
					}
				}
			} else {
				try {
					Object newPrimaryKey = repository.create(schema, params, files, pkValue);
					repository.attachManyToMany(schema, newPrimaryKey, multiValuedParams);
					attr.addFlashAttribute("message", "Item created successfully");
				} catch (DataIntegrityViolationException e) {
					attr.addFlashAttribute("errorTitle", "Unable to INSERT row (no changes applied)");
					attr.addFlashAttribute("error", e.getMessage());
					attr.addFlashAttribute("params", params);
				}
			}
		}

		if (attr.getFlashAttributes().containsKey("error")) {
			if (create)
				return "redirect:/" + properties.getBaseUrl() + "/model/" + schema.getClassName() + "/create";
			else
				return "redirect:/" + properties.getBaseUrl() + "/model/" + schema.getClassName() + "/edit/" + pkValue;
		} else {
			return "redirect:/" + properties.getBaseUrl() + "/model/" + schema.getClassName() + "/show/" + pkValue;
		}
	}
	
	
	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("activePage", "settings");
		return "settings";
	}
	

}
