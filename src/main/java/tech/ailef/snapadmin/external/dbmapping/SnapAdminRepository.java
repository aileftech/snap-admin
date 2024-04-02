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


package tech.ailef.snapadmin.external.dbmapping;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.annotations.ReadOnly;
import tech.ailef.snapadmin.external.dbmapping.fields.DbField;
import tech.ailef.snapadmin.external.dbmapping.query.DbQueryOutputField;
import tech.ailef.snapadmin.external.dbmapping.query.DbQueryResult;
import tech.ailef.snapadmin.external.dbmapping.query.DbQueryResultRow;
import tech.ailef.snapadmin.external.dto.FacetedSearchRequest;
import tech.ailef.snapadmin.external.dto.PaginatedResult;
import tech.ailef.snapadmin.external.dto.PaginationInfo;
import tech.ailef.snapadmin.external.dto.QueryFilter;
import tech.ailef.snapadmin.external.exceptions.InvalidPageException;
import tech.ailef.snapadmin.external.exceptions.SnapAdminException;

/**
 * Implements the basic CRUD operations (and some more)
 */
@Component
public class SnapAdminRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SnapAdmin snapAdmin;
	
	public SnapAdminRepository() {
	}

	/**
	 * Find an object by ID
	 * @param schema	the schema where to look
	 * @param id	the primary key value
	 * @return	an optional with the object with the specified primary key value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<DbObject> findById(DbObjectSchema schema, Object id) {
		SimpleJpaRepository repository = schema.getJpaRepository();
		
		Optional optional = repository.findById(id);
		if (optional.isEmpty())
			return Optional.empty();
		else {
			DbObject obj = new DbObject(optional.get(), schema);
			return Optional.of(obj);
		}
	}

	public long count(DbObjectSchema schema) {
		return schema.getJpaRepository().count();
	}
	
	/**
	 * Counts the elements that match the fuzzy search
	 * @param schema
	 * @param query
	 * @return
	 */
	public long count(DbObjectSchema schema, String query, Set<QueryFilter> queryFilters) {
		return schema.getJpaRepository().count(query, queryFilters);
	}

	public List<DbObject> search(DbObjectSchema schema, String query, Set<QueryFilter> queryFilters) {
		CustomJpaRepository jpaRepository = schema.getJpaRepository();
        
		return jpaRepository.search(query, queryFilters).stream()
			.map(o  -> new DbObject(o, schema))
			.toList();
	}
	
	
	/**
	 * Find all the objects in the schema. Only returns a single page of
	 * results based on the input parameters.
	 * @param schema
	 * @param page
	 * @param pageSize
	 * @param sortKey
	 * @param sortOrder
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PaginatedResult<DbObject> findAll(DbObjectSchema schema, int page, int pageSize, String sortKey, String sortOrder) {
		SimpleJpaRepository repository = schema.getJpaRepository();
		
		long maxElement = count(schema);
		int maxPage = (int)(Math.ceil ((double)maxElement / pageSize));
		
		if (page <= 0) page = 1;
		if (page > maxPage && maxPage != 0) {
			throw new InvalidPageException();
		}
		
		Sort sort = null;
		if (sortKey != null) {
			sort = Sort.by(sortKey);
		}
		if (Objects.equals(sortOrder, "ASC")) {
			sort = sort.ascending();
		} else if (Objects.equals(sortOrder, "DESC")) {
			sort = sort.descending();
		}
		PageRequest pageRequestion = null;
		
		if (sort != null) {
			pageRequestion = PageRequest.of(page - 1, pageSize, sort);
		} else {
			pageRequestion = PageRequest.of(page - 1, pageSize);
		}
		
		
		Page findAll = repository.findAll(pageRequestion);
		List<DbObject> results = new ArrayList<>();
		for (Object o : findAll) {
			results.add(new DbObject(o, schema));
		}
		
		
		return new PaginatedResult<DbObject>(
			new PaginationInfo(page, maxPage, pageSize, maxElement, null, null),
			results
		);
	}
	
	/**
	 * Update an existing object with new values. We don't use the "standard"
	 * JPA repository save method in this case (like we do on create) because
	 * we need to handle several edge cases in terms of how missing values
	 * are handled and also {@linkplain ReadOnly} fields. For this reason, we
	 * also need to call the validation manually.
	 * @param schema the schema where we need to update an item
	 * @param params the String-valued params coming from the HTML form
	 * @param files the file params coming from the HTML form
	 */
	@Transactional("transactionManager")
	public void update(DbObjectSchema schema, Map<String, String> params, Map<String, MultipartFile> files) {
		DbObject obj = schema.buildObject(params, files);
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<Object>> violations = validator.validate(obj.getUnderlyingInstance());
		
		if (violations.size() > 0) {
			throw new ConstraintViolationException(violations);
		}
		
		schema.getJpaRepository().update(schema, params, files);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional("transactionManager")
	private Object save(DbObjectSchema schema, DbObject o) {
		return schema.getJpaRepository().save(o.getUnderlyingInstance());
	}
	
	/**
	 * Attaches multiple many to many relationships to an object, parsed from a multi -valued map.
	 * @param schema	the entity class that owns this relationship
	 * @param id	the primary key of the entity where these relationships have to be attached to
	 * @param params	the multi-valued map containing the many-to-many relationships
	 */
	@Transactional("transactionManager")
	public void attachManyToMany(DbObjectSchema schema, Object id, Map<String, List<String>> params) {
		Optional<DbObject> optional = findById(schema, id);

		DbObject dbObject = optional.orElseThrow(() -> {
			return new SnapAdminException("Unable to retrieve newly inserted item");
		});
		
		for (String mParam : params.keySet()) {
			String fieldName = mParam.replace("[]", "");
			
			List<String> idValues = params.get(mParam);
			DbField field = schema.getFieldByName(fieldName);
			
			DbObjectSchema linkedSchema = field.getConnectedSchema();
			
			List<DbObject> traverseMany =  new ArrayList<>();
			for (String oId : idValues) {
				Optional<DbObject> findById = findById(linkedSchema, oId);
				if (findById.isPresent()) {
					traverseMany.add(findById.get());
				}
			}
			
			dbObject.set(
				field.getJavaName(), 
				traverseMany.stream().map(o -> o.getUnderlyingInstance()).collect(Collectors.toList())
			);
		}
		
		save(schema, dbObject);
	}
	
	/**
	 * Create a new object with the specific primary key and values,
	 * returns the primary key of the created object
	 * @param schema
	 * @param values
	 * @param primaryKey
	 */
	@Transactional("transactionManager")
	public Object create(DbObjectSchema schema, Map<String, String> values, Map<String, MultipartFile> files, String primaryKey) {
		DbObject obj = schema.buildObject(values, files);
		Object save = save(schema, obj);
		return new DbObject(save, schema).getPrimaryKeyValue();
	}
	
	/**
	 * Fuzzy search on primary key value and display name
	 * @param schema
	 * @param query
	 * @return
	 */
	public PaginatedResult<DbObject> search(DbObjectSchema schema, String query, int page, int pageSize, String sortKey, 
			String sortOrder, Set<QueryFilter> queryFilters) {
		CustomJpaRepository jpaRepository = schema.getJpaRepository();
        
		long maxElement = count(schema, query, queryFilters);
		int maxPage = (int)(Math.ceil ((double)maxElement / pageSize));
		
		if (page <= 0) page = 1;
		if (page > maxPage && maxPage != 0) {
			throw new InvalidPageException();
		}
		
		return new PaginatedResult<DbObject>(
			new PaginationInfo(page, maxPage, pageSize, maxElement, query, new FacetedSearchRequest(queryFilters)), 
			jpaRepository.search(query, page, pageSize, sortKey, sortOrder, queryFilters).stream()
				.map(o  -> new DbObject(o, schema))
				.toList()
		);
	}
	
	/**
	 * Fuzzy search on primary key value and display name
	 * @param schema
	 * @param query
	 * @return
	 */
	public List<DbObject> search(DbObjectSchema schema, String query) {
		CustomJpaRepository jpaRepository = schema.getJpaRepository();
		
		return jpaRepository.search(query, 1, 50, null, null, null).stream()
					.map(o  -> new DbObject(o, schema))
					.toList();
	}
	
	/**
	 * Execute custom SQL query using jdbcTemplate
	 */
	public DbQueryResult executeQuery(String sql) {
		List<DbQueryResultRow> results = new ArrayList<>();
		if (sql != null && !sql.isBlank()) {
			try {
				results = jdbcTemplate.query(sql, (rs, rowNum) -> {
					Map<DbQueryOutputField, Object> result = new HashMap<>();
					
					ResultSetMetaData metaData = rs.getMetaData();
					int cols = metaData.getColumnCount();
					
					for (int i = 0; i < cols; i++) {
						Object o = rs.getObject(i + 1);
						String columnName = metaData.getColumnName(i + 1);
						String tableName = metaData.getTableName(i + 1);
						DbQueryOutputField field = new DbQueryOutputField(columnName, tableName, snapAdmin);
						
						result.put(field, o);
					}
					
					DbQueryResultRow row = new DbQueryResultRow(result, sql);
					
					result.keySet().forEach(f -> {
						f.setResult(row);
					});
					
					return row;
				});
			} catch (TransientDataAccessResourceException | DataIntegrityViolationException e) {
				// If there's an exception we leave the results as empty
			} 
		}
		return new DbQueryResult(results);
	}
	
	/**
	 * Delete a specific object
	 * @param schema
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	@Transactional("transactionManager")
	public void delete(DbObjectSchema schema, String id) {
		schema.getJpaRepository().deleteById(id);
	}
	
}
