$(document).ready(function () {
	initListeners();

	// Grab focus on input field.
	$("#barcode_input").focus();

	loadHistory();
});

function initListeners() {
	// search form submitted
	$("#barcode_form").on("submit", function (event) {
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
		submitItem("Request", this);
	});
	$("#suggest_item_button").on("click", function (event) {
		submitItem("Suggestion", this);
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
			},
			500: function(xhr, status, error) {
				displayError("Server error", xhr.responseText);
			},
		}
	});
}

function barcodeChanged() {
	// force uppercase
	$('#barcode_input').val($('#barcode_input').val().toUpperCase());
	let barcode = $('#barcode_input').val();
	
	// enable/disable search button
	barcode = barcode.toUpperCase();
	let enable_search_button = (barcode != null && barcode.length > 0);
	$("#search_button").prop("disabled", !enable_search_button);
}

function displaySearchResult(data) {
	let dialog = $("#search_result_dialog");
	$(".modal-card-title", dialog).text(data['title']);
	$(".contributor", dialog).text(format(data['contributor']));
	$(".isbn", dialog).text(format(data['isbn']));
	if (data['requesterComments']) {
		$(".note", dialog).text(format(data['requesterComments']));
		$(".note_row").show();
	}
	else {
		$(".note_row").hide();
	}
	$("img", dialog).attr("src", data['coverImage']);
	dialog.addClass("is-active");
	$("#comments_input", dialog).focus();
	console.log("displayed result");
}

function format(field) {
	if (field != null) {
		return field;
	}
	return "<unknown>";
}

function displayError(heading, content) {
	// close any dialogs still open
	closeDialog();

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
	$("#comments_input").val("");

	$("#error_dialog").removeClass("is-active");
	$("button.submit").each(function (index) {
		button = $(this);
		button.prop("disabled", false);
		button.html(button.data("enabled-text"));
	});
}

function submitItem(requestType, submitButton) {
	$(submitButton).html($(submitButton).data("disabled-text"));
	$("button.submit").each(function (index) {
		button = $(this);
		button.prop("disabled", true);
	});


	let comments = $("#comments_input").val();
	let request_data = {
		"requestType": requestType,
		"barcode": $('#barcode_input').val(),
		"comments": (comments.length > 0) ? comments : null
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
		// TODO clean redundancy with search errors above
		statusCode: {
			404: function(xhr, status, error) {
				displayError("Item not found", xhr.responseJSON.message);
			},
			409: function(xhr, status, error) {
				displayError("Item incomplete", xhr.responseJSON.message);
			},
			400: function(xhr, status, error) {
				displayError("Request failed due to something about the item", xhr.responseText);
			},
			500: function(xhr, status, error) {
				displayError("Server error", xhr.responseText);
			}
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
