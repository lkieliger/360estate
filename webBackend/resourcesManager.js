var query = new Parse.Query(ClientRequest);
query.find({
	success : function(results) {

		// Find a <table> element with id="myTable":
		var table = document.getElementById("resources-table");

		for (var i = 0; i < results.length; i++) {
			var object = results[i];
			
			//Self invoking function to create a new scope for object
			(function(obj, rowIndex){

			var row = table.insertRow(rowIndex);
			var nameCell = row.insertCell(0);

			nameCell.innerHTML = obj.id;
			
			})(object, table.rows.length);
		}
	},
	error : function(error) {
		alert("Error: " + error.code + " " + error.message);
	}
});
