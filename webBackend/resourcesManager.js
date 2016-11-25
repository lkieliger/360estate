Parse.initialize("360ESTATE");
Parse.serverURL = 'https://360.astutus.org/parse/'

var Resources = Parse.Object.extend("Resources");
var query = new Parse.Query(Resources);
query.find({
	success : function(results) {
		alert('sucessfully retrieved resource list');
		// Find a <table> element with id="myTable":
		var table = document.getElementById("resources-table");

		for (var i = 0; i < results.length; i++) {
			var object = results[i];
			
			//Self invoking function to create a new scope for object
			(function(obj, rowIndex){

			var row = table.insertRow(rowIndex);
			var nameCell = row.insertCell(0);
			var actionCell = row.insertCell(1);

			nameCell.innerHTML = obj.id;
			
			var button = document.createElement("button");
			button.innerHTML = "Delete";
			//button.onclick = deleteParseObject;
			
			
			})(object, table.rows.length);
		}
	},
	error : function(error) {
		alert("Error: " + error.code + " " + error.message);
	}
});
