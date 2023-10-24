package tech.ailef.dbadmin.external.dbmapping.fields;

import java.util.List;

import jakarta.persistence.OneToMany;
import tech.ailef.dbadmin.external.dto.CompareOperator;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;

public class OneToManyFieldType extends DbFieldType {
	@Override
	public String getFragmentName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object parseValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getJavaClass() {
		return OneToMany.class;
	}
	
	@Override
	public boolean isRelationship() {
		return true;
	}
	
	@Override
	public String toString() {
		return "One to Many";
	}
	
	@Override
	public List<CompareOperator> getCompareOperators() {
		throw new DbAdminException();
	}
}
