package tech.ailef.dbadmin.dbmapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
	
	public long count(String q) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(Long.class);
        Root root = query.from(schema.getJavaClass());
        
        List<DbField> stringFields = 
        	schema.getSortedFields().stream().filter(f -> f.getType() == DbFieldType.STRING)
        			.collect(Collectors.toList());
        
        System.out.println("STRING F = " + stringFields);
        List<Predicate> predicates = new ArrayList<>();
        for (DbField f : stringFields) {
        	Path path = root.get(f.getJavaName());
        	predicates.add(cb.like(cb.lower(cb.toString(path)), "%" + q.toLowerCase() + "%"));
        }

        query.select(cb.count(root.get(schema.getPrimaryKey().getName())))
            .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
        
        Object o = entityManager.createQuery(query).getSingleResult();
        return (Long)o;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> search(String q, int page, int pageSize, String sortKey, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(schema.getJavaClass());
        Root root = query.from(schema.getJavaClass());
        
        List<DbField> stringFields = 
        	schema.getSortedFields().stream().filter(f -> f.getType() == DbFieldType.STRING)
        			.collect(Collectors.toList());
        
        List<Predicate> predicates = new ArrayList<>();
        for (DbField f : stringFields) {
        	Path path = root.get(f.getJavaName());
        	predicates.add(cb.like(cb.lower(cb.toString(path)), "%" + q.toLowerCase() + "%"));
        }

        query.select(root)
            .where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
        if (sortKey !=  null)
        	query.orderBy(sortOrder.equals("DESC") ? cb.desc(root.get(sortKey)) : cb.asc(root.get(sortKey)));
        
        return entityManager.createQuery(query).setMaxResults(pageSize)
        			.setFirstResult((page - 1) * pageSize).getResultList();
	}
}
