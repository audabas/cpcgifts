
$(document).ready(function() {
	
	// Support for AJAX loaded modal window.
	// Focuses on first input textbox after it loads the window.
	$('[data-toggle="modal"]').click(function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		if (url.indexOf('#') === 0) {
			$(url).modal('open');
		} else {
			$.get(url, function(data) {
				$('<div class="modal big-modal hide fade">' + data + '</div>').modal();
			}).success(function() { $('input:text:visible:first').focus(); });
		}
	});
	
	//Lazy load sur les images
	$("img.lazy").lazyload();
	
	$("ul").click(function(e) {
		setTimeout(function(){ // attente de l'affichage de l'onglet
			$("img.lazy").lazyload();
		}, 500);
	});
	
});

function createCookie(name,value,days) {
	var expires;
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		expires = "; expires="+date.toGMTString();
	}
	else { expires = ""; }
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function mel(dest,at) {
    window.location = "mailto:" + dest + "@" + at;
}
function melgmail(dest) {
    window.location = "mailto:" + dest + "@" + "gmail.com";
}

function getSmilies() {
	var smilies = {}; //Le tableau qui va contenir les smileys.

	$.ajaxSetup({ mimeType: "text/plain" }); // obligatoire pour les requêtes ajax en local
	if(typeof(Storage) !== "undefined") { //si le navigateur supporte le web storage
		var smilies_json = localStorage.getItem("smilies");
		if(smilies_json === null) {
			$.ajax({
				dataType: "json",
				url: "/img/smilies/smilies.json",
				success: function( data ) {
					smilies = data;
					smilies_json = JSON.stringify(smilies);
					localStorage.setItem("smilies", smilies_json);
				},
				async: false
			});
		} else {
			smilies = JSON.parse(smilies_json);
		}

	} else { //si le navigateur ne supporte pas web storage
		$.ajax({
			dataType: "json",
			url: "/img/smilies/smilies.json",
			success: function( data ) {
				smilies = data;
			},
			async: false
		});
	}

	return smilies;
}