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


package tech.ailef.snapadmin.internal.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tech.ailef.snapadmin.external.dto.LogsSearchRequest;
import tech.ailef.snapadmin.internal.model.UserAction;

/**
 * A repository that provides custom queries for UserActions 
 */
@Component
public class CustomActionRepositoryImpl implements CustomActionRepository {

    @PersistenceContext(unitName = "internal")
    private EntityManager entityManager;
    
    /**
     * Finds the UserAction that match the input search request.
     * Implemented as a custom CriteriaQuery in order to put all the filter
     * in an AND condition but ignore null value. The default JpaRepository
     * behaviour is to test for equality to NULL when an AND condition is used,
     * instead of ignoring the fields.
     */
    @Override
    public List<UserAction> findActions(LogsSearchRequest request) {
    	String table = request.getTable();
    	String actionType = request.getActionType();
    	String itemId = request.getItemId();
    	PageRequest page = request.toPageRequest();

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
        
        if (request.getSortKey() != null) {
        	String key = request.getSortKey();
        	if (request.getSortOrder().equalsIgnoreCase("ASC")) {
        		query.orderBy(cb.asc(userAction.get(key)));
        	} else {
        		query.orderBy(cb.desc(userAction.get(key)));
        	}
        }
        
        return entityManager.createQuery(query)
        			.setMaxResults(page.getPageSize())
        			.setFirstResult((int)page.getOffset())
        			.getResultList();
    }
    
    /**
     * Returns the count that match the filtering parameters, used for pagination.
     * @return the number of user actions matching the filtering parameters
     */
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