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

	<%@ include file="menubar.jspf"%>

	<div class="container">

	<%
		List<Giveaway> opengas = GAPersistance.getOpenGAs();
	%>

	<div class="row hero-unit text-center">
	<h1>CPC Gifts</h1>
	<h2>Le site des canards généreux</h2>
	<br />
	<%
		if(opengas.size() > 3) {
	%>
		<div id="featuredGAs" class="carousel slide" style="width: 460px; margin : auto;">
			<ol class="carousel-indicators">
				<li data-target="#featuredGAs" data-slide-to="0" class="active"></li>
				<li data-target="#featuredGAs" data-slide-to="1"></li>
				<li data-target="#featuredGAs" data-slide-to="2"></li>
			</ol>
			<!-- Carousel items -->
			<div class="carousel-inner">
				<%= ViewTools.gaCarouselView(opengas.get(0), true) %>
				<%= ViewTools.gaCarouselView(opengas.get(1), false) %>
				<%= ViewTools.gaCarouselView(opengas.get(2), false) %>
			</div>
			<!-- Carousel nav -->
			<a class="carousel-control left" href="#featuredGAs"
				data-slide="prev">&lsaquo;</a> <a class="carousel-control right"
				href="#featuredGAs" data-slide="next">&rsaquo;</a>
		</div>
		<%
		}
	%>
	</div>

	



		<%
			for(Giveaway ga : opengas) {
		%>

		<%=ViewTools.gaView(ga)%>
		<hr>

		<%
			}
				
				GAPersistance.closePm();
		%>


		<footer>
			<p><%@ include file="footer.jspf"%></p>
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

	<script src="js/main.js"></script>
	

</body>
</html>
