document.addEventListener("DOMContentLoaded", () => {
	let rootElements = document.querySelectorAll('.filterable-fields');
	
	
	rootElements.forEach(root => {
		let fields = root.querySelectorAll('.filterable-field');

		let activeFilters = root.querySelectorAll(".active-filter");
		activeFilters.forEach(activeFilter => {
			activeFilter.addEventListener('click', function(e) {
				let formId = e.target.dataset.formid;
				document.getElementById(formId).submit()
			});
		});
	
		fields.forEach(field => {
			field.querySelector(".card-header").addEventListener('click', function(e) {
				if (field.querySelector(".card-body").classList.contains('d-none')) {
					field.querySelector(".card-body").classList.remove('d-none');
					field.querySelector(".card-body").classList.add('d-block');
					field.querySelector(".card-header i.bi").classList.remove('bi-caret-right');
					field.querySelector(".card-header i.bi").classList.add('bi-caret-down');
				} else {
					field.querySelector(".card-body").classList.remove('d-block');
					field.querySelector(".card-body").classList.add('d-none');
					field.querySelector(".card-header i.bi").classList.remove('bi-caret-down');
					field.querySelector(".card-header i.bi").classList.add('bi-caret-right');
				}
			});
		});	
	});
	
	
});