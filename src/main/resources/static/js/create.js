document.addEventListener("DOMContentLoaded", () => {
	let checkboxes = document.querySelectorAll(".binary-field-checkbox");
	
	checkboxes.forEach(checkbox => {
		let fieldName = checkbox.dataset.fieldname;
		
		if (!checkbox.checked) {
			document.querySelector(`input[name="${fieldName}"]`).classList.add('d-block');
			document.querySelector(`input[name="${fieldName}"]`).classList.remove('d-none');
			document.querySelector(`input[name="${fieldName}"]`).value = '';				 
		} else {
			document.querySelector(`input[name="${fieldName}"]`).classList.add('d-none');
			document.querySelector(`input[name="${fieldName}"]`).classList.remove('d-block');
		}
		
		checkbox.addEventListener('change', function(e) {
			if (!e.target.checked) {
				document.querySelector(`input[name="${fieldName}"]`).classList.add('d-block');
				document.querySelector(`input[name="${fieldName}"]`).classList.remove('d-none');
				document.querySelector(`input[name="${fieldName}"]`).value = '';				 
			} else {
				document.querySelector(`input[name="${fieldName}"]`).classList.add('d-none');
				document.querySelector(`input[name="${fieldName}"]`).classList.remove('d-block');
			}

		});
	});
});