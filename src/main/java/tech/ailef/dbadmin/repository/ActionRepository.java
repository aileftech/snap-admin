package tech.ailef.dbadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import tech.ailef.dbadmin.model.Action;

public class ActionRepository extends SimpleJpaRepository<Action, Integer> {

	public ActionRepository(EntityManager em) {
		super(Action.class, em);
	}

}
