<%@page import="fr.cpcgifts.utils.Constants"%>
<%@page import="com.google.appengine.api.memcache.jsr107cache.GCacheFactory"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.jsr107cache.CacheException"%>
<%@page import="java.util.Collections"%>
<%@page import="net.sf.jsr107cache.CacheManager"%>
<%@page import="net.sf.jsr107cache.Cache"%>
<%@page import="java.util.List"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%	UserService userService = UserServiceFactory.getUserService(); %>
<%	User user = userService.getCurrentUser(); %>

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

<%@ include file="css.jspf" %>

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
		<p>Vous devez vérifier que vous respectez les conditions indiquées sur la page du concours avant de vous inscrire.<br /> 
		Les inscriptions ne sont pas filtrées mais si le gagnant enfreint une de ces règles, un nouveau gagnant sera désigné.
		</p>
		
		<h4>J'ai gagné un concours, mais le donateur refuse de me donner mon gain.</h4>
		<p>
			Si le donateur ne peut pas vous donner votre gain alors que vous remplissez toutes les conditions d'inscriptions,
			merci de le signaler à un admin afin que nous puissions mettre à jour la page du giveaway et, si nécessaire,
			prendre des mesures à l'encontre du canard en question.<br />
			Merci de rester poli et courtois, un oubli/une erreur peut arriver à tout le monde ;-)
		</p>
		
		<hr />
		<h3>F.A.Q. Créateurs de concours</h3>
		<hr />
		<h4>Que peut on donner sur CPC Gifts ?</h4>
		<p>Tout ce que vous voulez du moment que ça n'enfreint pas les règles de la plateforme sur laquelle vous avez acheté le jeu.<br />
		Les bons de réductions sont interdits à moins d'être très intéressant (80% ou plus).<br />
		Les clés séparées de jeux provenant d'un bundle sont interdites tant que le bundle est encore en vente.
		</p>
		
		<h4>Puis-je créer un concours sans tirage au sort ?</h4>
		<p>
		Oui !<br />
		Pour cela il vous suffit de créer le concours normalement, en prenant soin d'indiquer dans les règles personnalisées qu'il s'agit d'un concours sans tirage au sort.
		Les participants devront s'inscrire en cliquant sur le bouton "Participer".<br />
		À la fin du concours, un des participants sera tout de même tiré au sort. Il vous suffira de faire une demande de reroll en indiquant le vainqueur que vous avez désigné.
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
		Vous pouvez demander la désignation d'un nouveau gagnant aux admins 
		en utilisant le formulaire dédié sur la page du giveaway.
		</p>
		
		<h4>Comment transmettre son cadeau au gagnant ?</h4>
		<p>
		S'il s'agit d'une clé, ou d'un lien cadeau, il suffit de l'envoyer directement par MP au gagnant.
		S'il ne peut plus recevoir de MP, postez sur le <a href="<%= Constants.TOPIC_CANARDS_GENEREUX %>">topic</a>
		en lui demandant de vous contacter. <br />
		S'il s'agit d'un cadeau Steam ou d'une autre plateforme, utilisez le système d'échange de la plateforme en question. <br />
		Il est interdit d'utiliser un service tiers (Steamgifts, Steamtrades, etc.) dans le but d'augmenter sa contribution ou sa réputation
		sur ces sites.
		</p>
		
		<h4>Je n'ai pas de nouvelles du gagnant.</h4>
		<p>Si le concours est terminé depuis plus d'une semaine, vous pouvez demander la désignation d'un
		nouveau gagnant en utilisant le formulaire dédié sur la page du giveaway.
		</p>
		
		<hr />
		<h3>Le site CPC Gifts</h3>
		<hr />
		<h4>J'ai trouvé un bug, une faute d'orthographe, etc...</h4>
		<p>
		Merci de me le signaler dans le <a href="<%= Constants.TOPIC_DEV %>">topic dédié à CPC Gifts</a>.
		</p>
		
		<h4>Qui sont les admins ?</h4>
		<p>
			<ul>
				<li><a href="http://cpcgifts.appspot.com/user?userID=6001">bastien09</a> (créateur de CPC Gifts)</li>
				<li><a href="http://cpcgifts.appspot.com/user?userID=131001">sebarnolds</a></li>
				<li><a href="http://cpcgifts.appspot.com/user?userID=12005">SiGarrett</a></li>
				<li><a href="http://cpcgifts.appspot.com/user?userID=6005">Yakaaa</a></li>
			</ul>
			Merci de ne pas nous contacter individuellement pour des demandes pouvant se faire via CPC Gifts.
		</p>
		
		<h4>Je suis développeur et souhaite contribuer à l'amélioration du site / réutiliser le code pour un projet.</h4>
		<p>
		Les sources sont hébergées sur <a href="<%= Constants.GITHUB %>">github</a> et sont sous libres de droits.<br />
		Pour la contribution au développement, toute la communication se fait sur <a href="<%= Constants.TOPIC_DEV %>">le topic dédié</a>.
		</p>

		<hr />
		
		<footer>
		</footer>
	
	</div>
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	

</body>
</html>
