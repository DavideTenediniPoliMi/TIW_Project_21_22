/**
 * Login Manager
 */
 
(function() {
	if (sessionStorage.getItem("username") != null) {
		redirect("home.html");
		return;
	}

	var button_login = document.getElementById("button_login");
	var button_reg = document.getElementById("button_reg");
	var error = document.getElementById("error");

	button_login.addEventListener("click", (event) => {
		event.preventDefault();
		
		var form = event.target.closest("form");

		if (!form.checkValidity()) {
			form.reportValidity();
			return;
		}

		sendForm("CheckLogin", form);
	});

	button_reg.addEventListener("click", (event) => {
		event.preventDefault();
		
		var form = event.target.closest("form");
		var password = form.querySelector("input[name='password']");
		var passwordDuplicate = form.querySelector("input[name='passwordDuplicate']");

		if (!form.checkValidity()) {
			form.reportValidity();
			return;
		}
		
		if (password.value != passwordDuplicate.value) {
			showError("Passoword fields don't match!");
			return;
		}

		sendForm("RegisterUser", form);
	});

	function sendForm(url, form) {
		makeCall("POST", url, form, function(request) {
			if (request.readyState !== XMLHttpRequest.DONE) return;

			var message = request.responseText;

			if (request.status != 200) {
				showError(message);
				return;
			}
			
			var usernameJson = JSON.parse(message);
			
			if (!usernameJson.hasOwnProperty("username")) {
				showError("Something went wrong, try again!");
				return;
			}

			sessionStorage.setItem("username", usernameJson.username);
			redirect("home.html");
		});
	}

	function showError(errorText) {
		error.textContent = errorText;
	}
})();