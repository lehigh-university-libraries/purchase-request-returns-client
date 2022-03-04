$(document).ready(function () {
	initListeners();

	// Grab focus on input field.
	$("#barcode_input").focus();
});

function initListeners() {
	// search form submitted
	$("form").on("submit", function (event) {
		searchBarcode();
		event.preventDefault();
	});

	// barcode text input changed
	$("#barcode_input").on("input", function(event) {
		barcodeChanged();
	});

	initDialogListeners();
}

function initDialogListeners() {
	$("#request_item_button").on("click", function (event) {
		requestItem();
	});

	// various methods of closing the search dialog
	$(".modal-background, .modal-card-head .delete").each(function(index, element) {
		$(element).on("click", function(event) { 
			closeSearchResults();
		})
	})
	$(document).on("keydown", function(event) {
		if (event.keyCode === 27) { // Escape key
			closeSearchResults();
		}
	});
}

function searchBarcode() {
	let request_data = {
		"barcode": $('#barcode_input').val(),
	};
	$.ajax({
		url: document.location.origin + "/search",
		contentType: 'application/json',
		data: request_data,
		success: function (data, status, xhr) {
			console.log("Search call succeeded");
			displaySearchResult(data);
		},
		error: function (xhr, status, error) {
			console.log("Search failed with status " + status + " and error: " + error);
		}
	});
}

function barcodeChanged() {
	let barcode = $('#barcode_input').val();
	let enable_search_button = (barcode != null && barcode.length > 0);
	$("#search_button").prop("disabled", !enable_search_button);
}

function displaySearchResult(data) {
	let dialog = $("#search_result_dialog");
	$(".modal-card-title", dialog).text(data['title']);
	$(".contributor", dialog).text(data['contributor']);
	$(".isbn", dialog).text(data['isbn']);
	$("img", dialog).attr("src", data['coverImage']);
	dialog.addClass("is-active");
	$("#request_item_button", dialog).focus();
	console.log("displayed result");
}

function closeSearchResults() {
	$('#barcode_input').val("");
	$("#barcode_input").focus();

	$("#search_result_dialog").removeClass("is-active");
	let button = $("#request_item_button");
	button.prop("disabled", false);
	button.html(button.data("enabled-text"));
}

function requestItem() {
	let button = $("#request_item_button");
	button.prop("disabled", true);
	button.html(button.data("disabled-text"));

	let request_data = {
		"barcode": $('#barcode_input').val(),
	};
	$.ajax({
		url: document.location.origin + "/request",
		method: "POST",
		contentType: 'application/json',
		data: JSON.stringify(request_data),
		success: function (data, status, xhr) {
			console.log("Request call succeeded");
			closeSearchResults();
		},
		error: function (xhr, status, error) {
			console.log("Request call failed with status " + status + " and error: " + error);
		}
	});

}