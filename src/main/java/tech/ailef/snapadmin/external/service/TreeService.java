package tech.ailef.snapadmin.external.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.annotations.SnapTree;
import tech.ailef.snapadmin.external.dbmapping.DbObject;
import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.snapadmin.external.dto.TreeConfiguration;
import tech.ailef.snapadmin.external.dto.TreeNodeDTO;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TreeService {

    private final SnapAdmin snapAdmin;
    private final TreeDiscoveryService treeDiscoveryService;

    public TreeService(SnapAdmin snapAdmin, TreeDiscoveryService treeDiscoveryService) {
        this.snapAdmin = snapAdmin;
        this.treeDiscoveryService = treeDiscoveryService;
    }

    public List<TreeNodeDTO> fetchRoots(String entityClassName) {
        DbObjectSchema schema = snapAdmin.findSchemaByClassName(entityClassName);
        List<DbObject> objects = schema.findAll();

        List<TreeNodeDTO> nodes = new ArrayList<>();
        TreeConfiguration treeConfig = treeDiscoveryService.getTreeForEntity(entityClassName);

        for (DbObject obj : objects) {
            TreeNodeDTO dto = toDTO(obj);
            // Use root icon if available
            if (treeConfig != null && treeConfig.getIcon() != null && !treeConfig.getIcon().isEmpty()) {
                dto.setIcon(treeConfig.getIcon());
            }
            dto.setRoot(true);
            nodes.add(dto);
        }

        return nodes;
    }

    public List<TreeNodeDTO> fetchChildren(String parentClass, String parentId, String fieldName) {
        DbObjectSchema parentSchema = snapAdmin.findSchemaByClassName(parentClass);

        // Parse ID
        Object id = parentSchema.getPrimaryKey().getType().parseValue(parentId);
        Optional<?> entityOpt = parentSchema.getJpaRepository().findById(id);

        if (entityOpt.isEmpty()) {
            return new ArrayList<>();
        }

        // Validate that the field exists and is a collection relationship
        tech.ailef.snapadmin.external.dbmapping.fields.DbField dbField = parentSchema.getFieldByJavaName(fieldName);
        if (dbField == null) {
            return new ArrayList<>();
        }

        // Check if this field is a collection relationship (@OneToMany or @ManyToMany)
        boolean isOneToMany = dbField.getPrimitiveField().getAnnotation(OneToMany.class) != null;
        boolean isManyToMany = dbField.getPrimitiveField().getAnnotation(ManyToMany.class) != null;

        if (!isOneToMany && !isManyToMany) {
            return new ArrayList<>();
        }

        DbObject parentObj = new DbObject(entityOpt.get(), parentSchema);
        List<DbObject> children = parentObj.traverseMany(dbField);

        // Determine icon from parent field annotation
        String childIcon = null;
        try {
            Field field = parentSchema.getJavaClass().getDeclaredField(fieldName);
            if (field.isAnnotationPresent(SnapTree.class)) {
                childIcon = field.getAnnotation(SnapTree.class).icon();
            }
        } catch (Exception e) {
            // Field not found or other error, ignore
        }

        List<TreeNodeDTO> nodes = new ArrayList<>();
        for (DbObject child : children) {
            TreeNodeDTO dto = toDTO(child);
            if ((dto.getIcon() == null || dto.getIcon().isEmpty()) && childIcon != null && !childIcon.isEmpty()) {
                dto.setIcon(childIcon);
            }
            nodes.add(dto);
        }

        return nodes;
    }

    private TreeNodeDTO toDTO(DbObject obj) {
        TreeNodeDTO dto = new TreeNodeDTO();
        dto.setId(obj.getPrimaryKeyValue().toString());
        dto.setLabel(obj.getDisplayName());
        dto.setType(obj.getSchema().getClassName());

        // Check for class-level icon
        if (obj.getSchema().getJavaClass().isAnnotationPresent(SnapTree.class)) {
            SnapTree ann = obj.getSchema().getJavaClass().getAnnotation(SnapTree.class);
            if (!ann.icon().isEmpty()) {
                dto.setIcon(ann.icon());
            }
        }

        List<Field> childFields = treeDiscoveryService.getChildFields(obj.getSchema().getJavaClass());
        dto.setHasChildren(!childFields.isEmpty());
        if (!childFields.isEmpty()) {
            // Default to the first child field
            Field childField = childFields.get(0);
            dto.setChildField(childField.getName());

            // Determine child type and label
            try {
                // Get the generic type of the collection (e.g., List<Model> -> Model)
                ParameterizedType stringListType = (ParameterizedType) childField
                        .getGenericType();
                Class<?> childClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                dto.setChildType(childClass.getName());

                // Get label from annotation
                if (childField.isAnnotationPresent(SnapTree.class)) {
                    SnapTree ann = childField.getAnnotation(SnapTree.class);
                    String label = ann.childLabel();
                    if (label.isEmpty()) {
                        label = childClass.getSimpleName();
                    }
                    dto.setChildLabel(label);
                } else {
                    dto.setChildLabel(childClass.getSimpleName());
                }

                // For many-to-many relationships, find the inverse field name
                if (childField.isAnnotationPresent(ManyToMany.class)) {
                    String inverseField = findInverseField(childClass, obj.getSchema().getJavaClass());
                    dto.setInverseFieldName(inverseField);
                }
            } catch (Exception e) {
                // Fallback or error handling
                e.printStackTrace();
            }
        }

        return dto;
    }

    /**
     * Links two existing nodes (entities) by adding the child to the parent's
     * collection.
     * 
     * @param parentClass The class name of the parent entity
     * @param parentId    The ID of the parent entity
     * @param childClass  The class name of the child entity
     * @param childId     The ID of the child entity
     * @param fieldName   The name of the collection field in the parent entity
     */
    @org.springframework.transaction.annotation.Transactional
    public void linkNodes(String parentClass, String parentId, String childClass, String childId, String fieldName) {
        DbObjectSchema parentSchema = snapAdmin.findSchemaByClassName(parentClass);
        DbObjectSchema childSchema = snapAdmin.findSchemaByClassName(childClass);

        if (parentSchema == null || childSchema == null) {
            throw new RuntimeException("Invalid parent or child class");
        }

        Object parentIdObj = parentSchema.getPrimaryKey().getType().parseValue(parentId);
        Object childIdObj = childSchema.getPrimaryKey().getType().parseValue(childId);

        Optional<?> parentOpt = parentSchema.getJpaRepository().findById(parentIdObj);
        Optional<?> childOpt = childSchema.getJpaRepository().findById(childIdObj);

        if (parentOpt.isPresent() && childOpt.isPresent()) {
            Object parent = parentOpt.get();
            Object child = childOpt.get();

            try {
                Field field = parent.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                java.util.Collection<Object> collection = (java.util.Collection<Object>) field.get(parent);

                if (collection == null) {
                    collection = new ArrayList<>();
                    field.set(parent, collection);
                }

                collection.add(child);
                parentSchema.getJpaRepository().save(parent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to link nodes: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("Parent or Child entity not found");
        }
    }

    /**
     * Finds the field name in the child class that references the parent class
     * in a many-to-many relationship.
     * 
     * @param childClass  The child entity class
     * @param parentClass The parent entity class
     * @return The field name in the child class, or null if not found
     */
    private String findInverseField(Class<?> childClass, Class<?> parentClass) {
        for (Field field : childClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
                try {
                    // Check if this field's generic type matches the parent class
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                        Class<?> fieldType = (Class<?>) paramType.getActualTypeArguments()[0];
                        if (fieldType.equals(parentClass)) {
                            return tech.ailef.snapadmin.external.misc.Utils.camelToSnake(field.getName());
                        }
                    }
                } catch (Exception e) {
                    // Ignore and continue
                }
            }
        }
        return null;
    }
}
