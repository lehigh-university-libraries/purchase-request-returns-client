$(document).ready(function () {
	initListeners();

	// Grab focus on input field.
	$("#barcode_input").focus();
});

function initListeners() {
	$("form").on("submit", function (event) {
		searchBarcode();
		event.preventDefault();
	});

	$("#barcode_input").on("input", function(event) {
		barcodeChanged();
	});
}

function searchBarcode() {
	let barcode = $('#barcode_input').val();
	let request_data = {
		"barcode": barcode,
	};
	$.ajax({
		url: document.location.origin + "/search",
		contentType: 'application/json',
		data: request_data,
		success: function (data, status, xhr) {
			console.log("call succeeded");
			displaySearchResult(data);
		},
		error: function (xhr, status, error) {
			console.log("failed with status " + status + " and error: " + error);
		}
	});
}

function barcodeChanged() {
	let barcode = $('#barcode_input').val();
	let enable_search_button = (barcode != null && barcode.length > 0);
	$("#search_button").prop("disabled", !enable_search_button);
}

function displaySearchResult(data) {
	alert("Got result: " + JSON.stringify(data));
}
