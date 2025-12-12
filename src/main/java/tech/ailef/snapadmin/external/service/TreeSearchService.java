package tech.ailef.snapadmin.external.service;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import org.springframework.stereotype.Service;
import tech.ailef.snapadmin.external.SnapAdmin;
import tech.ailef.snapadmin.external.annotations.SnapTree;
import tech.ailef.snapadmin.external.dbmapping.DbObject;
import tech.ailef.snapadmin.external.dbmapping.DbObjectSchema;
import tech.ailef.snapadmin.external.dbmapping.SnapAdminRepository;
import tech.ailef.snapadmin.external.dbmapping.fields.DbField;
import tech.ailef.snapadmin.external.dto.TreeSearchResultDTO;
import tech.ailef.snapadmin.external.dto.TreeNodeDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TreeSearchService {

    private final SnapAdmin snapAdmin;
    private final SnapAdminRepository snapAdminRepository;

    public TreeSearchService(SnapAdmin snapAdmin, SnapAdminRepository snapAdminRepository) {
        this.snapAdmin = snapAdmin;
        this.snapAdminRepository = snapAdminRepository;
    }

    public List<TreeSearchResultDTO> search(String query, String rootClassName) {
        List<TreeSearchResultDTO> results = new ArrayList<>();

        // 1. Iterate over all schemas
        for (DbObjectSchema schema : snapAdmin.getSchemas()) {
            // Perform search
            List<DbObject> matches = snapAdminRepository.search(schema, query);

            for (DbObject match : matches) {
                // Try to find all paths to root
                List<List<TreeNodeDTO>> paths = findPathsToRoot(match, rootClassName, 0);

                for (List<TreeNodeDTO> path : paths) {
                    // Path is [Root, Child, ..., Match]

                    // Build full hierarchy path: "Root > Child > Match"
                    String label;
                    if (path.size() > 1) {
                        // Include parent context with > separator
                        String context = path.stream()
                                .limit(path.size() - 1) // Exclude the match itself
                                .map(TreeNodeDTO::getLabel)
                                .collect(Collectors.joining(" > "));
                        label = context + " > " + match.getDisplayName();
                    } else {
                        // No parent context, just the match
                        label = match.getDisplayName();
                    }

                    // DTO needs list of IDs for highlighting
                    List<String> idPath = path.stream()
                            .map(TreeNodeDTO::getId)
                            .collect(Collectors.toList());

                    results.add(new TreeSearchResultDTO(
                            match.getPrimaryKeyValue().toString(),
                            label,
                            schema.getClassName(),
                            idPath));
                }
            }
        }

        return results;
    }

    private List<List<TreeNodeDTO>> findPathsToRoot(DbObject node, String rootClassName, int depth) {
        TreeNodeDTO currentNode = new TreeNodeDTO();
        currentNode.setId(node.getPrimaryKeyValue().toString());
        currentNode.setLabel(node.getDisplayName());
        currentNode.setType(node.getSchema().getClassName());

        if (depth > 5)
            return Collections.emptyList();

        if (node.getSchema().getClassName().equals(rootClassName)) {
            List<TreeNodeDTO> path = new ArrayList<>();
            path.add(currentNode);
            return Collections.singletonList(path);
        }

        List<List<TreeNodeDTO>> allPaths = new ArrayList<>();

        // 1. Check ManyToOne fields (Parent)
        for (DbField field : node.getSchema().getFields()) {
            if (field.getPrimitiveField().isAnnotationPresent(ManyToOne.class)) {
                try {
                    Object parentVal = node.get(field.getJavaName()).getValue();
                    if (parentVal != null) {
                        DbObject parent = new DbObject(parentVal, field.getConnectedSchema());
                        List<List<TreeNodeDTO>> parentPaths = findPathsToRoot(parent, rootClassName, depth + 1);
                        for (List<TreeNodeDTO> p : parentPaths) {
                            List<TreeNodeDTO> newPath = new ArrayList<>(p);
                            newPath.add(currentNode);
                            allPaths.add(newPath);
                        }
                    }
                } catch (Exception e) {
                    // Ignore error accessing field
                }
            }
        }

        // 2. Check ManyToMany fields (Parent)
        for (DbField field : node.getSchema().getFields()) {
            // Skip if it's a child link (annotated with @SnapTree)
            if (field.getPrimitiveField()
                    .isAnnotationPresent(SnapTree.class)) {
                continue;
            }

            if (field.getPrimitiveField().isAnnotationPresent(ManyToMany.class)) {
                try {
                    // This returns a collection
                    List<DbObject> parents = node.traverseMany(field);
                    for (DbObject parent : parents) {
                        List<List<TreeNodeDTO>> parentPaths = findPathsToRoot(parent, rootClassName, depth + 1);
                        for (List<TreeNodeDTO> p : parentPaths) {
                            List<TreeNodeDTO> newPath = new ArrayList<>(p);
                            newPath.add(currentNode);
                            allPaths.add(newPath);
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        return allPaths;
    }
}
