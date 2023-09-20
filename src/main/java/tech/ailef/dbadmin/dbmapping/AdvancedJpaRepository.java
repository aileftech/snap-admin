package tech.ailef.dbadmin.dbmapping;

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
import tech.ailef.dbadmin.dto.CompareOperator;
import tech.ailef.dbadmin.dto.QueryFilter;
import tech.ailef.dbadmin.exceptions.DbAdminException;

@SuppressWarnings("rawtypes")
public class AdvancedJpaRepository extends SimpleJpaRepository {

	private EntityManager entityManager;
	
	private DbObjectSchema schema;
	
	@SuppressWarnings("unchecked")
	public AdvancedJpaRepository(DbObjectSchema schema, EntityManager em) {
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
            	cb.and(
            		finalPredicates.toArray(new Predicate[finalPredicates.size()]) // query search on String fields
            	)
            	
            );
        if (sortKey !=  null)
        	query.orderBy(sortOrder.equals("DESC") ? cb.desc(root.get(sortKey)) : cb.asc(root.get(sortKey)));
        
        return entityManager.createQuery(query).setMaxResults(pageSize)
        			.setFirstResult((page - 1) * pageSize).getResultList();
	}
	
	private List<Predicate> buildPredicates(String q, Set<QueryFilter> queryFilters,
			CriteriaBuilder cb, Path root) {
		List<Predicate> finalPredicates = new ArrayList<>();
        
        List<DbField> stringFields = 
        	schema.getSortedFields().stream().filter(f -> f.getType() == DbFieldType.STRING)
        			.collect(Collectors.toList());
        
        List<Predicate> queryPredicates = new ArrayList<>();
        if (q != null) {
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
        	String field = filter.getField();
        	String v = filter.getValue();
        	
        	DbField dbField = schema.getFieldByJavaName(field);
        	Object value = dbField.getType().parseValue(v);
        	
			if (op == CompareOperator.STRING_EQ) {
				finalPredicates.add(cb.equal(cb.lower(cb.toString(root.get(field))), value.toString().toLowerCase()));
			} else if (op == CompareOperator.CONTAINS) {
				finalPredicates.add(
					cb.like(cb.lower(cb.toString(root.get(field))), "%" + value.toString().toLowerCase() + "%")
				);
			} else if (op == CompareOperator.EQ) {
				finalPredicates.add(
					cb.equal(root.get(field), value)
				);
			} else if (op == CompareOperator.GT) {
				finalPredicates.add(
					cb.greaterThan(root.get(field), value.toString())
				);
			} else if (op == CompareOperator.LT) {
				finalPredicates.add(
					cb.lessThan(root.get(field), value.toString())
				);
			} else if (op == CompareOperator.AFTER) {
				if (value instanceof LocalDate)
					finalPredicates.add(
						cb.greaterThan(root.get(field), (LocalDate)value)
					);
				else if (value instanceof LocalDateTime)
					finalPredicates.add(
						cb.greaterThan(root.get(field), (LocalDateTime)value)
					);
				
			} else if (op == CompareOperator.BEFORE) {
				if (value instanceof LocalDate)
					finalPredicates.add(
						cb.lessThan(root.get(field), (LocalDate)value)
					);
				else if (value instanceof LocalDateTime)
					finalPredicates.add(
						cb.lessThan(root.get(field), (LocalDateTime)value)
					);
				
			}
        }
        return finalPredicates;
	}

	public void update(DbObjectSchema schema, Map<String, String> params, Map<String, MultipartFile> files) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaUpdate update = cb.createCriteriaUpdate(schema.getJavaClass());

		Root employee = update.from(schema.getJavaClass());

		for (DbField field : schema.getSortedFields()) {
			if (field.isPrimaryKey()) continue;
			
			String stringValue = params.get(field.getName());
			Object value = null;
			if (stringValue != null && stringValue.isBlank()) stringValue = null;
			if (stringValue != null) {
				value = field.getType().parseValue(stringValue);
			} else {
				try {
					MultipartFile file = files.get(field.getJavaName());
					if (file != null)
						value = file.getBytes();
				} catch (IOException e) {
					throw new DbAdminException(e);
				}
			}
			
			update.set(employee.get(field.getJavaName()), value);
		}
		String pkName = schema.getPrimaryKey().getJavaName();
		update.where(cb.equal(employee.get(pkName), params.get(schema.getPrimaryKey().getName())));

		Query query = entityManager.createQuery(update);
		int rowCount = query.executeUpdate();
		
	}
}
