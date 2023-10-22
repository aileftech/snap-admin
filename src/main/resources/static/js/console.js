document.addEventListener("DOMContentLoaded", () => {
	document.querySelector("#console-delete-btn").addEventListener("click", () => {
		if (confirm("Are you sure you want to delete this query?")) {
			document.querySelector("#console-delete-form").submit();
		}
	});

});