package tech.ailef.snapadmin.external.dto;

import java.util.List;

public class TreeSearchResultDTO {
    private String nodeId;
    private String label;
    private String type;
    private List<String> path; // List of IDs from root to this node: [rootId, childId, ..., nodeId]

    public TreeSearchResultDTO(String nodeId, String label, String type, List<String> path) {
        this.nodeId = nodeId;
        this.label = label;
        this.type = type;
        this.path = path;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }
}
