package tech.ailef.dbadmin.external.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import tech.ailef.dbadmin.external.dbmapping.DbFieldValue;
import tech.ailef.dbadmin.external.dbmapping.DbObject;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.external.dto.DataExportFormat;
import tech.ailef.dbadmin.external.dto.QueryFilter;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;
import tech.ailef.dbadmin.external.misc.Utils;

@Controller
@RequestMapping(value = { "/${dbadmin.baseUrl}/export", "/${dbadmin.baseUrl}/export/" })
public class DataExportController {

	@Autowired
	private DbAdmin dbAdmin;

	@Autowired
	private DbAdminRepository repository;

	@GetMapping("/{className}")
	@ResponseBody
	public ResponseEntity<byte[]> export(@PathVariable String className, @RequestParam(required = false) String query,
			@RequestParam String format, @RequestParam(required=false) Boolean raw, 
			@RequestParam MultiValueMap<String, String> otherParams) {
		if (format == null)
			format = "CSV";
		DataExportFormat exportFormat = null;
		try {
			exportFormat = DataExportFormat.valueOf(format.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new DbAdminException("Unsupported export format: " + format);
		}

		List<String> fieldToInclude = otherParams.getOrDefault("fields[]", new ArrayList<>());

		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);

		List<DbField> fields = schema.getSortedFields().stream().filter(f -> fieldToInclude.contains(f.getName()))
				.collect(Collectors.toList());

		Set<QueryFilter> queryFilters = Utils.computeFilters(schema, otherParams);

		List<DbObject> results = repository.search(schema, query, queryFilters);

		if (raw == null) raw = false;
		
		switch (exportFormat) {
		case CSV:
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"export_" + schema.getJavaClass().getSimpleName() + ".csv\"")
					.body(toCsv(results, fields, raw).getBytes());
		case XLSX:
			String sheetName = schema.getJavaClass().getSimpleName();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"export_" + schema.getJavaClass().getSimpleName() + ".xlsx\"")
					.body(toXlsx(sheetName, results, fields, raw));
		default:
			throw new DbAdminException("Unable to detect export format");
		}

	}

	private byte[] toXlsx(String sheetName, List<DbObject> items, List<DbField> fields, boolean raw) {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet(sheetName);

		int rowIndex = 0;
		for (DbObject item : items) {
			Row row = sheet.createRow(rowIndex++);
			int cellIndex = 0;
			for (DbField field : fields) {
				Cell cell = row.createCell(cellIndex++);
				
				if (raw) {
					if (field.isForeignKey()) {
						String cellValue = "";
						DbObject traverse = item.traverse(field);
						if (traverse != null) cellValue = traverse.getPrimaryKeyValue().toString();
						cell.setCellValue(cellValue);
					} else {
						String cellValue = "";
						DbFieldValue fieldValue = item.get(field);
						if (fieldValue.getValue() != null) cellValue = fieldValue.getValue().toString();
						cell.setCellValue(cellValue);
					}
				} else {
					if (field.isForeignKey()) {
						DbObject linkedItem = item.traverse(field);
						cell.setCellValue(linkedItem.getPrimaryKeyValue() + " (" + linkedItem.getDisplayName() + ")");
					} else {
						cell.setCellValue(item.get(field).getFormattedValue());
					}
				}
			}
		}
		// lets write the excel data to file now
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		try {
			workbook.write(fos);
			fos.close();
			workbook.close();
		} catch (IOException e) {
			throw new DbAdminException("Error writing XLSX file");
		}
		
		
		return fos.toByteArray();
	}

	private String toCsv(List<DbObject> items, List<DbField> fields, boolean raw) {
		if (items.isEmpty())
			return "";

		StringWriter sw = new StringWriter();

		String[] header = fields.stream().map(f -> f.getName()).toArray(String[]::new);

		CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(header).build();

		try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
			for (DbObject item : items) {
				printer.printRecord(fields.stream().map(f -> {
					if (raw) {
						if (f.isForeignKey()) {
							DbObject traverse = item.traverse(f);
							if (traverse == null) return "";
							else return traverse.getPrimaryKeyValue().toString();
						} else {
							DbFieldValue fieldValue = item.get(f);
							if (fieldValue.getValue() == null) return "";
							else return fieldValue.getValue().toString();
						}
						
					} else {
						if (f.isForeignKey()) {
							DbObject linkedItem = item.traverse(f);
							return linkedItem.getPrimaryKeyValue() + " (" + linkedItem.getDisplayName() + ")";
						} else {
							return item.get(f).getFormattedValue();
						}
					}
				}));
			}

			return sw.toString();
		} catch (IOException e) {
			throw new DbAdminException(e);
		}
	}

}
