package tech.ailef.snapadmin.external.dto;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for a single tree hierarchy.
 * Contains metadata about the root entity and its child relationships.
 */
public class TreeConfiguration {
    private String rootEntityClass; // Fully qualified class name
    private String label; // Tree label (e.g., "Vehicle Hierarchy")
    private String icon; // Icon for root nodes
    private Map<String, ChildFieldConfig> childFields; // fieldName -> config

    public TreeConfiguration() {
        this.childFields = new HashMap<>();
    }

    public TreeConfiguration(String rootEntityClass, String label, String icon) {
        this.rootEntityClass = rootEntityClass;
        this.label = label;
        this.icon = icon;
        this.childFields = new HashMap<>();
    }

    // Getters and Setters
    public String getRootEntityClass() {
        return rootEntityClass;
    }

    public void setRootEntityClass(String rootEntityClass) {
        this.rootEntityClass = rootEntityClass;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Map<String, ChildFieldConfig> getChildFields() {
        return childFields;
    }

    public void setChildFields(Map<String, ChildFieldConfig> childFields) {
        this.childFields = childFields;
    }

    public void addChildField(String fieldName, String childLabel, String icon, int order) {
        this.childFields.put(fieldName, new ChildFieldConfig(childLabel, icon, order));
    }

    /**
     * Configuration for a child field (collection relationship).
     */
    public static class ChildFieldConfig {
        private String label;
        private String icon;
        private int order;

        public ChildFieldConfig() {
        }

        public ChildFieldConfig(String label, String icon, int order) {
            this.label = label;
            this.icon = icon;
            this.order = order;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

    @Override
    public String toString() {
        return "TreeConfiguration{" +
                "rootEntityClass='" + rootEntityClass + '\'' +
                ", label='" + label + '\'' +
                ", childFields=" + childFields.size() +
                '}';
    }
}
