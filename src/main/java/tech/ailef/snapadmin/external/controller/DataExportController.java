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


package tech.ailef.snapadmin.external.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.dbmapping.DbFieldValue;
import tech.ailef.snapadmin.external.dbmapping.DbObject;
import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.snapadmin.external.dbmapping.SnapAdminRepository;
import tech.ailef.snapadmin.external.dbmapping.fields.DbField;
import tech.ailef.snapadmin.external.dbmapping.query.DbQueryResult;
import tech.ailef.snapadmin.external.dbmapping.query.DbQueryResultRow;
import tech.ailef.snapadmin.external.dto.DataExportFormat;
import tech.ailef.snapadmin.external.dto.QueryFilter;
import tech.ailef.snapadmin.external.exceptions.SnapAdminException;
import tech.ailef.snapadmin.external.exceptions.SnapAdminNotFoundException;
import tech.ailef.snapadmin.external.misc.Utils;
import tech.ailef.snapadmin.internal.model.ConsoleQuery;
import tech.ailef.snapadmin.internal.repository.ConsoleQueryRepository;

@Controller
@RequestMapping(value = { "/${snapadmin.baseUrl}/", "/${snapadmin.baseUrl}" })
public class DataExportController {
	private static final Logger logger = LoggerFactory.getLogger(DataExportFormat.class);
	
	@Autowired
	private SnapAdmin snapAdmin;

	@Autowired
	private SnapAdminRepository repository;
	
	@Autowired
	private ConsoleQueryRepository queryRepository;
	
	@Autowired
	private ObjectMapper mapper;

	@GetMapping("/console/export/{queryId}")
	public ResponseEntity<byte[]> export(@PathVariable String queryId, @RequestParam String format, 
			@RequestParam MultiValueMap<String, String> otherParams) {
		ConsoleQuery query = queryRepository.findById(queryId).orElseThrow(() -> new SnapAdminNotFoundException("Query not found: " + queryId));
		
		DataExportFormat exportFormat = null;
		try {
			exportFormat = DataExportFormat.valueOf(format.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new SnapAdminException("Unsupported export format: " + format);
		}
		
		List<String> fieldsToInclude = otherParams.getOrDefault("fields[]", new ArrayList<>());
		DbQueryResult results = repository.executeQuery(query.getSql());
		
		switch (exportFormat) {
		case CSV:
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"export_" + query.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".csv\"")
					.body(toCsvQuery(results, fieldsToInclude).getBytes());
		case XLSX:
			String sheetName = query.getTitle();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"export_" + query.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".xlsx\"")
					.body(toXlsxQuery(sheetName, results, fieldsToInclude));
		case JSONL:
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"export_" + query.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".jsonl\"")
					.body(toJsonlQuery(results, fieldsToInclude).getBytes());
		default:
			throw new SnapAdminException("Invalid DataExportFormat");
		}
	}
	
	@GetMapping("/export/{className}")
	@ResponseBody
	public ResponseEntity<byte[]> export(@PathVariable String className, @RequestParam(required = false) String query,
			@RequestParam String format, @RequestParam(required=false) Boolean raw, 
			@RequestParam MultiValueMap<String, String> otherParams) {
		if (raw == null) raw = false;
		
		DbObjectSchema schema = snapAdmin.findSchemaByClassName(className);
		
		if (!schema.isExportEnabled()) {
			throw new SnapAdminException("Export is not enabled for this table: " +  schema.getTableName());
		}
		
		List<String> fieldsToInclude = otherParams.getOrDefault("fields[]", new ArrayList<>());
		
		DataExportFormat exportFormat = null;
		try {
			exportFormat = DataExportFormat.valueOf(format.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new SnapAdminException("Unsupported export format: " + format);
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
		case JSONL:
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"export_" + schema.getJavaClass().getSimpleName() + ".jsonl\"")
					.body(toJsonl(results, fieldsToInclude, raw).getBytes());
		
		default:
			throw new SnapAdminException("Invalid DataExportFormat");
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
			throw new SnapAdminException("Error during serialization for XLSX workbook", e);
		}
		
		
		return fos.toByteArray();
	}
	
	private byte[] toXlsxQuery(String sheetName, DbQueryResult result, List<String> fields) {
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
		
		for (DbQueryResultRow item : result.getRows()) {
			Row row = sheet.createRow(rowIndex++);
			int cellIndex = 0;
			
			List<String> record = getRecord(item, fields);
			
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
			throw new SnapAdminException("Error during serialization for XLSX workbook", e);
		}
		
		
		return fos.toByteArray();
	}

	/**
	 * Converts a list of DbObjects to a string containing their JSONL representation.
	 * One item per line in JSON format.
	 * @param items	the items to be serialized
	 * @param fields	the fields to take from each item
	 * @param raw	whether to use raw values or not
	 * @return	a string containing the items serialized in JSONL format
	 */
	private String toJsonl(List<DbObject> items, List<String> fields, boolean raw) {
		if (items.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		
		for (DbObject item : items) {
			Map<String, Object> map = item.toMap(fields, raw);
			try {
				String json = mapper.writeValueAsString(map);
				sb.append(json);
			} catch (JsonProcessingException e) {
				throw new SnapAdminException(e);
			}
			
			sb.append("\n");
		}

		return sb.toString();
	}
	
	private String toJsonlQuery(DbQueryResult result, List<String> fields) {
		if (result.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		
		for (DbQueryResultRow item : result.getRows()) {
			Map<String, Object> map = item.toMap(fields);
			try {
				String json = mapper.writeValueAsString(map);
				sb.append(json);
			} catch (JsonProcessingException e) {
				throw new SnapAdminException(e);
			}
			
			sb.append("\n");
		}

		return sb.toString();
	
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
			throw new SnapAdminException("Error during creation of CSV file", e);
		}
	}
	
	private String toCsvQuery(DbQueryResult result, List<String> fields) {
		if (result.isEmpty())
			return "";

		StringWriter sw = new StringWriter();

		CSVFormat csvFormat = 
			CSVFormat.DEFAULT.builder()
					 .setHeader(fields.toArray(String[]::new))
					 .build();

		try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
			for (DbQueryResultRow item : result.getRows()) {
				printer.printRecord(getRecord(item, fields));
			}

			return sw.toString();
		} catch (IOException e) {
			throw new SnapAdminException("Error during creation of CSV file", e);
		}
	
	}
	
	private List<String> getRecord(DbQueryResultRow row, List<String> fields) {
		List<String> record = new ArrayList<>();
		
		for (String field : fields) {
			Object value = row.getFieldByName(field);
			record.add(value ==  null ? null : value.toString());
		}
		
		return record;
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
