$(document).ready(function() {
  initListeners();  

  // Grab focus on input field.
  $("#barcode_input").focus();
});

function initListeners() {
  $("form").on("submit", function(event) {
    alert("form submitted");
    event.preventDefault();
  });
}
