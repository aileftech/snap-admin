// Inline Create Modal functionality
let currentFieldName = null;
let currentEntityType = null;

// Attach event listeners to all inline create buttons
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.inline-create-btn').forEach(button => {
        button.addEventListener('click', function () {
            const entityClass = this.getAttribute('data-entity-class');
            const fieldName = this.getAttribute('data-field-name');
            openCreateModal(entityClass, fieldName);
        });
    });
});

function openCreateModal(entityClassName, fieldName) {
    currentFieldName = fieldName;
    currentEntityType = entityClassName;

    // Get the simple class name
    const simpleClassName = entityClassName.split('.').pop();

    // Build the URL to the create page
    // Use the global baseUrl variable
    const createUrl = `/${baseUrl}/model/${entityClassName}/create`;

    // Create modal if it doesn't exist
    let modal = document.getElementById('inlineCreateModal');
    if (!modal) {
        modal = createModalElement();
        document.body.appendChild(modal);
    }

    // Update modal title
    document.getElementById('inlineCreateModalLabel').textContent = `Create New ${simpleClassName}`;

    // Load the create form into the modal
    const modalBody = document.getElementById('inlineCreateModalBody');
    modalBody.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>';

    // Fetch the create page
    fetch(createUrl)
        .then(response => response.text())
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const form = doc.querySelector('form');

            if (form) {
                // Remove the original submit button to prevent double submission
                const submitBtn = form.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.remove();
                }

                // Update form to prevent default submission
                form.setAttribute('onsubmit', 'return false;');

                modalBody.innerHTML = '';
                modalBody.appendChild(form);
            } else {
                modalBody.innerHTML = '<div class="alert alert-danger">Failed to load form</div>';
            }
        })
        .catch(error => {
            console.error('Error loading create form:', error);
            modalBody.innerHTML = '<div class="alert alert-danger">Error loading form</div>';
        });

    // Show the modal
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
}

function createModalElement() {
    const modalHtml = `
        <div class="modal fade" id="inlineCreateModal" tabindex="-1" aria-labelledby="inlineCreateModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="inlineCreateModalLabel">Create New</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="inlineCreateModalBody">
                        <!-- Form will be loaded here -->
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" onclick="submitInlineCreate()">Create & Select</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    const div = document.createElement('div');
    div.innerHTML = modalHtml;
    return div.firstElementChild;
}

function submitInlineCreate() {
    const form = document.querySelector('#inlineCreateModalBody form');
    if (!form) {
        alert('Form not found');
        return;
    }

    const formData = new FormData(form);

    fetch(form.action, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (response.redirected) {
                // Success! The response URL should be .../show/{id}
                const newUrl = response.url;
                const id = newUrl.split('/').pop();

                // Fetch the new page to get the display name
                return fetch(newUrl)
                    .then(res => res.text())
                    .then(html => {
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(html, 'text/html');
                        // Try to find the display name
                        // Based on show.html: <h3 class="mb-3 fw-bold" th:text="${object.getDisplayName()}"></h3>
                        let displayName = id; // Default to ID
                        const h3 = doc.querySelector('h3.mb-3.fw-bold');
                        if (h3) {
                            displayName = h3.textContent.trim();
                        } else {
                            // Fallback: try to find it in the breadcrumb
                            const h1 = doc.querySelector('h1.fw-bold');
                            if (h1) {
                                const spans = h1.querySelectorAll('span');
                                if (spans.length > 0) {
                                    displayName = spans[spans.length - 1].textContent.trim();
                                }
                            }
                        }

                        return { id, displayName };
                    });
            } else {
                // Error or same page
                return response.text().then(html => {
                    throw new Error(html);
                });
            }
        })
        .then(result => {
            if (result && result.id) {
                // Close modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('inlineCreateModal'));
                modal.hide();

                // Update UI
                updateField(currentFieldName, result.id, result.displayName);
            }
        })
        .catch(error => {
            console.error('Error creating entity:', error);
            // If it's HTML (error page), display it in the modal
            if (error.message && error.message.includes('<html')) {
                document.getElementById('inlineCreateModalBody').innerHTML = error.message;
            } else {
                alert('Error creating entity. Please check the form.');
            }
        });
}

function updateField(fieldName, id, displayName) {
    // Try to find single select input
    let input = document.querySelector(`input[name="${fieldName}"]`);
    if (input && input.classList.contains('autocomplete')) {
        input.value = id;
        // Optionally flash the input to show it changed
        input.classList.add('bg-success', 'text-white');
        setTimeout(() => input.classList.remove('bg-success', 'text-white'), 1000);
        return;
    }

    // Try to find multi select input
    // The input name for multi select is fieldName[]
    // But we need to find the container or the hidden inputs
    // The autocomplete-multi input has data-fieldname="fieldName[]"
    input = document.querySelector(`input[data-fieldname="${fieldName}[]"]`);
    if (input) {
        const root = input.closest('.autocomplete-multi-input');
        if (root) {
            const selectedValues = root.querySelector('.selected-values');
            const clearAllBadge = root.querySelector('.clear-all-badge');

            // Show clear all badge
            clearAllBadge.classList.remove('d-none');
            clearAllBadge.classList.add('d-inline-block');

            // Add new badge
            const badgeHtml = `
                <span class="value-badge">
                    <input type="checkbox" class="badge-checkbox" checked="checked" 
                           name="${fieldName}[]" value="${id}">
                    <span class="badge bg-primary me-2">
                        ${displayName}
                    </span>
                </span>`;

            // Append and add event listener
            // We need to create a temporary element to parse the HTML
            const temp = document.createElement('div');
            temp.innerHTML = badgeHtml;
            const newBadge = temp.firstElementChild;

            newBadge.addEventListener('click', function () {
                newBadge.remove();
            });

            selectedValues.appendChild(newBadge);
        }
    }
}
