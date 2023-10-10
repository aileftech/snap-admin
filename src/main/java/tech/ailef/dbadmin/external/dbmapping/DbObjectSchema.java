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


package tech.ailef.dbadmin.external.dbmapping;

import java.lang.reflect.InvocationTargetException;
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

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import tech.ailef.dbadmin.external.DbAdmin;
import tech.ailef.dbadmin.external.annotations.ComputedColumn;
import tech.ailef.dbadmin.external.annotations.DisableCreate;
import tech.ailef.dbadmin.external.annotations.DisableDelete;
import tech.ailef.dbadmin.external.annotations.DisableEdit;
import tech.ailef.dbadmin.external.annotations.HiddenColumn;
import tech.ailef.dbadmin.external.dto.MappingError;
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
	
	private List<MappingError> errors = new ArrayList<>();
	
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
	
	public String getBasePackage() {
		return entityClass.getPackageName();
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
	
	public List<MappingError> getErrors() {
		return Collections.unmodifiableList(errors);
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
	
	public void addError(MappingError error) {
		errors.add(error);
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
	 * See {@link DbObjectSchema#getSortedFields()} 
	 * @return
	 */
	@JsonIgnore
	public List<DbField> getSortedFields() {
		return getSortedFields(true);
	}
	
	/**
	 * Returns a sorted list of physical fields (i.e., fields that correspond to
	 * a column in the table as opposed to fields that are just present as 
	 * instance variables, like relationship fields). Sorted alphabetically
	 * with priority the primary key, and non nullable fields.
	 * 
	 * If readOnly is true, `@HiddenColumn`s are not returned. If instead
	 * readOnly is false, i.e. how it gets called in the create/edit page,
	 * hidden columns are included if they are not nullable.
	 * 
	 * @param readOnly whether we only need to read the fields are create/edit
	 * @return 
	 */
	public List<DbField> getSortedFields(boolean readOnly) {
		return getFields().stream()
			.filter(f -> {
				boolean toMany = f.getPrimitiveField().getAnnotation(OneToMany.class) == null
					&& f.getPrimitiveField().getAnnotation(ManyToMany.class) == null;
				
				OneToOne oneToOne = f.getPrimitiveField().getAnnotation(OneToOne.class);
				boolean mappedBy = oneToOne != null && !oneToOne.mappedBy().isBlank();
				
				boolean hidden = f.getPrimitiveField().getAnnotation(HiddenColumn.class) != null;
				
				
				return toMany && !mappedBy && (!hidden || !readOnly);
			})
			.sorted((a, b) -> {
				if (a.isPrimaryKey() && !b.isPrimaryKey())
					return -1;
				if (b.isPrimaryKey() && !a.isPrimaryKey())
					return 1;
				
				if (!a.isNullable() && b.isNullable())
					return -1;
				if (a.isNullable() && !b.isNullable())
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
	
	public boolean isDeleteEnabled() {
		return entityClass.getAnnotation(DisableDelete.class) == null;
	}
	
	public boolean isEditEnabled() {
		return entityClass.getAnnotation(DisableEdit.class) == null;
	}
	
	public boolean isCreateEnabled() {
		return entityClass.getAnnotation(DisableCreate.class) == null;
	}
	
	/**
	 * Returns all the data in this schema, as `DbObject`s
	 * @return
	 */
	public List<DbObject> findAll() {
		List<?> r = jpaRepository.findAll();
		return r.stream().map(o -> new DbObject(o, this)).toList();
	}

	public DbObject buildObject(Map<String, String> params, Map<String, MultipartFile> files) {
		try {
			Object instance = getJavaClass().getConstructor().newInstance();
			DbObject dbObject = new DbObject(instance, this);
			
			for (String param : params.keySet()) {
				// Parameters starting with __ are hidden and not related to the object creation
				if (param.startsWith("__")) continue;
				
				String javaFieldName = getFieldByName(param).getJavaName();
				Method setter = dbObject.findSetter(javaFieldName);
				
				if (setter ==  null) {
					throw new RuntimeException("Cannot find setter for " + javaFieldName);
				}
				
				Object parsedFieldValue = 
					getFieldByName(param).getType().parseValue(params.get(param));
				
				if (parsedFieldValue != null && getFieldByName(param).isSettable()) {
					setter.invoke(instance, parsedFieldValue);
				}
			}
			
			for (String fileParam : files.keySet()) {
				if (fileParam.startsWith("__")) continue;

				String javaFieldName = getFieldByName(fileParam).getJavaName();
				Method setter = dbObject.findSetter(javaFieldName);
				
				if (setter ==  null) {
					throw new RuntimeException("Cannot find setter for " + fileParam);
				}
				
				Object parsedFieldValue = 
						getFieldByName(fileParam).getType().parseValue(params.get(fileParam));
				
				if (parsedFieldValue != null && getFieldByName(fileParam).isSettable()) {
					setter.invoke(instance, parsedFieldValue);
				}
			}
			
			return dbObject;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		
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
