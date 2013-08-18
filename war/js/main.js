
$(document).ready(function() {
	
	// Support for AJAX loaded modal window.
	// Focuses on first input textbox after it loads the window.
	$('[data-toggle="modal"]').click(function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		if (url.indexOf('#') == 0) {
			$(url).modal('open');
		} else {
			$.get(url, function(data) {
				$('<div class="modal big-modal hide fade">' + data + '</div>').modal();
			}).success(function() { $('input:text:visible:first').focus(); });
		}
	});
	
	//Lazy load sur les images
	$("img.lazy").lazyload();
	
});