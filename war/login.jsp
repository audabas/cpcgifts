<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	CpcUser cpcuser = (CpcUser) session.getAttribute("cpcuser");

	if (cpcuser == null) {
		if (userService.isUserLoggedIn()) {
			cpcuser = CpcUserPersistance.getCpcUser(user.getUserId());
			session.setAttribute("cpcuser", cpcuser);
		}
	}

	if (cpcuser != null) {
		response.sendRedirect("/");
		return;
	}
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
<title>Canard PC Gifts</title>
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

<script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<div class="navbar  navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<div class="pull-right">
					<a class="btn btn-inverse"
						href="<%=userService.createLogoutURL("/")%>"><i
						class="icon-off icon-white"></i> Quitter</a></a>
				</div>
			</div>
		</div>
	</div>

	<div class="container">

		<!-- Main hero unit for a primary marketing message or call to action -->
		<div class="hero-unit">
			<h1>Bienvenue sur Canard PC Gifts !</h1>

		</div>

		<div class="row">
			<p class="offset1">
				Salut à toi ami canard ! <br /> En t'inscrivant sur ce site tu
				acceptes les <a
					href="http://forum.canardpc.com/threads/63940-Le-topic-des-canards-g%C3%A9n%C3%A9reux-R%C3%A8gle-de-bonne-conduite-dans-le-1er-post-%21%21?p=5015733&viewfull=1#post5015733">règles
					présentes sur le premier post du topic des canards généreux</a>. <br />
				Tout manquement à ces règles peut entraîner un ban et une lapidation publique sur le topic (On rigole pas avec la générosité ici, non mais !).
			</p>

			<p class="offset1">
				Afin de s'assurer que tu es bien inscrit sur le forum Canard PC, tu
				dois indiquer ton identifiant CPC.<br /> Tu le trouvera en allant
				sur la page de ton profil, il s'agit en fait de la dernière partie
				de l'url indiquée dans la barre d'adresse (<a href="/aidecpcid.html" data-target="#"
					data-toggle="modal"><i class="icon-question-sign"></i>
					Comment faire</a>).
			</p>
			<br />
		</div>

		<div class="row-fluid">

			<form action="/signin-serv" onsubmit="return validateForm()"
				method="post">
				<div class="input-prepend control-group offset1 span8" id="idCPCCg">
					<span class="add-on">http://forum.canardpc.com/members/</span> <input
						class="span2" name="idCPC" id="idCPC" type="text"
						placeholder="id-pseudo" required="required">
				</div>
				<input class="btn btn-success" type="submit" value="S'inscrire" />
			</form>

		</div>

		<hr>

		<%@ include file="footer.jspf"%>

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

	<script src="js/main.js"></script>

	<script type="text/javascript">
		function validateForm() {

			if ($("#idCPC").val().match(/[0-9]+-.+/g) == null) {
				alert("Identifiant cpc invalide !");
				$("#idCPCCg").addClass("error");
				return false;
			}

		}
		
		var nbCheck = 0; // nombre de fois ou on a vérifié si l'utilisateur était enregistré. 

		var timer = setInterval(function() {
			$.ajax({
				url : "/signin-serv",
				success : function(data) {
					nbCheck++;
					if (data == true) {
						window.location.reload();
					}
					if(nbCheck > 60) {
						clearInterval(timer);
					}
				}
			});
		}, 1000);
	</script>


</body>
</html>
