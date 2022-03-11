$(document).ready(function () {
	initListeners();

	// Grab focus on input field.
	$("#barcode_input").focus();

	loadHistory();
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
	$(".modal-background, .modal-card-head .delete, .cancel_button").each(function(index, element) {
		$(element).on("click", function(event) { 
			closeDialog();
		})
	})
	$(document).on("keydown", function(event) {
		if (event.keyCode === 27) { // Escape key
			closeDialog();
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
		statusCode: {
			404: function(xhr, status, error) {
				displayError("Item not found", xhr.responseJSON.message);
			},
			409: function(xhr, status, error) {
				displayError("Item incomplete", xhr.responseJSON.message);
			},
			400: function(xhr, status, error) {
				displayError("Invalid barcode", xhr.responseText);
			}
		}
		// error: function (xhr, status, error) {
		// 	// console.log("Search failed with status " + xhr.status + " and error: " + error);
		// 	console.log("Search failed with status " + status + " and error: " + error);
		// 	if (xhr.status == 404) {
		// 		displayError("Item not found", xhr.responseJSON.message);
		// 	}
		// 	else if (xhr.status = 409) {
		// 		displayError("Item incomplete", xhr.responseJSON.message);
		// 	}
		// 	else if (xhr.status = 400) {
		// 		displayError("Invalid barcode", xhr.responseText);
		// 	}
		// 	else {
		// 		displayError("Unexpected error", xhr.responseText);
		// 	}
		// }
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

function displayError(heading, content) {
	let dialog = $("#error_dialog");
	$(".modal-card-title", dialog).text(heading);
	$(".error_message", dialog).text(content);
	dialog.addClass("is-active");
	$(".cancel_button", dialog).focus();
	console.log("displayed error");
}

function closeDialog() {
	$('#barcode_input').val("");
	$("#barcode_input").focus();

	$("#search_result_dialog").removeClass("is-active");
	$("#error_dialog").removeClass("is-active");
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
			closeDialog();
			loadHistory();
		},
		error: function (xhr, status, error) {
			console.log("Request call failed with status " + status + " and error: " + error);
		}
	});
}

function loadHistory() {
	$.ajax({
		url: document.location.origin + "/requestHistory",
		contentType: 'application/json',
		success: function (tbody, status, xhr) {
			console.log("History call succeeded", tbody);
			$("#history_table tbody").replaceWith(tbody);
		},
		error: function (xhr, status, error) {
			console.log("History failed with status " + status + " and error: " + error);
		}
	});
}
