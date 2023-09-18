/* Request to the autocomplete REST endpoit */
async function getSuggestions(className, query) {
	const response = await fetch(`/dbadmin/api/autocomplete/${className}?query=${query}`);
	const suggestions = await response.json();
	return suggestions;
}

function hideSuggestions(inputElement) {
	let suggestionsDiv = inputElement.parentElement.querySelector("div.suggestions");
	suggestionsDiv.classList.remove('d-block');
	suggestionsDiv.classList.add('d-none');
}

function showSuggestions(inputElement) {
	let suggestionsDiv = inputElement.parentElement.querySelector("div.suggestions");
	suggestionsDiv.classList.remove('d-none');
	suggestionsDiv.classList.add('d-block');
}

document.addEventListener("DOMContentLoaded", () => {
	/* Instead of using onBlur, which takes precedence over onClick
	/* and causes the click event to disappear, we detect click
	/* on outside elements and close all the autocomplete manually */
	document.querySelector("body").addEventListener('click', function(e) {
		if (!e.target.classList.contains("suggestion") && !e.target.classList.contains("autocomplete")) {
			rootElements.forEach(root => {
				hideSuggestions(root.querySelector("input.autocomplete"));
			});
		}
	});
	
	
	let rootElements = document.querySelectorAll(".autocomplete-input");

	rootElements.forEach(root => {
		let input = root.querySelector("input.autocomplete");
		if (input == undefined) return;
	
		input.addEventListener('focus', function() {
			showSuggestions(input);
		});
		
		input.parentElement.querySelector("div.suggestions").innerHTML = 
			`<div class="suggestion p-2 m-0">Enter a valid ID or start typing for suggestions</div>`;
		
		input.addEventListener('keyup', async function(e) {
			let suggestions = await getSuggestions(e.target.dataset.classname, e.target.value);
			input.parentElement.querySelector("div.suggestions").innerHTML = "";
			
			if (e.target.value.length <= 0) {
				input.parentElement.querySelector("div.suggestions").innerHTML = 
					`<div class="suggestion p-2 m-0">Enter a valid ID or start typing for suggestions</div>`;
				return;
			}
			
			suggestions.forEach(suggestion => {
				let suggestionDiv = document.createElement('div');
				suggestionDiv.innerHTML = 
					`<div class="suggestion p-2 m-0">
						<strong>${suggestion.id}</strong>
						<p class="p-0 m-0">${suggestion.value}</p>
					</div>`;
					 
				input.parentElement.querySelector("div.suggestions").appendChild(suggestionDiv);
				
				suggestionDiv.addEventListener('click', function(e) {
					input.value = suggestion.id;
					hideSuggestions(input);
				});
			});
			
			if (suggestions.length == 0) {
				let suggestionDiv = document.createElement('div');
				suggestionDiv.innerHTML = 
					`<div class="suggestion p-2 m-0">
						<p class="p-0 m-0">No results</p>
					</div>`;
					 
				input.parentElement.querySelector("div.suggestions").appendChild(suggestionDiv);
			}
		});
	});
});