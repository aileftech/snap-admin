package tech.ailef.dbadmin.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.ailef.dbadmin.internal.model.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {

}
