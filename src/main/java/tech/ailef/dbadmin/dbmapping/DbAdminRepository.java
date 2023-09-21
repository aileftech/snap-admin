package tech.ailef.dbadmin.dbmapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import tech.ailef.dbadmin.dto.PaginatedResult;
import tech.ailef.dbadmin.dto.PaginationInfo;
import tech.ailef.dbadmin.dto.QueryFilter;
import tech.ailef.dbadmin.exceptions.DbAdminException;
import tech.ailef.dbadmin.exceptions.InvalidPageException;

/**
 * Implements the basic CRUD operations (and some more)
 */
@Component
public class DbAdminRepository {
	private JdbcTemplate jdbcTemplate;
	
	public DbAdminRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
		else
			return Optional.of(new DbObject(optional.get(), schema));
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
	public PaginatedResult findAll(DbObjectSchema schema, int page, int pageSize, String sortKey, String sortOrder) {
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
		
		
		return new PaginatedResult(
			new PaginationInfo(page, maxPage, pageSize, maxElement, null, new HashSet<>()),
			results
		);
	}
	
	/**
	 * Update an existing object with new values
	 * @param schema
	 * @param params
	 */
	@Transactional
	public void update(DbObjectSchema schema, Map<String, String> params, Map<String, MultipartFile> files) {
		schema.getJpaRepository().update(schema, params, files);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	private void save(DbObjectSchema schema, DbObject o) {
		schema.getJpaRepository().save(o.getUnderlyingInstance());
	}
	
	@Transactional
	public void attachManyToMany(DbObjectSchema schema, Object id, Map<String, List<String>> params) {
		Optional<DbObject> optional = findById(schema, id);

		DbObject dbObject = optional.orElseThrow(() -> {
			return new DbAdminException("Unable to retrieve newly inserted item");
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
				fieldName, 
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
	public Object create(DbObjectSchema schema, Map<String, String> values, Map<String, MultipartFile> files, String primaryKey) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName(schema.getTableName());
		
		Map<String, Object> allValues = new HashMap<>();
		allValues.putAll(values);
		
		values.keySet().forEach(fieldName -> {
			if (values.get(fieldName).isBlank()) {
				allValues.put(fieldName, null);
			}
		});
		
		files.keySet().forEach(f -> {
			try {
				allValues.put(f, files.get(f).getBytes());
			} catch (IOException e) {
				throw new DbAdminException(e);
			}
		});

		if (primaryKey == null) {
			insert = insert.usingGeneratedKeyColumns(schema.getPrimaryKey().getName());
			return insert.executeAndReturnKey(allValues);
		} else {
			insert.execute(allValues);
			return primaryKey;
		}
	}
	
	
	/**
	 * Fuzzy search on primary key value and display name
	 * @param schema
	 * @param query
	 * @return
	 */
	public PaginatedResult search(DbObjectSchema schema, String query, int page, int pageSize, String sortKey, 
			String sortOrder, Set<QueryFilter> queryFilters) {
		AdvancedJpaRepository jpaRepository = schema.getJpaRepository();
        
		long maxElement = count(schema, query, queryFilters);
		int maxPage = (int)(Math.ceil ((double)maxElement / pageSize));
		
		if (page <= 0) page = 1;
		if (page > maxPage && maxPage != 0) {
			throw new InvalidPageException();
		}
		
		return new PaginatedResult(
			new PaginationInfo(page, maxPage, pageSize, maxElement, query, queryFilters), 
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
		AdvancedJpaRepository jpaRepository = schema.getJpaRepository();
		
		return jpaRepository.search(query, 1, 50, null, null, null).stream()
					.map(o  -> new DbObject(o, schema))
					.toList();
	}
	
	/**
	 * Delete a specific object
	 * @param schema
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void delete(DbObjectSchema schema, String id) {
		schema.getJpaRepository().deleteById(id);
	}
	
}
