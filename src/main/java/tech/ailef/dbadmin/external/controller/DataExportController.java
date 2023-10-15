package tech.ailef.dbadmin.external.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(DataExportFormat.class);
	
	@Autowired
	private DbAdmin dbAdmin;

	@Autowired
	private DbAdminRepository repository;

	@GetMapping("/{className}")
	@ResponseBody
	public ResponseEntity<byte[]> export(@PathVariable String className, @RequestParam(required = false) String query,
			@RequestParam String format, @RequestParam(required=false) Boolean raw, 
			@RequestParam MultiValueMap<String, String> otherParams) {
		if (raw == null) raw = false;
		
		DbObjectSchema schema = dbAdmin.findSchemaByClassName(className);
		List<String> fieldsToInclude = otherParams.getOrDefault("fields[]", new ArrayList<>());
		
		DataExportFormat exportFormat = null;
		try {
			exportFormat = DataExportFormat.valueOf(format.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new DbAdminException("Unsupported export format: " + format);
		}

		Set<QueryFilter> queryFilters = Utils.computeFilters(schema, otherParams);
		List<DbObject> results = repository.search(schema, query, queryFilters);

		switch (exportFormat) {
		case CSV:
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"export_" + schema.getJavaClass().getSimpleName() + ".csv\"")
					.body(toCsv(results, fieldsToInclude, raw).getBytes());
		case XLSX:
			String sheetName = schema.getJavaClass().getSimpleName();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"export_" + schema.getJavaClass().getSimpleName() + ".xlsx\"")
					.body(toXlsx(sheetName, results, fieldsToInclude, raw));
		default:
			throw new DbAdminException("Invalid DataExportFormat");
		}

	}

	private byte[] toXlsx(String sheetName, List<DbObject> items, List<String> fields, boolean raw) {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet(sheetName);

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		
		int rowIndex = 0;
		Row headerRow = sheet.createRow(rowIndex++);
		for (int i = 0; i < fields.size(); i++) {
			Cell headerCell = headerRow.createCell(i);
			headerCell.setCellValue(fields.get(i));
			headerCell.setCellStyle(headerStyle);
		}
		
		for (DbObject item : items) {
			Row row = sheet.createRow(rowIndex++);
			int cellIndex = 0;
			
			List<String> record = getRecord(item, fields, raw);
			
			for (String value : record) {
				Cell cell = row.createCell(cellIndex++);
				cell.setCellValue(value);
			}
		}

		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		try {
			workbook.write(fos);
			fos.close();
			workbook.close();
		} catch (IOException e) {
			throw new DbAdminException("Error during serialization for XLSX workbook", e);
		}
		
		
		return fos.toByteArray();
	}

	private String toCsv(List<DbObject> items, List<String> fields, boolean raw) {
		if (items.isEmpty())
			return "";

		StringWriter sw = new StringWriter();

		CSVFormat csvFormat = 
			CSVFormat.DEFAULT.builder()
					 .setHeader(fields.toArray(String[]::new))
					 .build();

		try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
			for (DbObject item : items) {
				printer.printRecord(getRecord(item, fields, raw));
			}

			return sw.toString();
		} catch (IOException e) {
			throw new DbAdminException("Error during creation of CSV file", e);
		}
	}
	
	/**
	 * Builds and returns a record (i.e a row) for a spreadsheet file (CSV or XLSX) as a list of Strings.
	 * Each column contains the value of a database column, potentially with some processing applied if
	 * the {@code raw} parameter is true.
	 * 
	 * @param item the object to create the record for
	 * @param fields	the fields to include (this might contain {@code ComputedColumn} fields)
	 * @param raw whether to export raw values or performing standard processing (foreign key resolution, formatting)
	 * @return a record for a spreadsheet file as a list of Strings
	 */
	private List<String> getRecord(DbObject item, List<String> fields, boolean raw) {
		List<String> record = new ArrayList<>();

		Set<String> dbFields = item.getSchema().getSortedFields().stream().map(f -> f.getName())
			.collect(Collectors.toSet());
		Set<String> computedFields = new HashSet<>(item.getSchema().getComputedColumnNames());
		
		for (String field : fields) {
			// Physical field
			if (dbFields.contains(field)) {
				DbField dbField = item.getSchema().getFieldByName(field);
				if (dbField.isForeignKey()) {
					DbObject linkedItem = item.traverse(dbField);
					
					if (linkedItem == null) record.add("");
					else {
						if (raw) {
							record.add(linkedItem.getPrimaryKeyValue().toString());
						} else {
							record.add(linkedItem.getPrimaryKeyValue() + " (" + linkedItem.getDisplayName() + ")");
						}
					}
				} else {
					if (raw) {
						DbFieldValue fieldValue = item.get(dbField);
						if (fieldValue.getValue() == null) record.add("");
						else record.add(fieldValue.getValue().toString());
					} else {
						
						record.add(item.get(dbField).getFormattedValue());
					}
					
				}
				
			}
			// Computed column field
			else if (computedFields.contains(field)) {
				Object computedValue = item.compute(field);
				record.add(computedValue.toString());
			}
			else {
				logger.info("Missing field `" + field + "` requested for export");
			}
		}
		
		return record;
	}

}
