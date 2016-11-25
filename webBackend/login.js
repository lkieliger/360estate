
Parse.initialize("360ESTATE");
Parse.serverURL = 'https://360.astutus.org/parse/'
	
var loginForm = document.getElementById("loginForm");

var connectButton = document.createElement("button");
connectButton.type = "button";
connectButton.innerHTML = "Connect";
connectButton.onclick = connectUser;

var username = document.createElement("input");
username.autofocus = true;
username.type = "text";
username.name = "username";
username.placeholder = "username";

var password = document.createElement("input");
password.type = "password";
password.name = "password";
password.placeholder = "password";
password.addEventListener("keyup", function(event) {
	event.preventDefault();
	if (event.keyCode == 13) {
		password.blur();
	    connectButton.click();
	}
	});

loginForm.appendChild(username);
loginForm.appendChild(password);
loginForm.appendChild(connectButton);


function connectUser(){
	
	Parse.User.logIn(username.value, password.value, {
	  success: function(user) {
	    alert('login successful')
	    window.location.href="clientRequests.html";
	  },
	  error: function(user, error) {
	    alert('login not successful');
	  }
	});
}
