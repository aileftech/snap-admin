package tech.ailef.dbadmin.external.dbmapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.annotations.ComputedColumn;
import tech.ailef.dbadmin.external.exceptions.DbAdminException;
import tech.ailef.dbadmin.external.misc.Utils;

public class DbObjectSchema {
	/**
	 * All the fields in this table. The fields include all the
	 * columns present in the table plus relationship fields.
	 */
	@JsonIgnore
	private List<DbField> fields = new ArrayList<>();
	
	/**
	 * The methods designated as computed columns in the `@Entity` class.
	 */
	@JsonIgnore
	private Map<String, Method> computedColumns = new HashMap<>();
	
	/**
	 * A JPA repository to operate on the database
	 */
	private AdvancedJpaRepository jpaRepository;
	
	private DbAdmin dbAdmin;
	
	/**
	 * The corresponding `@Entity` class that this schema describes
	 */
	@JsonIgnore
	private Class<?> entityClass;
	
	/**
	 * The name of this table on the database
	 */
	private String tableName;
	
	public DbObjectSchema(Class<?> klass, DbAdmin dbAdmin) {
		this.dbAdmin = dbAdmin;
		this.entityClass = klass;
		
		Table tableAnnotation = klass.getAnnotation(Table.class);
		
		String tableName = Utils.camelToSnake(getJavaClass().getSimpleName());
		if (tableAnnotation != null && tableAnnotation.name() != null
			&& !tableAnnotation.name().isBlank()) { 
			tableName = tableAnnotation.name();
		}

		this.tableName = tableName;
		
		List<Method> methods = Arrays.stream(entityClass.getMethods())
				.filter(m -> m.getAnnotation(ComputedColumn.class) != null)
				.collect(Collectors.toList());
		for (Method m : methods) {
			if (m.getParameterCount() > 0)
				throw new DbAdminException("@ComputedColumn can only be applied on no-args methods");
			
			String name = m.getAnnotation(ComputedColumn.class).name();
			if (name.isBlank())
				name = Utils.camelToSnake(m.getName());
			
			computedColumns.put(name, m);
		}
	}
	
	public DbAdmin getDbAdmin() {
		return dbAdmin;
	}
	
	@JsonIgnore
	public Class<?> getJavaClass() {
		return entityClass;
	}
	
	@JsonIgnore
	public String getClassName() {
		return entityClass.getName();
	}
	
	public List<DbField> getFields() {
		return Collections.unmodifiableList(fields);
	}
	
	public DbField getFieldByJavaName(String name) {
		return fields.stream().filter(f -> f.getJavaName().equals(name)).findFirst().orElse(null);
	}
	
	public DbField getFieldByName(String name) {
		return fields.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}
	
	public void addField(DbField f) {
		fields.add(f);
	}

	public AdvancedJpaRepository getJpaRepository() {
		return jpaRepository;
	}

	public void setJpaRepository(AdvancedJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	@JsonIgnore
	public List<DbField> getSortedFields() {
		return getFields().stream()
			.filter(f -> {
				boolean toMany = f.getPrimitiveField().getAnnotation(OneToMany.class) == null
					&& f.getPrimitiveField().getAnnotation(ManyToMany.class) == null;
				
				OneToOne oneToOne = f.getPrimitiveField().getAnnotation(OneToOne.class);
				boolean mappedBy = oneToOne != null && !oneToOne.mappedBy().isBlank();
				
				return toMany && !mappedBy;
			})
			.sorted((a, b) -> {
				if (a.isPrimaryKey() && !b.isPrimaryKey())
					return -1;
				if (b.isPrimaryKey() && !a.isPrimaryKey())
					return 1;
				return a.getName().compareTo(b.getName());
			}).collect(Collectors.toList());
	}
	
	public List<DbField> getRelationshipFields() {
		List<DbField> res = getFields().stream().filter(f -> {
			return f.getPrimitiveField().getAnnotation(OneToMany.class) != null
				|| f.getPrimitiveField().getAnnotation(ManyToMany.class) != null;
		}).collect(Collectors.toList());
		return res;
	}
	
	public List<DbField> getManyToManyOwnedFields() {
		List<DbField> res = getFields().stream().filter(f -> {
			ManyToMany anno = f.getPrimitiveField().getAnnotation(ManyToMany.class);
			return anno != null && anno.mappedBy().isBlank();
		}).collect(Collectors.toList());
		return res;
	}
	
	@JsonIgnore
	public DbField getPrimaryKey() {
		Optional<DbField> pk = fields.stream().filter(f -> f.isPrimaryKey()).findFirst();
		if (pk.isPresent())
			return pk.get();
		else
			throw new RuntimeException("No primary key defined on " + entityClass.getName() + " (table `" + tableName + "`)");
	}
	
	public List<String> getComputedColumnNames() {
		return computedColumns.keySet().stream().sorted().toList();
	}
	
	public Method getComputedColumn(String name) {
		return computedColumns.get(name);
	}
	
	public List<DbField> getFilterableFields() {
		return getSortedFields().stream().filter(f -> { 
			return !f.isBinary() && !f.isPrimaryKey()
					&& f.isFilterable();
		}).toList();
	}
	
	public List<DbObject> findAll() {
		List r = jpaRepository.findAll();
		List<DbObject> results = new ArrayList<>();
		for (Object o : r) {
			results.add(new DbObject(o, this));
		}
		return results;
	}

	@Override
	public String toString() {
		return "DbObjectSchema [fields=" + fields + ", className=" + entityClass.getName() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(tableName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbObjectSchema other = (DbObjectSchema) obj;
		return Objects.equals(tableName, other.tableName);
	}
	
	
	
}
