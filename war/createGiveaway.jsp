<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
%>

<%@ include file="forcelogin.jspf"%>


<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Cr&eacute;er un concours - CPCGifts</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="css/bootstrap.min.css">
<style>
body {
	padding-top: 60px;
	padding-bottom: 40px;
}
</style>
<link rel="stylesheet" href="css/bootstrap-responsive.min.css">
<link rel="stylesheet" href="css/main.css">
<link rel="stylesheet" href="css/smoothness/jquery-ui-1.10.2.custom.min.css">
<script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<%@ include file="menubar.jspf"%>

	<div class="container">

		<form action="/giveaway-serv" onsubmit="return validateForm()" method="post">
			<div class="row control-group" id="gameNameCg">
				<div class="span1">
					<img src="/img/gift.png" />
				</div>
				<div class="span10 offset1">
					<div class="row">
						<h4>Nom du jeu*</h4>
					</div>
					<div class="row">
						<input class="span10" name="gameName" id="gameName" type="text" required="required">
						<span class="help-block well">Pensez à indiquer la plateforme de distribution (Steam, Desura, GOG, etc...)</span>
					</div>
				</div>
			</div>
			<hr>
			<div class="row control-group" id="imgUrlCg">
				<div class="span1">
					<img src="/img/image.png" />
				</div>
				<div class="span10 offset1">
					<div class="row">
						<h4>Url de l'image (460x215 pixels)</h4>
					</div>
					<div class="row">
						<input class="span10" name="imgUrl" id="imgUrl" type="text">
						<span class="help-block well">Les images de tailles différentes à 460x215 sont acceptées mais peuvent mal s'afficher.</span>
						<span class="help-block well">Si le jeu est disponible sur steam, le plus simple est de récupérer le lien vers l'image depuis le hub de la communauté (<a href="/aidesteamimg.html"
					role="button" data-target="#" data-toggle="modal"><i class="icon-question-sign"></i>
					Comment faire</a>).</span>
					<span class="help-block well">Vous pouvez héberger vos photos sur <a href="http://tof.canardpc.com/">tof.canardpc.com</a> (utilisez le <i>Lien direct de l'image</i>).</span>
					</div>
				</div>
			</div>
			<hr>
			<div class="row control-group" id="gameDescriptionCg">
				<div class="span1">
					<img src="/img/desc.png" />
				</div>
				<div class="span10 offset1">
					<div class="row">
						<h4>Description</h4>
					</div>
					<div class="row">
						<textarea class="span10" name="gameDescription" id="gameDescription" rows="3" ></textarea>
						<span class="help-block well">500 caractères maximum. Il vous reste <span id="descLimit">500</span> caractères. </span>
					</div>
				</div>
			</div>
			<hr>
			<div class="row control-group" id="endDateCg">
				<div class="span1">
					<img src="/img/date.png" />
				</div>
				<div class="span10 offset1">
					<div class="row">
						<h4>Date de fin*</h4>
					</div>
					<div class="row">
						<input class="span2" name="endDate" id="endDate" type="text" required="required">
						<input name="endDateParsed" id="endDateParsed" type="text" style="display: none;">
					</div>
				</div>
			</div>
			<div class="row form-actions">
				<div class="span10 offset1">
					<input class="btn btn-success" type="submit"
						value="Créer le concours" />
				</div>
			</div>
		</form>




		<footer>
			<p><%@ include file="footer.jspf" %></p>
		</footer>

	</div>
	<!-- /container -->

	<script
		src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script>
		window.jQuery
				|| document
						.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')
	</script>

	<script src="js/vendor/bootstrap.min.js"></script>
	
	<script src="js/vendor/jquery-ui-1.10.2.custom.min.js"></script>
	<script src="js/vendor/jquery-ui-timepicker-addon.js"></script>

	<script src="js/main.js"></script>
	<script src="js/datepicker.js"></script>
	<script src="js/bootstrap-limit.js"></script>
	
	<script type="text/javascript">
	
		var mindate = new Date();
		
		mindate.setMinutes(mindate.getMinutes() + 60);
		
		$("#gameDescription").limit({
			  maxChars: 500,
			  counter : $("#descLimit")
			});

		$("#gameDescription").on("cross" , function (e) {
			alert("Votre description dépasse la limite de 500 caractères.");
		});
	
		$("#endDate").datetimepicker({
			minDateTime: mindate,
			maxDate: 33,
			dateFormat: "dd/mm/yy",
			timeFormat: "HH:mm",
			altField : "#endDateParsed",
			altFieldTimeOnly: false,
			altFormat: "yy-mm-dd",
			altTimeFormat: "HH-mm@z",
			altSeparator: "@",
			defaultTimezone : mindate.getTimezoneOffset()
		});
	
		function validateForm()
		{
			
			if($("#gameName").val() == "") {
				alert("Vous devez donner un nom au concours.");
				$("#gameNameCg").addClass("error");
				return false;
			} else {
				$("#gameNameCg").removeClass("error");
			}
			if($("#endDateParsed").val() == "") {
				alert("Date de fin invalide !");
				return false;
			}
			
			if($("#gameName").val().length >= 50) {
				alert("Nom du jeu trop long (limite : 50 caractères) !");
				return false;
			}
			if($("#imgUrl").val().length >= 500) {
				alert("Url vers l'image trop longue (limite : 500 caractères) !");
				return false;
			}
			if($("#gameDescription").val().length >= 500) {
				alert("Description du jeu trop longue (limite : 500 caractères) !");
				return false;
			}
			
			
		}
		
	</script>
	

</body>
</html>
