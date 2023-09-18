package tech.ailef.dbadmin.dbmapping;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tech.ailef.dbadmin.DbAdmin;
import tech.ailef.dbadmin.annotations.ComputedColumn;
import tech.ailef.dbadmin.annotations.Filterable;
import tech.ailef.dbadmin.exceptions.DbAdminException;
import tech.ailef.dbadmin.misc.Utils;

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
				return f.getPrimitiveField().getAnnotation(OneToMany.class) == null
					&& f.getPrimitiveField().getAnnotation(ManyToMany.class) == null;
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
					&& f.getPrimitiveField().getAnnotation(Filterable.class) != null;
		}).toList();
	}
	
	public List<Object> getFieldValues(DbField field) {
		return jpaRepository.distinctFieldValues(field);
	}

	public Object[] getInsertArray(Map<String, String> params, Map<String, MultipartFile> files) {
		int currentIndex = 0;
		
		String pkValue = params.get(getPrimaryKey().getName());
		if (pkValue == null || pkValue.isBlank())
			pkValue = null;
		
		Object[] row; 
		if (pkValue == null) {
			row = new Object[getSortedFields().size() - 1];
		} else {
			row = new Object[getSortedFields().size()];
		}
		
		for (DbField field : getSortedFields()) {
			// Skip the primary key if the value is null
			// If it is autogenerated, it will be filled by the database
			// otherwise it will throw an error
			if (field.isPrimaryKey() && pkValue == null) {
				continue;
			}
			
			String name = field.getName();
			
			String stringValue = params.get(name);
			Object value = null;
			if (stringValue != null && stringValue.isBlank()) stringValue = null;
			if (stringValue != null) {
				value = stringValue;
			} else {
				value = files.get(name);
			}
			
			String type = params.get("__dbadmin_" + name + "_type");
			
			if (type == null)
				throw new RuntimeException("Missing type hidden field for: " + name);
			
			try {
				if (value == null)
					row[currentIndex++] = null;
				else
					row[currentIndex++] = DbFieldType.valueOf(type).parseValue(value);
			} catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		return row;
	}
	
	public Object[] getUpdateArray(Map<String, String> params, Map<String, MultipartFile> files) {
		Object[] row = new Object[getSortedFields().size() + 1];
		
		int currentIndex = 0;
		DbField primaryKey = getPrimaryKey();
		String pkValue = params.get(primaryKey.getName());
		
		for (DbField field : getSortedFields()) {
			String name = field.getName();
			
			String stringValue = params.get(name);
			Object value = null;
			if (stringValue != null && stringValue.isBlank()) stringValue = null;
			if (stringValue != null) {
				value = stringValue;
			} else {
				value = files.get(name);
			}
			
			String type = params.get("__dbadmin_" + name + "_type");
			
			if (type == null)
				throw new RuntimeException("Missing type hidden field for: " + name);
			
			try {
				if (value == null)
					row[currentIndex++] = null;
				else
					row[currentIndex++] = DbFieldType.valueOf(type).parseValue(value);
			} catch (IllegalArgumentException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		row[currentIndex] = primaryKey.getType().parseValue(pkValue);
		
		return row;
	}


	@Override
	public String toString() {
		return "DbObjectSchema [fields=" + fields + ", className=" + entityClass.getName() + "]";
	}
	
}
