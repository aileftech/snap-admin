document.addEventListener("DOMContentLoaded", () => {
	let rootElements = document.querySelectorAll('.filterable-field');
	
	rootElements.forEach(root => {
		root.querySelector(".card-header").addEventListener('click', function(e) {
			if (root.querySelector(".card-body").classList.contains('d-none')) {
				root.querySelector(".card-body").classList.remove('d-none');
				root.querySelector(".card-body").classList.add('d-block');
				root.querySelector(".card-header i.bi").classList.remove('bi-caret-right');
				root.querySelector(".card-header i.bi").classList.add('bi-caret-down');
			} else {
				root.querySelector(".card-body").classList.remove('d-block');
				root.querySelector(".card-body").classList.add('d-none');
				root.querySelector(".card-header i.bi").classList.remove('bi-caret-down');
				root.querySelector(".card-header i.bi").classList.add('bi-caret-right');
			}
		});
	});
});