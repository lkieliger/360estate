Parse.initialize("360ESTATE");
Parse.serverURL = 'https://360.astutus.org/parse/'

var selectionSwitch = true;
	
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

                //Add object id info to row for allowing multiple row deletion
                row.dataset.parseid = obj.id;

			var nameCell = row.insertCell(0);
			var lastNameCell = row.insertCell(1);
			var phoneCell = row.insertCell(2);
                var propertyDescriptionCell = row.insertCell(3);
                var actionCell = row.insertCell(4);
                actionCell.className += " actionCell";

			nameCell.innerHTML = obj.get('name');
			lastNameCell.innerHTML = obj.get('lastName');
			phoneCell.innerHTML = obj.get('phone');
                propertyDescriptionCell.innerHTML = obj.get('propertyDescription');

                var checkBox = document.createElement("input");
                checkBox.type = "checkbox";
                actionCell.appendChild(checkBox);
			})(object, table.rows.length);
		}
	},
	error : function(error) {
		alert("Error: " + error.code + " " + error.message);
	}
});



function generateData(){
	var ClientRequest = Parse.Object.extend("ClientRequest");
	for (var j = 0; j < 10; j++) {
		(function(id){
		var newData = new ClientRequest();
		newData.set("name", "Senior");
		newData.set("lastName", "Sanchez");
		newData.set("phone", "08001234567");
            newData.set("propertyDescription", "Some house " + id);
		
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
    alert('Press enter to refresh page');
	location = location;
}

function selectAll() {
    (function ($) {

        $('#results-table input[type=checkbox]').each(function () {
            $(this).prop("checked", selectionSwitch);
        });

        selectionSwitch = !selectionSwitch;
    })(jQuery);
}

function deleteSelection() {
    if (window.confirm("Are you sure you want to delete all the selected entries ?")) {

        (function ($) {

            $('#results-table input[type=checkbox]:checked').each(function () {
                var rowObjectId = $(this).closest('tr').data('parseid');
                var ClientRequest = Parse.Object.extend("ClientRequest");
                var toDelete = new ClientRequest();
                toDelete.id = rowObjectId;

                toDelete.destroy({
                    success: function () {
                    },
                    error: function (error) {
                        alert('Failed to delete a checked item, error code: ' + error.message);
                    }
                });

            });
        })(jQuery);

        alert('Sucessfully deleted selected entries');
        location = location;
    }
}

function logoutUser(){
	Parse.User.logOut();
    var currentUser = Parse.User.current();
    gotoLogin();
}


//CREATE CONTROL PANEL
var controlPanel = document.getElementById("controlPanel");

var generateDataButton = document.createElement("button");
generateDataButton.innerHTML = "<i class=\"fa fa-cog\" aria-hidden=\"true\"></i> Generate";
generateDataButton.onclick = generateData;

var logoutButton = document.createElement("button");
logoutButton.innerHTML = "<i class=\"fa fa-sign-out\" aria-hidden=\"true\"></i> Logout";
logoutButton.onclick = logoutUser;

//For swap
// <i class="fa fa-random" aria-hidden="true"></i>

var deleteSelectedButton = document.createElement("button");
deleteSelectedButton.innerHTML = "<i class=\"fa fa-trash-o\" aria-hidden=\"true\"></i> Delete selection";
deleteSelectedButton.onclick = deleteSelection;

controlPanel.appendChild(generateDataButton);
controlPanel.appendChild(deleteSelectedButton);
controlPanel.appendChild(logoutButton);


//CREATE COLUMN BUTTONS
var selTableHeader = document.getElementById("selectionTableHeader");

var selectAllButton = document.createElement("button");
selectAllButton.innerHTML = "<i class=\"fa fa-check-square-o\" aria-hidden=\"true\"></i>";
selectAllButton.onclick = selectAll;
selectAllButton.className += " tableHeaderControl";

selTableHeader.appendChild(selectAllButton);


//Why are you reading this ?

