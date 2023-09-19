package tech.ailef.dbadmin.dbmapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.MultiValueMap;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
	public long count(String q, MultiValueMap<String, String> filteringParams) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(Long.class);
        Root root = query.from(schema.getJavaClass());

        List<Predicate> finalPredicates = buildPredicates(q, filteringParams, cb, root);
        
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
	public List<Object> search(String q, int page, int pageSize, String sortKey, String sortOrder, MultiValueMap<String, String> filteringParams) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(schema.getJavaClass());
        Root root = query.from(schema.getJavaClass());
        
        List<Predicate> finalPredicates = buildPredicates(q, filteringParams, cb, root);
        
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
	
	private List<Predicate> buildPredicates(String q, MultiValueMap<String, String> filteringParams,
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

        /*
         * Compute filtering predicates
         */
        if (filteringParams != null) {
	        List<String> ops = filteringParams.get("filter_op[]");
			List<String> fields = filteringParams.get("filter_field[]");
			List<String> values = filteringParams.get("filter_value[]");
			
			
			if (ops != null && fields != null && values != null) {
				if (ops.size() != fields.size() || fields.size() != values.size()
					|| ops.size() != values.size()) {
					throw new DbAdminException("Filtering parameters must have the same size");
				}
				
				for (int i = 0; i < ops.size(); i++) {
					String op = ops.get(i);
					String field = fields.get(i);
					String value = values.get(i);
					
					if (op.equalsIgnoreCase("equals")) {
						finalPredicates.add(cb.equal(cb.toString(root.get(field)), value));
					} else if (op.equalsIgnoreCase("contains")) {
						System.out.println("CONTAINS");
						finalPredicates.add(cb.like(cb.toString(root.get(field)), "%" + value + "%"));
					}
				}
			}
        }
        return finalPredicates;
	}
	
	
//	@SuppressWarnings("unchecked")
//	public List<Object> distinctFieldValues(DbField field) {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//		
//		Class<?> outputType = field.getType().getJavaClass();
//		if (field.getConnectedType() != null) {
//			outputType = field.getConnectedSchema().getPrimaryKey().getType().getJavaClass();
//		}
//		
//        CriteriaQuery query = cb.createQuery(outputType);
//        Root root = query.from(schema.getJavaClass());
//        
//        query.select(root.get(field.getJavaName()).as(outputType)).distinct(true);
//        
//        return entityManager.createQuery(query).getResultList();
//	}
}
