document.addEventListener("DOMContentLoaded", () => {
	let form = document.getElementById('log-filter-form');
	
	if (form == null) return;
	
	let selects = form.querySelectorAll('select');
	selects.forEach(select => {
		select.addEventListener('change', function(e) {
			form.submit();
		});
	});
});