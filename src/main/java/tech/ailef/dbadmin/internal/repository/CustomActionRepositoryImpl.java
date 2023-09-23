package tech.ailef.dbadmin.internal.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.internal.model.UserAction;

@Component
public class CustomActionRepositoryImpl implements CustomActionRepository {

    @PersistenceContext(unitName = "internal")
    private EntityManager entityManager;
    
    @Autowired
    private DbAdmin dbAdmin;

    @Override
    public List<UserAction> findActions(String table, String actionType, String itemId, PageRequest page) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserAction> query = cb.createQuery(UserAction.class);
        Root<UserAction> userAction = query.from(UserAction.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (table != null)
            predicates.add(cb.equal(userAction.get("onTable"), table));
        if (actionType != null)
            predicates.add(cb.equal(userAction.get("actionType"), actionType));
        if (itemId != null)
        	predicates.add(cb.equal(userAction.get("primaryKey"), itemId));

        if (!predicates.isEmpty()) {
            query.select(userAction)
                 .where(cb.and(
                            predicates.toArray(new Predicate[predicates.size()])));
        }
        
        return entityManager.createQuery(query)
        			.setMaxResults(page.getPageSize())
        			.setFirstResult((int)page.getOffset())
        			.getResultList();
    }
    
    @Override
    public long countActions(String table, String actionType, String itemId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserAction> userAction = query.from(UserAction.class);

        List<Predicate> predicates = new ArrayList<>();
        if (table != null)
            predicates.add(cb.equal(userAction.get("onTable"), table));
        if (actionType != null)
            predicates.add(cb.equal(userAction.get("actionType"), actionType));
        if (itemId != null)
        	predicates.add(cb.equal(userAction.get("primaryKey"), itemId));

        if (!predicates.isEmpty()) {
            query.select(cb.count(userAction))
                 .where(cb.and(
                            predicates.toArray(new Predicate[predicates.size()])));
        } else {
        	query.select(cb.count(userAction));
        }
        
        return entityManager.createQuery(query).getSingleResult();
    }

}