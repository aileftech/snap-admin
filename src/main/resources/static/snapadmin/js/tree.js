document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('tree-container');
    if (!container) return;

    const rootClass = container.dataset.rootClass;
    const baseUrl = container.dataset.baseUrl;

    loadRoots(rootClass, container, baseUrl);

    // Search Logic
    const searchInput = document.getElementById('tree-search');
    const searchResults = document.getElementById('search-results');
    let searchTimeout;

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            const query = e.target.value;

            if (query.length < 2) {
                searchResults.classList.add('d-none');
                return;
            }

            searchTimeout = setTimeout(() => performSearch(query, rootClass, baseUrl, searchResults), 300);
        });

        // Hide results when clicking outside
        document.addEventListener('click', (e) => {
            if (!searchInput.contains(e.target) && !searchResults.contains(e.target)) {
                searchResults.classList.add('d-none');
            }
        });
    }

    // Context Menu Logic
    const contextMenu = document.getElementById('tree-context-menu');
    let contextMenuNode = null;

    // Hide context menu when clicking outside or pressing Escape
    document.addEventListener('click', () => hideContextMenu());
    document.addEventListener('contextmenu', (e) => {
        // Only prevent default if not on tree node (to allow default context menu elsewhere)
        if (!e.target.closest('.tree-node-content')) {
            hideContextMenu();
        }
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') hideContextMenu();
    });

    // Context menu item handlers
    if (contextMenu) {
        contextMenu.addEventListener('click', (e) => {
            e.stopPropagation();
            const menuItem = e.target.closest('.context-menu-item');
            if (!menuItem || !contextMenuNode) return;

            const action = menuItem.dataset.action;
            if (action === 'edit') {
                handleEdit(contextMenuNode, baseUrl);
            } else if (action === 'delete') {
                handleDelete(contextMenuNode, baseUrl);
            } else if (action === 'add-child') {
                handleAddChild(contextMenuNode, baseUrl);
            } else if (action === 'link-existing') {
                handleLinkExisting(contextMenuNode);
            }
            hideContextMenu();
        });
    }

    // Expose context menu functions globally for use in createNodeElement
    window.showTreeContextMenu = function (event, node, nodeElement) {
        event.preventDefault();
        event.stopPropagation();

        contextMenuNode = { node, nodeElement };

        // Show/Hide "Add Child" option
        const addChildItem = contextMenu.querySelector('[data-action="add-child"]');
        const linkExistingItem = contextMenu.querySelector('[data-action="link-existing"]');
        const divider = contextMenu.querySelector('.dropdown-divider');

        if (addChildItem) {
            if (node.childType) {
                addChildItem.style.display = 'flex';
                if (divider) divider.style.display = 'block';

                // Update label
                const labelSpan = addChildItem.querySelector('#add-child-label');
                if (labelSpan) {
                    labelSpan.textContent = `Add ${node.childLabel || 'Child'}`;
                }
            } else {
                addChildItem.style.display = 'none';
                if (divider) divider.style.display = 'none';
            }
        }

        // Show/Hide "Link Existing" option
        if (linkExistingItem) {
            // Only show for Many-to-Many relationships (indicated by presence of inverseFieldName)
            if (node.inverseFieldName) {
                linkExistingItem.style.display = 'flex';
            } else {
                linkExistingItem.style.display = 'none';
            }
        }

        contextMenu.style.display = 'block';
        contextMenu.style.left = event.pageX + 'px';
        contextMenu.style.top = event.pageY + 'px';
    };

    function hideContextMenu() {
        if (contextMenu) {
            contextMenu.style.display = 'none';
            contextMenuNode = null;
        }
    }
});

async function loadRoots(entityClass, container, baseUrl) {
    try {
        const response = await fetch(`/${baseUrl}/api/tree/roots/${entityClass}`);
        const roots = await response.json();

        container.innerHTML = ''; // Clear loading spinner

        if (roots.length === 0) {
            container.innerHTML = '<div class="text-muted text-center">No items found.</div>';
            return;
        }

        renderNodes(roots, container, baseUrl);
    } catch (error) {
        console.error('Error loading roots:', error);
        container.innerHTML = '<div class="text-danger">Error loading hierarchy.</div>';
    }
}

function renderNodes(nodes, container, baseUrl) {
    const ul = document.createElement('ul');
    ul.className = 'tree-list';

    nodes.forEach(node => {
        const li = createNodeElement(node, baseUrl);
        ul.appendChild(li);
    });

    container.appendChild(ul);
}

function createNodeElement(node, baseUrl) {
    const li = document.createElement('li');
    li.className = 'tree-node-wrapper';
    li.dataset.id = node.id;
    li.dataset.type = node.type;
    li.dataset.childField = node.childField || '';
    li.dataset.childType = node.childType || '';
    li.dataset.childLabel = node.childLabel || '';
    li.dataset.inverseFieldName = node.inverseFieldName || '';
    li.dataset.hasChildren = node.hasChildren;

    const content = document.createElement('div');
    content.className = 'tree-node-content';

    // Expand/collapse button
    const toggleBtn = document.createElement('button');
    toggleBtn.className = 'tree-toggle btn btn-sm btn-link text-decoration-none p-0';

    if (node.hasChildren) {
        toggleBtn.innerHTML = '<i class="bi bi-chevron-right text-muted"></i>';
        toggleBtn.onclick = (e) => {
            e.stopPropagation();
            toggleNode(node, li, toggleBtn, baseUrl);
        };
    } else {
        toggleBtn.innerHTML = '<i class="bi bi-dot text-muted" style="opacity: 0.5"></i>';
        toggleBtn.disabled = true;
        toggleBtn.style.cursor = 'default';
    }
    content.appendChild(toggleBtn);

    // Icon
    const icon = document.createElement('i');
    icon.className = (node.icon || 'bi bi-file') + ' me-2 ms-1 text-secondary';
    content.appendChild(icon);

    // Label
    const label = document.createElement('span');
    label.className = 'tree-label';
    label.textContent = node.label;
    label.onclick = () => openEntity(node, baseUrl);
    content.appendChild(label);

    // ID badge (optional)
    const idBadge = document.createElement('small');
    idBadge.className = 'text-muted ms-2 opacity-50';
    idBadge.textContent = `#${node.id}`;
    content.appendChild(idBadge);

    li.appendChild(content);

    // Add right-click context menu
    content.addEventListener('contextmenu', (e) => {
        if (window.showTreeContextMenu) {
            window.showTreeContextMenu(e, node, li);
        }
    });

    return li;
}

async function toggleNode(node, li, toggleBtn, baseUrl) {
    let childContainer = li.querySelector('.tree-children');
    const icon = toggleBtn.querySelector('i');

    if (childContainer) {
        // Already loaded, just toggle visibility
        if (childContainer.style.display === 'none') {
            childContainer.style.display = 'block';
            icon.classList.replace('bi-chevron-right', 'bi-chevron-down');
        } else {
            childContainer.style.display = 'none';
            icon.classList.replace('bi-chevron-down', 'bi-chevron-right');
        }
    } else {
        // Load children
        icon.className = 'spinner-border spinner-border-sm text-primary';
        icon.innerHTML = ''; // Remove chevron

        try {
            const children = await fetchChildren(node, baseUrl);

            // Restore icon
            icon.className = 'bi bi-chevron-down text-muted';

            childContainer = document.createElement('div');
            childContainer.className = 'tree-children ms-4';

            if (children.length === 0) {
                childContainer.innerHTML = '<div class="text-muted small fst-italic py-1">No children</div>';
            } else {
                renderNodes(children, childContainer, baseUrl);
            }

            li.appendChild(childContainer);
        } catch (error) {
            console.error('Error loading children:', error);
            icon.className = 'bi bi-exclamation-circle text-danger';
        }
    }
}

async function fetchChildren(node, baseUrl) {
    if (!node.childField) return [];
    const response = await fetch(`/${baseUrl}/api/tree/children/${node.type}/${node.id}/${node.childField}`);
    return await response.json();
}

function openEntity(node, baseUrl) {
    window.location.href = `/${baseUrl}/model/${node.type}/edit/${node.id}`;
}

async function performSearch(query, rootClass, baseUrl, resultsContainer) {
    try {
        const response = await fetch(`/${baseUrl}/api/tree/search?q=${encodeURIComponent(query)}&rootClass=${rootClass}`);
        const results = await response.json();
        displaySearchResults(results, resultsContainer, baseUrl);
    } catch (error) {
        console.error("Search error:", error);
    }
}

function displaySearchResults(results, container, baseUrl) {
    container.innerHTML = '';

    if (results.length === 0) {
        container.innerHTML = '<div class="list-group-item text-muted">No results found</div>';
    } else {
        results.forEach(result => {
            const item = document.createElement('a');
            item.href = '#';
            item.className = 'list-group-item list-group-item-action';
            item.innerHTML = `
                <div class="d-flex w-100 justify-content-between align-items-center">
                    <h6 class="mb-0 text-truncate" style="max-width: 70%;">${result.label}</h6>
                    <small class="text-muted">${result.type.split('.').pop()}</small>
                </div>
            `;
            item.onclick = (e) => {
                e.preventDefault();
                highlightPath(result.path, baseUrl);
                container.classList.add('d-none');
            };
            container.appendChild(item);
        });
    }

    container.classList.remove('d-none');
}

async function highlightPath(path, baseUrl) {
    // path is [rootId, childId, ..., targetId]

    // 1. Find root node
    const rootId = path[0];
    let currentNode = document.querySelector(`li.tree-node-wrapper[data-id="${rootId}"]`);

    if (!currentNode) {
        console.error("Root node not found", rootId);
        return;
    }

    // 2. Iterate through path
    for (let i = 0; i < path.length - 1; i++) {
        const currentId = path[i];
        const nextId = path[i + 1];

        // Ensure current node is expanded
        const li = currentNode;
        const toggleBtn = li.querySelector('.tree-toggle');

        if (toggleBtn && !toggleBtn.disabled) {
            // Check if already expanded
            const childContainer = li.querySelector('.tree-children');
            if (!childContainer) {
                // Not loaded, click to load
                const node = {
                    id: li.dataset.id,
                    type: li.dataset.type,
                    childField: li.dataset.childField,
                    hasChildren: li.dataset.hasChildren === 'true'
                };
                await toggleNode(node, li, toggleBtn, baseUrl);
            } else if (childContainer.style.display === 'none') {
                // Hidden, show it
                childContainer.style.display = 'block';
                li.querySelector('.tree-toggle i').classList.replace('bi-chevron-right', 'bi-chevron-down');
            }
        }

        // Find next node
        const nextNode = li.querySelector(`li.tree-node-wrapper[data-id="${nextId}"]`);
        if (!nextNode) {
            console.error("Next node not found in path", nextId);
            return;
        }
        currentNode = nextNode;
    }

    // 3. Highlight target
    document.querySelectorAll('.tree-node-content.highlight').forEach(el => el.classList.remove('highlight'));
    const content = currentNode.querySelector('.tree-node-content');
    if (content) {
        content.classList.add('highlight');
        content.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

// Context Menu Action Handlers
function handleEdit(contextMenuNode, baseUrl) {
    const { node } = contextMenuNode;
    // Redirect to edit page
    window.location.href = `/${baseUrl}/model/${node.type}/edit/${node.id}`;
}

async function handleDelete(contextMenuNode, baseUrl) {
    const { node, nodeElement } = contextMenuNode;

    // Confirm deletion
    const confirmed = confirm(`Are you sure you want to delete "${node.label}"?\n\nThis action cannot be undone.`);
    if (!confirmed) return;

    try {
        // Call delete API
        const response = await fetch(`/${baseUrl}/model/${node.type}/delete/${node.id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        });

        if (response.ok || response.redirected) {
            // Remove node from tree
            if (nodeElement && nodeElement.parentElement) {
                nodeElement.remove();

                // Show success message (optional)
                console.log(`Successfully deleted ${node.label}`);
            }
        } else {
            throw new Error('Delete failed');
        }
    } catch (error) {
        console.error('Error deleting node:', error);
        alert(`Failed to delete "${node.label}". Please try again.`);
    }
}

function handleAddChild(contextMenuNode, baseUrl) {
    const { node, nodeElement } = contextMenuNode;

    if (!node.childType) {
        console.error("No child type defined for this node");
        return;
    }

    // Construct URL for Create page
    // For many-to-many relationships, use the inverse field name if available
    // For many-to-one relationships, use the parent type + "_id" pattern

    let fieldName;
    const inverseFieldName = nodeElement.dataset.inverseFieldName;

    if (inverseFieldName && inverseFieldName !== '') {
        // Many-to-many relationship: use the inverse field name
        // The field expects an array of IDs, so we use fieldName[]
        fieldName = inverseFieldName + '[]';
    } else {
        // Many-to-one relationship: use the traditional pattern
        const parentTypeName = node.type.split('.').pop().toLowerCase();
        fieldName = parentTypeName + '_id';
    }

    window.location.href = `/${baseUrl}/model/${node.childType}/create?${encodeURIComponent(fieldName)}=${encodeURIComponent(node.id)}`;
}

// Link Existing Logic
let linkModal = null;
let linkSearchTimeout;
let selectedLinkItem = null;
let currentLinkContext = null;

document.addEventListener('DOMContentLoaded', function () {
    const modalEl = document.getElementById('linkExistingModal');
    if (modalEl) {
        linkModal = new bootstrap.Modal(modalEl);

        const searchInput = document.getElementById('link-search');
        const resultsContainer = document.getElementById('link-search-results');
        const confirmBtn = document.getElementById('btn-confirm-link');

        // Search Input Handler
        searchInput.addEventListener('input', (e) => {
            clearTimeout(linkSearchTimeout);
            const query = e.target.value;

            if (query.length < 2) {
                resultsContainer.classList.add('d-none');
                return;
            }

            linkSearchTimeout = setTimeout(() => performLinkSearch(query, currentLinkContext.node.childType, resultsContainer), 300);
        });

        // Confirm Button Handler
        confirmBtn.addEventListener('click', () => {
            if (selectedLinkItem && currentLinkContext) {
                performLink(currentLinkContext, selectedLinkItem);
            }
        });

        // Clear state on modal close
        modalEl.addEventListener('hidden.bs.modal', () => {
            searchInput.value = '';
            resultsContainer.classList.add('d-none');
            confirmBtn.disabled = true;
            selectedLinkItem = null;
            currentLinkContext = null;
        });
    }
});

async function performLinkSearch(query, className, container) {
    try {
        // Use the existing autocomplete API
        const response = await fetch(`/${document.getElementById('tree-container').dataset.baseUrl}/api/autocomplete/${className}?query=${encodeURIComponent(query)}`);
        const results = await response.json();

        container.innerHTML = '';

        if (results.length === 0) {
            container.innerHTML = '<div class="list-group-item text-muted">No results found</div>';
        } else {
            results.forEach(result => {
                const item = document.createElement('a');
                item.href = '#';
                item.className = 'list-group-item list-group-item-action';
                item.innerHTML = `
                    <div class="d-flex w-100 justify-content-between align-items-center">
                        <h6 class="mb-0">${result.value}</h6>
                        <small class="text-muted">#${result.id}</small>
                    </div>
                `;
                item.onclick = (e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    selectLinkItem(result, item, container);
                };
                container.appendChild(item);
            });
        }
        container.classList.remove('d-none');
    } catch (error) {
        console.error("Link search error:", error);
    }
}

function selectLinkItem(item, element, container) {
    console.log("Selecting link item:", item);
    selectedLinkItem = item;

    // Update UI to show selection
    container.querySelectorAll('.active').forEach(el => el.classList.remove('active'));
    element.classList.add('active');

    // Set input value and hide dropdown
    const searchInput = document.getElementById('link-search');
    if (searchInput) {
        searchInput.value = item.value;
    }
    container.classList.add('d-none');

    // Enable confirm button
    const confirmBtn = document.getElementById('btn-confirm-link');
    if (confirmBtn) {
        console.log("Enabling confirm button");
        confirmBtn.disabled = false;
    } else {
        console.error("Confirm button not found");
    }
}

async function performLink(context, item) {
    const { node, nodeElement } = context;
    const baseUrl = document.getElementById('tree-container').dataset.baseUrl;

    // Determine field name (similar to handleAddChild)
    let fieldName;
    const inverseFieldName = nodeElement.dataset.inverseFieldName;

    if (inverseFieldName && inverseFieldName !== '') {
        // For linking, we need the field name on the PARENT side that holds the collection
        // But wait, the API expects the field name on the PARENT side.
        // Let's re-read the API requirement.
        // API: linkNodes(parentClass, parentId, childClass, childId, fieldName)
        // fieldName is "The name of the collection field in the parent entity"

        // The `inverseFieldName` stored in dataset is the field on the CHILD that points to the PARENT.
        // We need the field on the PARENT that points to the CHILD.
        // This is actually `node.childField`!
        fieldName = node.childField;
    } else {
        // Fallback or error?
        // If it's OneToMany, the parent has a collection too.
        fieldName = node.childField;
    }

    try {
        const response = await fetch(`/${baseUrl}/api/tree/link`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                parentClass: node.type,
                parentId: node.id,
                childClass: node.childType,
                childId: item.id,
                field: fieldName
            })
        });

        if (response.ok) {
            // Success!
            linkModal.hide();

            // Refresh the parent node to show the new child
            // We can do this by simulating a click on the toggle button to collapse and expand
            const toggleBtn = nodeElement.querySelector('.tree-toggle');
            if (toggleBtn) {
                // If currently expanded, collapse first
                const childContainer = nodeElement.querySelector('.tree-children');
                if (childContainer && childContainer.style.display !== 'none') {
                    childContainer.remove(); // Remove to force re-fetch
                    // Reset icon
                    toggleBtn.querySelector('i').className = 'bi bi-chevron-right text-muted';
                }

                // Now expand (which will fetch fresh data)
                toggleNode(node, nodeElement, toggleBtn, baseUrl);
            }
        } else {
            const errorText = await response.text();
            alert('Failed to link item: ' + errorText);
        }
    } catch (error) {
        console.error('Link error:', error);
        alert('An error occurred while linking.');
    }
}

function handleLinkExisting(contextMenuNode) {
    console.log("Opening Link Existing modal for:", contextMenuNode);
    currentLinkContext = contextMenuNode;
    if (linkModal) {
        const btn = document.getElementById('btn-confirm-link');
        if (btn) {
            console.log("Disabling confirm button on open");
            btn.disabled = true;
        }
        linkModal.show();
        // Focus search input after modal opens
        setTimeout(() => document.getElementById('link-search').focus(), 500);
    } else {
        console.error("Link modal not initialized");
    }
}
