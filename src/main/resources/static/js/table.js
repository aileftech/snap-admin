function updateBulkActions(table, selected) {
	let divs = document.querySelectorAll(".bulk-actions");
	divs.forEach(div => {
		div.innerHTML = `${selected} items selected <input type="submit" form="delete-form" class="ui-btn btn btn-secondary" value="Delete">`;
	});
}

document.addEventListener("DOMContentLoaded", () => {
	let selected = 0;

	if (document.getElementById('delete-form') != null) {
		document.getElementById('delete-form').addEventListener('submit', function(e) {
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