package tech.ailef.snapadmin.external.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define hierarchical tree structures for entities.
 * Can be applied to entity classes (to mark as root) or fields (to mark as
 * children).
 * 
 * Example usage:
 * 
 * <pre>
 * {@literal @}Entity
 * {@literal @}SnapTree(root = true, label = "Vehicle Hierarchy", icon = "bi bi-building")
 * public class Brand {
 *     {@literal @}OneToMany(mappedBy = "brand")
 *     {@literal @}SnapTree(childLabel = "Models", icon = "bi bi-car")
 *     private Set&lt;VehicleModel&gt; vehicleModels;
 * }
 * </pre>
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SnapTree {
    /**
     * If true, marks this entity as a root node in the tree.
     * Only applies to {@literal @}Entity classes.
     * 
     * @return true if this is a root node
     */
    boolean root() default false;

    /**
     * The label/title for this tree (only for root nodes).
     * E.g., "Vehicle Hierarchy", "Part Categories"
     * 
     * @return the tree label
     */
    String label() default "";

    /**
     * The label for child nodes (only for fields).
     * E.g., "Models", "Compatible Parts"
     * 
     * @return the child label
     */
    String childLabel() default "";

    /**
     * Icon class for the node (optional).
     * Uses Bootstrap Icons by default.
     * E.g., "bi bi-car", "bi bi-gear"
     * 
     * @return the icon class
     */
    String icon() default "";

    /**
     * Order/priority for displaying this tree or child collection.
     * Lower numbers appear first.
     * 
     * @return the display order
     */
    int order() default 0;
}
