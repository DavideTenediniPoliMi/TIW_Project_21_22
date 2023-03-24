/**
 * Utils
 */
 
function makeCall(method, url, form, callback, reset = true) {
	var request = new XMLHttpRequest();
	
	request.onreadystatechange = function() {
		callback(request);
	};
	
	request.open(method, url);
	
	if (form == null) {
		request.send();
	} else {
		request.send(new FormData(form));
		
		if (reset) {
			form.reset();
		}
	}
}

function redirect(path) {
	window.location.href = path;
}

function disable(element) {
	element.classList.add("disabled");
}

function enable(element) {
	element.classList.remove("disabled");
}