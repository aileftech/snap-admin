/* Request to the autocomplete REST endpoit */
async function getSuggestions(className, query) {
	const response = await fetch(`/${baseUrl}/api/autocomplete/${className}?query=${query}`);
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
	let rootElements = document.querySelectorAll(".autocomplete-multi-input");
	
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
	
	
	rootElements.forEach(root => {
		/* Event listener to delete badge on click
		 */
		root.querySelectorAll(".selected-values .value-badge").forEach(badge => {
			badge.addEventListener('click', function() {
				badge.remove();
			});
		});
		
		root.querySelector(".clear-all-badge").addEventListener('click', function(e) {
			e.target.classList.add('d-none');
			e.target.classList.remove('d-inline-block');
			root.querySelectorAll(".selected-values .value-badge").forEach(badge => {
				badge.remove();
			});	
		});
		
		let input = root.querySelector("input.autocomplete");
		if (input == undefined) return;
		
		input.addEventListener('focus', function() {
			showSuggestions(input);
		});
		
		let fieldName = input.dataset.fieldname;
		
		input.parentElement.querySelector("div.suggestions").innerHTML = 
			`<div class="suggestion p-2 m-0">Start typing for suggestions</div>`;
		
		input.addEventListener('keyup', async function(e) {
			let suggestions = await getSuggestions(e.target.dataset.classname, e.target.value);
			input.parentElement.querySelector("div.suggestions").innerHTML = "";
			
			if (e.target.value.length <= 1) {
				input.parentElement.querySelector("div.suggestions").innerHTML = 
					`<div class="suggestion p-2 m-0">Start typing for suggestions</div>`;
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
					hideSuggestions(input);
					input.value = '';
					
					// Check if we need to add the 'Clear all' button back
					root.querySelector(".clear-all-badge").classList.add('d-inline-block');
					root.querySelector(".clear-all-badge").classList.remove('d-none');
					
					
					root.querySelector(".selected-values")
						.innerHTML += `
							<span class="value-badge">
								<input type="checkbox" class="badge-checkbox" checked="checked" 
									   name="${fieldName}" value="${suggestion.id}">
								<span class="badge bg-primary me-2">
									${suggestion.value}
								</span>
							</span>`
					
					root.querySelectorAll(".selected-values .value-badge").forEach(badge => {
						badge.addEventListener('click', function() {
							badge.remove();
						});
					});		
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
