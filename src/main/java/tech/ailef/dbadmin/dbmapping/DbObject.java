package tech.ailef.dbadmin.dbmapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import tech.ailef.dbadmin.annotations.DisplayName;
import tech.ailef.dbadmin.exceptions.DbAdminException;
import tech.ailef.dbadmin.misc.Utils;

public class DbObject {
	private Object instance;
	
	private DbObjectSchema schema;
	
	public DbObject(Object instance, DbObjectSchema schema) {
		this.instance = instance;
		this.schema = schema;
	}

	public boolean has(DbField field) {
		return findGetter(field.getJavaName()) != null;
	}
	
	public Object getUnderlyingInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public List<DbObject> getValues(DbField field) {
		List<Object> values = (List<Object>)get(field.getJavaName()).getValue();
		return values.stream().map(o -> new DbObject(o, field.getConnectedSchema()))
				.collect(Collectors.toList());
	}
	
	public DbFieldValue get(DbField field) {
		return get(field.getJavaName());
	}
	
	public DbObject traverse(String fieldName) {
		DbField field = schema.getFieldByName(fieldName);
		return traverse(field);
	}
	
	public DbObject traverse(DbField field) {
		ManyToOne manyToOne = field.getPrimitiveField().getAnnotation(ManyToOne.class);
		OneToOne oneToOne = field.getPrimitiveField().getAnnotation(OneToOne.class);
		if (oneToOne != null || manyToOne != null) {
			Object linkedObject = get(field.getJavaName()).getValue();
			DbObject linkedDbObject = new DbObject(linkedObject, field.getConnectedSchema());
			return linkedDbObject;
		} else {
			throw new DbAdminException("Cannot traverse field " + field.getName() + " in class " + schema.getClassName());
		}
	}
	
	public List<DbObject> traverseMany(String fieldName) {
		DbField field = schema.getFieldByName(fieldName);
		return traverseMany(field);
	}
	
	@SuppressWarnings("unchecked")
	public List<DbObject> traverseMany(DbField field) {
		ManyToMany manyToMany = field.getPrimitiveField().getAnnotation(ManyToMany.class);
		OneToMany oneToMany = field.getPrimitiveField().getAnnotation(OneToMany.class);
		if (manyToMany != null || oneToMany != null) {
			List<Object> linkedObjects = (List<Object>)get(field.getJavaName()).getValue();
			return linkedObjects.stream().map(o -> new DbObject(o, field.getConnectedSchema()))
				.collect(Collectors.toList());
		} else {
			throw new DbAdminException("Cannot traverse field " + field.getName() + " in class " + schema.getClassName());
		}
	}
	
	public DbFieldValue get(String name) {
		Method getter = findGetter(name);
		
		if (getter == null)
			throw new DbAdminException("Unable to find getter method for field `"
				+ name + "` in class " + instance.getClass());

		try {
			Object result = getter.invoke(instance);
			return new DbFieldValue(result, schema.getFieldByJavaName(name));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new DbAdminException(e);
		}
	}
	
	public Object getPrimaryKeyValue() {
		DbField primaryKeyField = schema.getPrimaryKey();
		Method getter = findGetter(primaryKeyField.getJavaName());
		
		if (getter == null)
			throw new DbAdminException("Unable to find getter method for field `"
				+ primaryKeyField.getJavaName() + "` in class " + instance.getClass());
		
		try {
			Object result = getter.invoke(instance);
			return result;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new DbAdminException(e);
		}
	}
	
	public String getDisplayName() {
		Method[] methods = instance.getClass().getMethods();
		
		Optional<Method> displayNameMethod = 
			Arrays.stream(methods)
			      .filter(m -> m.getAnnotation(DisplayName.class) != null)
			      .findFirst();
		
		if (displayNameMethod.isPresent()) {
			try {
				return displayNameMethod.get().invoke(instance).toString();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new DbAdminException(e);
			}
		} else {
			return getPrimaryKeyValue().toString();
		}
	}
	
	public List<String> getComputedColumns() {
		return schema.getComputedColumnNames();
	}
	
	public Object compute(String column) {
		Method method = schema.getComputedColumn(column);
		
		if (method == null)
			throw new DbAdminException("Unable to find mapped method for @ComputedColumn " + column);
		
		try {
			return method.invoke(instance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new DbAdminException("Error while calling @ComputedColumn " + column
					+ " on class " + schema.getClassName());
		}
	}
	
//	public void initializeFromMap(Map<String, String> values) {
////		String pkValue = values.get(schema.getPrimaryKey().getName());
//		
//		List<String> fields = 
//			values.keySet().stream().filter(f -> !f.startsWith("__dbadmin_")).collect(Collectors.toList());
//		
//		for (String field : fields) {
//			String fieldJavaName = Utils.snakeToCamel(field);
//			Method setter = findSetter(fieldJavaName);
//			if (setter == null)
//				throw new DbAdminException("Unable to find setter for field " + fieldJavaName + " in class " + schema.getClassName());
//			
//			try {
//				setter.invoke(instance, values.get(field));
//			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				throw new DbAdminException(e);
//			}
//		}
//	}
	
	public void set(String fieldName, Object value) {
		Method setter = findSetter(fieldName);
		
		if (setter == null) {
			throw new DbAdminException("Unable to find setter method for " + fieldName + " in " + schema.getClassName());
		}
		
		try {
			setter.invoke(instance, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private Method findSetter(String fieldName) {
		fieldName = Utils.snakeToCamel(fieldName);
		String capitalize = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		Method[] methods = instance.getClass().getDeclaredMethods();
		
		for (Method m : methods) {
			if (m.getName().equals("set" + capitalize))
				return m;
		}
		
		return null;
	}
	
	private Method findGetter(String fieldName) {
		String capitalize = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		Method[] methods = instance.getClass().getDeclaredMethods();
		
		for (Method m : methods) {
			if (m.getName().equals("get" + capitalize))
				return m;
		}
		
		return null;
	}
}
