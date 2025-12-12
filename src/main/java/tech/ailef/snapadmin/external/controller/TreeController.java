package tech.ailef.snapadmin.external.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.ailef.snapadmin.external.SnapAdminProperties;
import tech.ailef.snapadmin.external.dto.TreeConfiguration;
import tech.ailef.snapadmin.external.dto.TreeNodeDTO;
import tech.ailef.snapadmin.external.dto.TreeSearchResultDTO;
import tech.ailef.snapadmin.external.service.TreeDiscoveryService;
import tech.ailef.snapadmin.external.service.TreeService;
import tech.ailef.snapadmin.external.service.TreeSearchService;

import java.util.List;

@Controller
@RequestMapping(value = { "/${snapadmin.baseUrl}", "/${snapadmin.baseUrl}/" })
public class TreeController {

    @Autowired
    private TreeDiscoveryService treeDiscoveryService;

    @Autowired
    private TreeService treeService;

    @Autowired
    private TreeSearchService treeSearchService;

    @Autowired
    private SnapAdminProperties properties;

    @GetMapping("/tree")
    public String showTreePage(Model model) {
        List<TreeConfiguration> trees = treeDiscoveryService.getAllTrees();

        if (trees.isEmpty()) {
            model.addAttribute("error", "No hierarchy trees found.");
            model.addAttribute("status", "404");
            model.addAttribute("message", "Annotate your entities with @SnapTree to see them here.");
            model.addAttribute("activePage", "tree");
            return "snapadmin/other/error";
        }

        // Default to the first tree
        TreeConfiguration defaultTree = trees.get(0);
        return showSpecificTree(defaultTree.getRootEntityClass(), model);
    }

    @GetMapping("/tree/{className}")
    public String showSpecificTree(@PathVariable String className, Model model) {
        TreeConfiguration treeConfig = treeDiscoveryService.getTreeForEntity(className);

        if (treeConfig == null) {
            return "redirect:/" + properties.getBaseUrl() + "/tree";
        }

        model.addAttribute("treeConfig", treeConfig);
        model.addAttribute("allTrees", treeDiscoveryService.getAllTrees());
        model.addAttribute("activePage", "tree");
        model.addAttribute("title", "Hierarchy | " + treeConfig.getLabel());

        return "snapadmin/tree";
    }

    @GetMapping("/api/tree/roots/{entityClass}")
    @ResponseBody
    public List<TreeNodeDTO> getRootNodes(@PathVariable String entityClass) {
        return treeService.fetchRoots(entityClass);
    }

    @GetMapping("/api/tree/children/{entityClass}/{id}/{field}")
    @ResponseBody
    public List<TreeNodeDTO> getChildren(
            @PathVariable String entityClass,
            @PathVariable String id,
            @PathVariable String field) {
        return treeService.fetchChildren(entityClass, id, field);
    }

    @GetMapping("/api/tree/search")
    @ResponseBody
    public List<TreeSearchResultDTO> search(
            @RequestParam String q,
            @RequestParam String rootClass) {
        return treeSearchService.search(q, rootClass);
    }

    @org.springframework.web.bind.annotation.PostMapping("/api/tree/link")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> linkNodes(
            @RequestParam String parentClass,
            @RequestParam String parentId,
            @RequestParam String childClass,
            @RequestParam String childId,
            @RequestParam String field) {
        try {
            treeService.linkNodes(parentClass, parentId, childClass, childId, field);
            return org.springframework.http.ResponseEntity.ok().build();
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
