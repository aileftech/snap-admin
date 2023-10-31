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


package tech.ailef.snapadmin.external.dbmapping;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tech.ailef.snapadmin.external.dbmapping.fields.DbField;
import tech.ailef.snapadmin.external.dbmapping.fields.StringFieldType;
import tech.ailef.snapadmin.external.dbmapping.fields.TextFieldType;
import tech.ailef.snapadmin.external.dto.CompareOperator;
import tech.ailef.snapadmin.external.dto.QueryFilter;
import tech.ailef.snapadmin.external.exceptions.DbAdminException;

@SuppressWarnings("rawtypes")
public class CustomJpaRepository extends SimpleJpaRepository {

	private EntityManager entityManager;
	
	private DbObjectSchema schema;
	
	@SuppressWarnings("unchecked")
	public CustomJpaRepository(DbObjectSchema schema, EntityManager em) {
		super(schema.getJavaClass(), em);
		this.entityManager = em;
		this.schema = schema;
	}
	
	@SuppressWarnings("unchecked")
	public long count(String q, Set<QueryFilter> queryFilters) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(Long.class);
        Root root = query.from(schema.getJavaClass());

        List<Predicate> finalPredicates = buildPredicates(q, queryFilters, cb, root);
        
        query.select(cb.count(root.get(schema.getPrimaryKey().getName())))
            .where(
        		cb.and(
                		finalPredicates.toArray(new Predicate[finalPredicates.size()])
                	)
        		);
        
        Object o = entityManager.createQuery(query).getSingleResult();
        return (Long)o;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> search(String q, int page, int pageSize, String sortKey, String sortOrder, Set<QueryFilter> filters) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(schema.getJavaClass());
        Root root = query.from(schema.getJavaClass());
        
        List<Predicate> finalPredicates = buildPredicates(q, filters, cb, root);
        
        query.select(root)
            .where(
            	cb.or(
            		cb.and(finalPredicates.toArray(new Predicate[finalPredicates.size()])), // query search on String fields
            		cb.equal(root.get(schema.getPrimaryKey().getName()).as(String.class), q)
            	)
            	
            );
        
        if (sortKey !=  null)
        	query.orderBy(sortOrder.equals("DESC") ? cb.desc(root.get(sortKey)) : cb.asc(root.get(sortKey)));
        
        return entityManager.createQuery(query).setMaxResults(pageSize)
        			.setFirstResult((page - 1) * pageSize).getResultList();
	}
	
	
	public List<Object> search(String query, Set<QueryFilter> filters) {
		return search(query, 1, Integer.MAX_VALUE, null, null, filters);
	}

	

	@SuppressWarnings("unchecked")
	public int update(DbObjectSchema schema, Map<String, String> params, Map<String, MultipartFile> files) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaUpdate update = cb.createCriteriaUpdate(schema.getJavaClass());

		Root root = update.from(schema.getJavaClass());

		boolean hasUpdate = false;
		for (DbField field : schema.getSortedFields()) {
			if (field.isPrimaryKey()) continue;
			if (field.isReadOnly()) continue;
			
			boolean keepValue = params.getOrDefault("__keep_" + field.getName(), "off").equals("on");
			if (keepValue) continue;
			
			String stringValue = params.get(field.getName());
			Object value = null;
			if (stringValue != null && stringValue.isBlank()) stringValue = null;
			if (stringValue != null) {
				value = field.getType().parseValue(stringValue);
			} else {
				try {
					MultipartFile file = files.get(field.getName());
					if (file != null) {
						if (file.isEmpty()) value = null;
						else value = file.getBytes();
					}
				} catch (IOException e) {
					throw new DbAdminException(e);
				}
			}
			
			if (field.getConnectedSchema() != null)
				value = field.getConnectedSchema().getJpaRepository().findById(value).get();
			
			update.set(root.get(field.getJavaName()), value);
			hasUpdate = true;
		}
		
		if (!hasUpdate) return 0;
		
		String pkName = schema.getPrimaryKey().getJavaName();
		update.where(cb.equal(root.get(pkName), params.get(schema.getPrimaryKey().getName())));

		Query query = entityManager.createQuery(update);
		return query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	private List<Predicate> buildPredicates(String q, Set<QueryFilter> queryFilters,
			CriteriaBuilder cb, Path root) {
		List<Predicate> finalPredicates = new ArrayList<>();
        
        List<DbField> stringFields = 
        	schema.getSortedFields().stream().filter(f -> f.getType() instanceof StringFieldType || f.getType() instanceof TextFieldType)
        			.collect(Collectors.toList());
        
        List<Predicate> queryPredicates = new ArrayList<>();
        if (q != null && !q.isBlank()) {
	        for (DbField f : stringFields) {
	        	Path path = root.get(f.getJavaName());
	        	queryPredicates.add(cb.like(cb.lower(cb.toString(path)), "%" + q.toLowerCase() + "%"));
	        }
	        
	        Predicate queryPredicate = cb.or(queryPredicates.toArray(new Predicate[queryPredicates.size()]));
	        finalPredicates.add(queryPredicate);
        }

        
        if (queryFilters == null) queryFilters = new HashSet<>();
        for (QueryFilter filter  : queryFilters) {
        	CompareOperator op = filter.getOp();
        	DbField dbField = filter.getField();
        	String fieldName = dbField.getJavaName();
        	String v = filter.getValue();
        	
        	Object value = null;
        	
        	if (!v.isBlank()) {
	        	try {
	        		value = dbField.getType().parseValue(v);
	        	} catch (Exception e) {
	        		throw new DbAdminException("Invalid value `" + v + "` specified for field `" + dbField.getName() + "`");
	        	}
        	}
        	
			if (op == CompareOperator.STRING_EQ) {
				if (value == null)
					finalPredicates.add(cb.isNull(root.get(fieldName)));
				else
					finalPredicates.add(cb.equal(cb.lower(cb.toString(root.get(fieldName))), value.toString().toLowerCase()));
			} else if (op == CompareOperator.CONTAINS) {
				if (value != null)
					finalPredicates.add(
						cb.like(cb.lower(cb.toString(root.get(fieldName))), "%" + value.toString().toLowerCase() + "%")
					);
			} else if (op == CompareOperator.EQ) {
				finalPredicates.add(
					cb.equal(root.get(fieldName), value)
				);
			} else if (op == CompareOperator.GT) {
				if (value != null)
					finalPredicates.add(
						cb.greaterThan(root.get(fieldName), value.toString())
					);
			} else if (op == CompareOperator.LT) {
				if (value != null)
					finalPredicates.add(
						cb.lessThan(root.get(fieldName), value.toString())
					);
			} else if (op == CompareOperator.AFTER) {
				if (value instanceof LocalDate)
					finalPredicates.add(
						cb.greaterThan(root.get(fieldName), (LocalDate)value)
					);
				else if (value instanceof LocalDateTime)
					finalPredicates.add(
						cb.greaterThan(root.get(fieldName), (LocalDateTime)value)
					);
				
			} else if (op == CompareOperator.BEFORE) {
				if (value instanceof LocalDate)
					finalPredicates.add(
						cb.lessThan(root.get(fieldName), (LocalDate)value)
					);
				else if (value instanceof LocalDateTime)
					finalPredicates.add(
						cb.lessThan(root.get(fieldName), (LocalDateTime)value)
					);
				
			}
        }
        return finalPredicates;
	}
}
