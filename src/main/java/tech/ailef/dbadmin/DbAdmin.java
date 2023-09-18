package tech.ailef.dbadmin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceContext;
import tech.ailef.dbadmin.annotations.DbAdminAppConfiguration;
import tech.ailef.dbadmin.annotations.DbAdminConfiguration;
import tech.ailef.dbadmin.annotations.DisplayFormat;
import tech.ailef.dbadmin.dbmapping.AdvancedJpaRepository;
import tech.ailef.dbadmin.dbmapping.DbField;
import tech.ailef.dbadmin.dbmapping.DbFieldType;
import tech.ailef.dbadmin.dbmapping.DbObjectSchema;
import tech.ailef.dbadmin.exceptions.DbAdminException;
import tech.ailef.dbadmin.misc.Utils;

@Component
public class DbAdmin {
	@PersistenceContext
	private EntityManager entityManager;
	
	private List<DbObjectSchema> schemas = new ArrayList<>();
	
	private String modelsPackage;
	
	public DbAdmin(@Autowired EntityManager entityManager) {
		Map<String, Object> beansWithAnnotation = 
			ApplicationContextUtils.getApplicationContext().getBeansWithAnnotation(DbAdminConfiguration.class);
		
		if (beansWithAnnotation.size() != 1) {
			throw new DbAdminException("Found " + beansWithAnnotation.size() + " beans with annotation @DbAdminConfiguration, but must be unique");
		}
		
		DbAdminAppConfiguration applicationClass = (DbAdminAppConfiguration) beansWithAnnotation.values().iterator().next();
		
		this.modelsPackage = applicationClass.getModelsPackage();
		this.entityManager = entityManager;
		
    	init();
	}

	public void init() {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		
		Set<BeanDefinition> beanDefs = provider.findCandidateComponents(modelsPackage);
		for (BeanDefinition bd : beanDefs) {
			schemas.add(processBeanDefinition(bd));
		}
	}
	
	private DbObjectSchema processBeanDefinition(BeanDefinition bd) {
		String fullClassName = bd.getBeanClassName();
		
		try {
			Class<?> klass = Class.forName(fullClassName);
			DbObjectSchema schema = new DbObjectSchema(klass, this);
			AdvancedJpaRepository simpleJpaRepository = new AdvancedJpaRepository(schema, entityManager);
			schema.setJpaRepository(simpleJpaRepository);
				
			System.out.println("\n\n******************************************************");
			System.out.println("* Class: " + klass + " - Table: " + schema.getTableName());
			System.out.println("******************************************************");
			
			Field[] fields = klass.getDeclaredFields();
			for (Field f : fields) {
				System.out.println(" - Mapping field " + f);
				DbField field = mapField(f, schema);
				if (field == null) {
//					continue;
					// TODO: CHECK THIS EXCEPTION
					throw new DbAdminException("IMPOSSIBLE TO MAP FIELD: " + f);
				}
				field.setSchema(schema);
				
				schema.addField(field);
				System.out.println(field);
			}
			
			return schema;
		} catch (ClassNotFoundException |
				IllegalArgumentException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Determines the name for the given field, by transforming it to snake_case
	 * and checking if the `@Column` annotation is present.
	 * @param f
	 * @return
	 */
	private String determineFieldName(Field f) {
		Column[] columnAnnotations = f.getAnnotationsByType(Column.class);
		String fieldName = Utils.camelToSnake(f.getName());
		
		if (columnAnnotations.length != 0) {
			Column col = columnAnnotations[0];
			if (col.name() != null && !col.name().isBlank())
				fieldName = col.name();
		}
		
		return fieldName;
	}
	
	private boolean determineNullable(Field f) {
		Column[] columnAnnotations = f.getAnnotationsByType(Column.class);

		boolean nullable = true;
		if (columnAnnotations.length != 0) {
			Column col = columnAnnotations[0];
			nullable = col.nullable();
		}
		
		return nullable;
	}
	
	private DbField mapField(Field f, DbObjectSchema schema) {
		OneToMany oneToMany = f.getAnnotation(OneToMany.class);
		ManyToMany manyToMany = f.getAnnotation(ManyToMany.class);
		ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
		OneToOne oneToOne = f.getAnnotation(OneToOne.class);
		
		String fieldName = determineFieldName(f);
		
		// This will contain the type of the entity linked by the
		// foreign key, if any
		Class<?> connectedType = null;
		
		// Try to assign default field type
		DbFieldType fieldType = null;
		try {
			fieldType = DbFieldType.fromClass(f.getType());
		} catch (DbAdminException e) {
			// If failure, we try to map a relationship on this field
		}

		if (manyToOne != null || oneToOne != null) {
			fieldName = mapRelationshipJoinColumn(f);
			fieldType = mapForeignKeyType(f.getType());
			connectedType = f.getType();
		}
		
		if (manyToMany != null || oneToMany != null) {
			ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
	        Class<?> targetEntityClass = (Class<?>) stringListType.getActualTypeArguments()[0];
	        fieldType = mapForeignKeyType(targetEntityClass);
	        connectedType = targetEntityClass;
		}
		
		if (fieldType == null) {
			throw new DbAdminException("Unable to determine fieldType for " + f.getType());
		}
		
		DisplayFormat displayFormat = f.getAnnotation(DisplayFormat.class);
		
		DbField field = new DbField(f.getName(), fieldName, f, fieldType, schema, displayFormat != null ? displayFormat.format() : null);
		field.setConnectedType(connectedType);
		
		Id[] idAnnotations = f.getAnnotationsByType(Id.class);
		field.setPrimaryKey(idAnnotations.length != 0);
		
		field.setNullable(determineNullable(f));
		
		if (field.isPrimaryKey())
			field.setNullable(false);
		
		return field;
	}
	
	/**
	 * Returns the join column name for the relationship defined on 
	 * the input Field object.
	 * @param f
	 * @return
	 */
	private String mapRelationshipJoinColumn(Field f) {
		String joinColumnName = Utils.camelToSnake(f.getName()) + "_id"; 
		JoinColumn[] joinColumn = f.getAnnotationsByType(JoinColumn.class);
		if (joinColumn.length != 0) {
			joinColumnName = joinColumn[0].name();
		}
		return joinColumnName;

	}
	
	/**
	 * Returns the type of a foreign key field, by looking at the type
	 * of the primary key (defined as `@Id`) in the referenced table.
	 * 
	 * @param f
	 * @return
	 */
	private DbFieldType mapForeignKeyType(Class<?> entityClass) {
		try {
			Object linkedEntity = entityClass.getConstructor().newInstance();
			Class<?> linkType = null;
			
			for (Field ef : linkedEntity.getClass().getDeclaredFields()) {
				if (ef.getAnnotationsByType(Id.class).length != 0) {
					linkType = ef.getType();
				}
			}
			
			if (linkType == null)
				throw new DbAdminException("Unable to find @Id field in Entity class " + entityClass);
			
			return DbFieldType.fromClass(linkType);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new DbAdminException(e);
		}
	}
	
	public String getBasePackage() {
		return modelsPackage;
	}
	
	public List<DbObjectSchema> getSchemas() {
		return Collections.unmodifiableList(schemas);
	}
	
	public DbObjectSchema findSchemaByClassName(String className) {
		return schemas.stream().filter(s -> s.getClassName().equals(className)).findFirst().orElseThrow(() -> {
			return new DbAdminException("Schema " + className + " not found.");
		});
	}
	
	public DbObjectSchema findSchemaByClass(Class<?> klass) {
		return findSchemaByClassName(klass.getName());
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
