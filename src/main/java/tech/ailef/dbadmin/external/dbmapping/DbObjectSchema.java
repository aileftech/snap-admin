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

/**
 * A class that represents a table/`@Entity` as reconstructed from the
 * JPA annotations found on its fields.
 *
 */
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
	private CustomJpaRepository jpaRepository;
	
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
	
	/**
	 * Initializes this schema for the specific `@Entity` class. 
	 * Determines the table name from the `@Table` annotation and also
	 * which methods are `@ComputedColumn`s
	 * @param klass the `@Entity` class
	 * @param dbAdmin the DbAdmin instance
	 */
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
	
	/**
	 * Returns the DbAdmin instance
	 * @return the DbAdmin instance
	 */
	public DbAdmin getDbAdmin() {
		return dbAdmin;
	}
	
	/**
	 * Returns the Java class for the underlying `@Entity` this schema
	 * corresponds to
	 * @return  the Java class for the `@Entity` this schema corresponds to
	 */
	@JsonIgnore
	public Class<?> getJavaClass() {
		return entityClass;
	}
	
	/**
	 * Returns the name of the Java class for the underlying `@Entity` this schema
	 * corresponds to
	 * @return  the name of the Java class for the `@Entity` this schema corresponds to
	 */
	@JsonIgnore
	public String getClassName() {
		return entityClass.getName();
	}
	
	/**
	 * Returns an unmodifiable list of all the fields in the schema
	 * @return an unmodifiable list of all the fields in the schema
	 */
	public List<DbField> getFields() {
		return Collections.unmodifiableList(fields);
	}
	
	/**
	 * Get a field by its Java name, i.e. the name of the instance variable
	 * in the `@Entity` class
	 * @param name	name of the instance variable
	 * @return	the DbField if found, null otherwise
	 */
	public DbField getFieldByJavaName(String name) {
		return fields.stream().filter(f -> f.getJavaName().equals(name)).findFirst().orElse(null);
	}
	
	/**
	 * Get a field by its database name, i.e. the name of the column corresponding
	 * to the field
	 * @param name	name of the column
	 * @return	the DbField if found, null otherwise
	 */
	public DbField getFieldByName(String name) {
		return fields.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}
	
	/**
	 * Adds a field to this schema. This is used by the DbAdmin instance
	 * during initialization and it's not supposed to be called afterwards
	 * @param f	the DbField to add
	 */
	public void addField(DbField f) {
		fields.add(f);
	}
	
	/**
	 * Returns the underlying CustomJpaRepository
	 * @return
	 */
	public CustomJpaRepository getJpaRepository() {
		return jpaRepository;
	}

	/**
	 * Sets the underlying CustomJpaRepository
	 * @param jpaRepository
	 */
	public void setJpaRepository(CustomJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}
	
	/**
	 * Returns the inferred table name for this schema 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Returns a sorted list of physical fields (i.e., fields that correspond to
	 * a column in the table as opposed to fields that are just present as 
	 * instance variables, like relationship fields). Sorted alphabetically
	 * with priority to the primary key.
	 * 
	 * @return
	 */
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
	
	/**
	 * Returns the list of relationship fields
	 * @return
	 */
	public List<DbField> getRelationshipFields() {
		List<DbField> res = getFields().stream().filter(f -> {
			return f.getPrimitiveField().getAnnotation(OneToMany.class) != null
				|| f.getPrimitiveField().getAnnotation(ManyToMany.class) != null;
		}).collect(Collectors.toList());
		return res;
	}
	
	/**
	 * Returns the list of ManyToMany fields owned by this class (i.e. they
	 * do not have "mappedBy")
	 * @return
	 */
	public List<DbField> getManyToManyOwnedFields() {
		List<DbField> res = getFields().stream().filter(f -> {
			ManyToMany anno = f.getPrimitiveField().getAnnotation(ManyToMany.class);
			return anno != null && anno.mappedBy().isBlank();
		}).collect(Collectors.toList());
		return res;
	}
	
	/**
	 * Returns the DbField which serves as the primary key for this schema
	 * @return
	 */
	@JsonIgnore
	public DbField getPrimaryKey() {
		Optional<DbField> pk = fields.stream().filter(f -> f.isPrimaryKey()).findFirst();
		if (pk.isPresent())
			return pk.get();
		else
			throw new RuntimeException("No primary key defined on " + entityClass.getName() + " (table `" + tableName + "`)");
	}
	
	/**
	 * Returns the names of the `@ComputedColumn`s in this schema
	 * @return
	 */
	public List<String> getComputedColumnNames() {
		return computedColumns.keySet().stream().sorted().toList();
	}
	
	/**
	 * Returns the method for the given `@ComputedColumn` name
	 * @param name the name of the `@ComputedColumn`
	 * @return the corresponding instance method if found, null otherwise
	 */
	public Method getComputedColumn(String name) {
		return computedColumns.get(name);
	}
	
	/**
	 * Returns the list of fields that are `@Filterable`
	 * @return 
	 */
	public List<DbField> getFilterableFields() {
		return getSortedFields().stream().filter(f -> { 
			return !f.isBinary() && !f.isPrimaryKey() && f.isFilterable();
		}).toList();
	}
	
	/**
	 * Returns all the data in this schema, as `DbObject`s
	 * @return
	 */
	public List<DbObject> findAll() {
		List<?> r = jpaRepository.findAll();
		return r.stream().map(o -> new DbObject(o, this)).toList();
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
