package tech.ailef.snapadmin.external.dto;

/**
 * Data Transfer Object representing a node in the hierarchy tree.
 * Used for rendering the tree structure in the UI.
 */
public class TreeNodeDTO {
    private String id; // Entity ID
    private String label; // Display name (from getDisplayName())
    private String icon; // Icon class (e.g., "bi bi-car")
    private String type; // Entity class name (fully qualified)
    private boolean hasChildren; // Whether this node can be expanded
    private boolean isRoot; // Is this a root node?
    private String childField; // Field name to fetch children (if hasChildren)
    private String childType; // Type of the child entity (for Add Child action)
    private String childLabel; // Label for the child entity (e.g. "Model")
    private String inverseFieldName; // For many-to-many: field name in child entity pointing back to parent

    public TreeNodeDTO() {
    }

    public TreeNodeDTO(String id, String label, String type) {
        this.id = id;
        this.label = label;
        this.type = type;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getChildField() {
        return childField;
    }

    public void setChildField(String childField) {
        this.childField = childField;
    }

    public String getChildType() {
        return childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public String getChildLabel() {
        return childLabel;
    }

    public void setChildLabel(String childLabel) {
        this.childLabel = childLabel;
    }

    public String getInverseFieldName() {
        return inverseFieldName;
    }

    public void setInverseFieldName(String inverseFieldName) {
        this.inverseFieldName = inverseFieldName;
    }

    @Override
    public String toString() {
        return "TreeNodeDTO{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", type='" + type + '\'' +
                ", hasChildren=" + hasChildren +
                '}';
    }
}
