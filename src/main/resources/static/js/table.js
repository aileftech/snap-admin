function updateBulkActions(table, selected) {
	let deleteEnabled = table.dataset.deleteenabled;
	
	let divs = document.querySelectorAll(".bulk-actions");
	divs.forEach(div => {
		if (deleteEnabled === "true") {
			div.innerHTML = `${selected} items selected 
				<input type="submit" form="multi-delete-form" 
					class="ui-btn btn btn-secondary ${deleteEnabled === "false" ? 'disable' : ''} " value="Delete">`;
		} else {
			
			div.innerHTML = `<p class=" badge bg-secondary text-white m-0 mt-2">DELETE not allowed on this table</p>`;
		}
	});
}

document.addEventListener("DOMContentLoaded", () => {
	let selected = 0;

	document.querySelectorAll(".delete-form").forEach(form => {
		form.addEventListener('submit', function(e) {
			if (!confirm('Are you sure you want to delete this item?')) {
				e.preventDefault();
			}
		});
	});

	if (document.getElementById('multi-delete-form') != null) {
		document.getElementById('multi-delete-form').addEventListener('submit', function(e) {
			if (selected == 0) {
				e.preventDefault();
				alert('No items selected');
				return;
			}
			
			if (!confirm('Are you sure you want to delete these items?')) {
				e.preventDefault();
			}
		});
	}
	
	document.querySelectorAll("div.table-selectable").forEach(table => {
		let tableInputs = table.querySelectorAll("table input[type=\"checkbox\"]");
		
		tableInputs.forEach(input => {
			if (input.checked && !input.classList.contains('check-all')) selected++;
			
			input.addEventListener('change', function(e) {
				if (e.target.classList.contains('check-all')) {
					if (e.target.checked) {
						selected = tableInputs.length - 1;
						tableInputs.forEach(input => {
							input.checked = true;
						});
					} else {
						selected = 0;
						tableInputs.forEach(input => {
							input.checked = false;
						});
					}	
				} else {
					if (e.target.checked) {
						selected++;
					} else {
						selected--;
					}
				}
				
				updateBulkActions(table, selected);
			});
		});
		
		updateBulkActions(table, selected);
	});
	
	if (document.querySelector("div.table-selectable select.page-size") != null) {
		document.querySelector("div.table-selectable select.page-size").addEventListener('change', function(e) {
			this.parentElement.querySelector("input[name=\"pageSize\"]").value = e.target.value;
			this.parentElement.submit();
		});
	}
	
});