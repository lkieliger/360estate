Parse.initialize("360ESTATE");
Parse.serverURL = 'https://360.astutus.org/parse/'
	
function gotoLogin(){
	window.location.href = "index.html";
}
	
var currentUser = Parse.User.current();
if (currentUser) {
} else {
  alert('User is not logged');
  gotoLogin();
}

var ClientRequest = Parse.Object.extend("ClientRequest");
var query = new Parse.Query(ClientRequest);
query.find({
	success : function(results) {

		var table = document.getElementById("results-table");

		for (var i = 0; i < results.length; i++) {
			var object = results[i];
			
			//Self invoking function to create a new scope for object
			(function(obj, rowIndex){

			var row = table.insertRow(rowIndex);

			var nameCell = row.insertCell(0);
			var lastNameCell = row.insertCell(1);
			var phoneCell = row.insertCell(2);
			var houseIdCell = row.insertCell(3);
			var buttonCell = row.insertCell(4);

			nameCell.innerHTML = obj.get('name');
			lastNameCell.innerHTML = obj.get('lastName');
			phoneCell.innerHTML = obj.get('phone');
			houseIdCell.innerHTML = obj.get('houseId');

			
			var deleteParseObject = function(){
				obj.destroy({
					  success: function(myObject) {
						  location = location;
					  },
					  error: function(myObject, error) {
						  alert('Couldn\'t delete the Parse object');
					  }
					});
			}
			
			var button = document.createElement("button");
			button.innerHTML = "Delete";
			button.onclick = deleteParseObject;
			
			buttonCell.appendChild(button);
			})(object, table.rows.length);
		}
	},
	error : function(error) {
		alert("Error: " + error.code + " " + error.message);
	}
});



function generateData(){
	var ClientRequest = Parse.Object.extend("ClientRequest");
	alert('call to generate data');
	for (var j = 0; j < 10; j++) {
		(function(id){
			alert('creating new data with id '+ id);
		var newData = new ClientRequest();
		newData.set("name", "Senior");
		newData.set("lastName", "Sanchez");
		newData.set("phone", "08001234567");
		newData.set("houseId", id);
		
		newData.save(null, {
			  		success: function(object) {
			  			//alert('success');
			  		},
			  		error: function(object, error) {
			  			alert('Failed to create new object, with error code: ' + error.message);
			  		}
				});
		})(j);
	}
	alert('will now refresh page');
	location = location;
}

function logoutUser(){
	Parse.User.logOut().then(() => {
		  var currentUser = Parse.User.current();  // this will now be null
		  gotoLogin();
		});
}

var controlPanel = document.getElementById("controlPanel");

var generateDataButton = document.createElement("button");
generateDataButton.innerHTML = "Generate one entry";
generateDataButton.onclick = generateData;

var logoutButton = document.createElement("button");
logoutButton.innerHTML = "Logout";
logoutButton.onclick = logoutUser;

controlPanel.appendChild(generateDataButton);
controlPanel.appendChild(logoutButton);

//Why are you reading this ?

