function showFileInput(inputElement) {
	inputElement.classList.add('d-block');
	inputElement.classList.remove('d-none');
	inputElement.value = '';
	
	let img = document.getElementById(`__thumb_${inputElement.name}`);
	if (img != null) {
		img.classList.add('img-muted');
	}
}

function hideFileInput(inputElement) {
	inputElement.classList.add('d-none');
	inputElement.classList.remove('d-block');
	
	let img = document.getElementById(`__thumb_${inputElement.name}`);
	if (img != null) {
		img.classList.remove('img-muted');
	}
}

document.addEventListener("DOMContentLoaded", () => {
	let checkboxes = document.querySelectorAll(".binary-field-checkbox");
	
	checkboxes.forEach(checkbox => {
		let fieldName = checkbox.dataset.fieldname;
		
		if (!checkbox.checked) {
			showFileInput(document.querySelector(`input[name="${fieldName}"]`));
		} else {
			hideFileInput(document.querySelector(`input[name="${fieldName}"]`));
		}
		
		checkbox.addEventListener('change', function(e) {
			if (!e.target.checked) {
				showFileInput(document.querySelector(`input[name="${fieldName}"]`));				 
			} else {
				hideFileInput(document.querySelector(`input[name="${fieldName}"]`));
			}

		});
	});
});