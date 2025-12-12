package tech.ailef.snapadmin.external.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.annotations.SnapTree;
import tech.ailef.snapadmin.external.dto.TreeConfiguration;
import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Service responsible for discovering and caching tree configurations
 * based on @SnapTree annotations in entity classes.
 */
@Service
public class TreeDiscoveryService {

    private final SnapAdmin snapAdmin;
    private Map<String, TreeConfiguration> treeConfigurations; // className -> config

    public TreeDiscoveryService(SnapAdmin snapAdmin) {
        this.snapAdmin = snapAdmin;
    }

    /**
     * Scan all registered schemas for @SnapTree annotations
     * and build tree configurations.
     */
    @PostConstruct
    public void discoverTrees() {
        treeConfigurations = new HashMap<>();

        // Get all registered schemas from SnapAdmin
        List<DbObjectSchema> schemas = snapAdmin.getSchemas();

        for (DbObjectSchema schema : schemas) {
            Class<?> entityClass = schema.getJavaClass();

            // Check if this entity is a tree root
            if (entityClass.isAnnotationPresent(SnapTree.class)) {
                SnapTree treeAnnotation = entityClass.getAnnotation(SnapTree.class);

                if (treeAnnotation.root()) {
                    // This is a root entity - create a tree configuration
                    TreeConfiguration config = new TreeConfiguration(
                            entityClass.getName(),
                            treeAnnotation.label().isEmpty() ? entityClass.getSimpleName() : treeAnnotation.label(),
                            treeAnnotation.icon().isEmpty() ? "bi bi-diagram-3" : treeAnnotation.icon());

                    // Scan for child fields in this entity and all entities it references
                    scanChildFields(entityClass, config);

                    treeConfigurations.put(entityClass.getName(), config);
                }
            }
        }
    }

    /**
     * Recursively scan an entity class for fields annotated with @SnapTree.
     */
    private void scanChildFields(Class<?> entityClass, TreeConfiguration config) {
        // Get all fields from the class
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(SnapTree.class)) {
                SnapTree fieldAnnotation = field.getAnnotation(SnapTree.class);

                // This field represents a child collection
                String childLabel = fieldAnnotation.childLabel().isEmpty()
                        ? field.getName()
                        : fieldAnnotation.childLabel();
                String icon = fieldAnnotation.icon().isEmpty()
                        ? "bi bi-folder"
                        : fieldAnnotation.icon();
                int order = fieldAnnotation.order();

                config.addChildField(field.getName(), childLabel, icon, order);
            }
        }
    }

    /**
     * Get all discovered tree configurations.
     */
    public List<TreeConfiguration> getAllTrees() {
        return new ArrayList<>(treeConfigurations.values());
    }

    /**
     * Get tree configuration for a specific entity class.
     */
    public TreeConfiguration getTreeForEntity(String entityClassName) {
        return treeConfigurations.get(entityClassName);
    }

    /**
     * Get all fields annotated with @SnapTree for a given entity class.
     */
    public List<Field> getChildFields(Class<?> entityClass) {
        List<Field> childFields = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(SnapTree.class)) {
                childFields.add(field);
            }
        }

        return childFields;
    }

    /**
     * Check if any trees have been discovered.
     */
    public boolean hasTreeViews() {
        return !treeConfigurations.isEmpty();
    }

    /**
     * Get the configuration for a specific field in an entity.
     */
    public TreeConfiguration.ChildFieldConfig getChildFieldConfig(String entityClassName, String fieldName) {
        TreeConfiguration config = treeConfigurations.get(entityClassName);
        if (config != null) {
            return config.getChildFields().get(fieldName);
        }
        return null;
    }
}
