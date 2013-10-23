<%@page import="fr.cpcgifts.utils.Constants"%>
<%@page import="com.google.appengine.api.memcache.jsr107cache.GCacheFactory"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.jsr107cache.CacheException"%>
<%@page import="java.util.Collections"%>
<%@page import="net.sf.jsr107cache.CacheManager"%>
<%@page import="net.sf.jsr107cache.Cache"%>
<%@page import="java.util.List"%>
<%@page import="fr.cpcgifts.utils.ViewTools"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
%>

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
<title>Canard PC Gifts - FAQ</title>
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

	<%@ include file="menubar.jspf"%>

	<div class="container">
	
		<h3>F.A.Q. Participants</h3>
		<hr />
		<h4>Quelles sont les conditions pour s'inscrire à un concours ?</h4>
		<p>Si rien n'est précisé dans la description du concours ce sont les règles indiquées dans
		<a href="<%= Constants.TOPIC_CANARDS_GENEREUX_FIRST_POST %>">le premier post du topic des canards généreux</a>
		qui s'appliquent.<br />
		Les inscriptions ne sont pas filtrées mais si le gagnant enfreint une de ces règles, un nouveau gagnant sera désigné.
		</p>
		
		<hr />
		<h3>F.A.Q. Créateurs de concours</h3>
		<hr />
		<h4>Que peut on donner sur CPC Gifts ?</h4>
		<p>Tout ce que vous voulez du moment que ça n'enfreint pas les règles de la plateforme sur laquelle vous avez acheté le jeu.<br />
		Les bons de réductions sont interdits à moins d'être très intéressant (80% ou plus).<br />
		Les clés séparées de jeux provenant d'un bundle sont interdites tant que le bundle est encore en vente.
		</p>
		
		<h4>Comment limiter les inscriptions de mon concours à certains canards uniquement ?</h4>
		<p>Il n'existe pour le moment aucun moyen de filtrer les inscriptions aux concours.<br />
		Vous pouvez cependant indiquer vos propres règles dans la description.
		Si le gagnant enfreint ces règles, un nouveau gagnant sera désigné.<br />
		Si rien n'est indiqué dans la description ce sont les règles indiquées dans
		<a href="<%= Constants.TOPIC_CANARDS_GENEREUX_FIRST_POST %>">le premier post du topic des canards généreux</a>
		qui s'appliquent.</p>
		
		<h4>Le gagnant enfreint les règles de mon concours.</h4>
		<p>
		Vous pouvez demander la désignation d'un nouveau gagnant aux admins sur
		<a href="<%= Constants.TOPIC_CANARDS_GENEREUX %>">le topic des canards généreux</a>
		en indiquant dans le post le lien du giveaway et la règle que le gagnant enfreint.
		</p>
		
		<h4>Je n'ai pas de nouvelles du gagnant.</h4>
		<p>Si le concours est terminé depuis plus d'une semaine, vous pouvez demander la désignation d'un
		nouveau gagnant sur
		<a href="<%= Constants.TOPIC_CANARDS_GENEREUX %>">le topic des canards généreux</a>.
		</p>
		
		<hr />
		<h3>Le site CPC Gifts</h3>
		<hr />
		<h4>J'ai trouvé un bug, une faute d'orthographe, etc...</h4>
		<p>Merci de me le signaler dans le <a href="<%= Constants.TOPIC_DEV %>">topic dédié à CPC Gifts</a>.
		</p>

		<hr />
		
		<footer>
		</footer>
	
	</div>
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	

</body>
</html>
