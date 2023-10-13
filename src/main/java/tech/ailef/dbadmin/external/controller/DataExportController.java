package tech.ailef.dbadmin.external.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.dbmapping.DbAdminRepository;
import tech.ailef.dbadmin.external.dbmapping.DbField;
import tech.ailef.dbadmin.external.dbmapping.DbObject;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.dto.QueryFilter;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;
import tech.ailef.dbadmin.external.misc.Utils;

@Controller
@RequestMapping(value= {"/${dbadmin.baseUrl}/export", "/${dbadmin.baseUrl}/export/"})
public class DataExportController {
	
	@Autowired
	private DbAdmin dbAdmin;
	
	@Autowired
	private DbAdminRepository repository;
	
	@GetMapping("/{className}")
	@ResponseBody
	public ResponseEntity<byte[]> export(@PathVariable String className,
			@RequestParam(required=false) String query,
			@RequestParam MultiValueMap<String, String> otherParams) {
		
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		
		Set<QueryFilter> queryFilters = Utils.computeFilters(schema, otherParams);
		
		List<DbObject> results = repository.search(schema, query, queryFilters);

		String result = toCsv(results, schema.getSortedFields());
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"export_" + schema.getJavaClass().getSimpleName() + ".csv\"")
				.body(result.getBytes());
	}
	
	private String toCsv(List<DbObject> items, List<DbField> fields) {
		if (items.isEmpty()) return "";
		
		StringWriter sw = new StringWriter();

		String[] header = fields.stream().map(f -> f.getName()).toArray(String[]::new);
		
	    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
	        .setHeader(header)
	        .build();
	    
	    try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
	        for (DbObject item : items) {
	        	printer.printRecord(fields.stream().map(f -> {
	        		if (f.isForeignKey()) {
	        			DbObject linkedItem = item.traverse(f);
	        			return linkedItem.getPrimaryKeyValue() + " (" + linkedItem.getDisplayName() + ")";
	        		} else {
	        			return item.get(f).getFormattedValue();
	        		}
	        	}));
	        }
	        
	        return sw.toString();
	    } catch (IOException e) {
	    	throw new DbAdminException(e);
		}
	}
	
}
