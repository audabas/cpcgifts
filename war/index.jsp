<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.memcache.jsr107cache.GCacheFactory"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.jsr107cache.CacheException"%>
<%@page import="java.util.Collections"%>
<%@page import="net.sf.jsr107cache.CacheManager"%>
<%@page import="net.sf.jsr107cache.Cache"%>
<%@page import="java.util.List"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
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

<script src="js/vendor/modernizr-2.6.2.min.js"></script>

<%@ include file="css.jspf" %>

</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<%@ include file="menubar.jspf"%>

	<div class="container">

	<%
	
	Cache cache;
	List<Giveaway> opengas = null;
	
	Map props = new HashMap();
    props.put(GCacheFactory.EXPIRATION_DELTA, 300); // on garde la liste en cache 5 minutes
	
	try {
        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
                    
        opengas = (List<Giveaway>) cache.get("openGAsList");
		
		if(opengas == null) {
			opengas = GAPersistance.getOpenGAs();
			cache.put("openGAsList", opengas);
		}
		
    } catch (CacheException e) {
    	opengas = GAPersistance.getOpenGAs();
    }
	%>

	<div class="row hero-unit text-center">
	<h1>CPC Gifts</h1>
	<h2>Le site des canards généreux</h2>
	<br />
	<%
		if(opengas.size() > 3) {
	%>
		<div id="featuredGAs" class="carousel slide visible-desktop" style="width: 460px; margin : auto;">
			<ol class="carousel-indicators">
				<li data-target="#featuredGAs" data-slide-to="0" class="active"></li>
				<li data-target="#featuredGAs" data-slide-to="1"></li>
				<li data-target="#featuredGAs" data-slide-to="2"></li>
			</ol>
			<!-- Carousel items -->
			<div class="carousel-inner">
				
				<% 
				Giveaway ga = opengas.get(0);
				boolean active = true; 
				%>				
				<%@ include file="/templates/gacarouselview.jspf" %>
				
				<% 
				ga = opengas.get(1);
				active = false; 
				%>				
				<%@ include file="/templates/gacarouselview.jspf" %>
				
				<% 
				ga = opengas.get(2);
				active = false; 
				%>				
				<%@ include file="/templates/gacarouselview.jspf" %>

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
		
		<div>
			<%@ include file="/templates/gaview.jspf" %>
			<hr>
		</div>
		
		<%
			}
				
				GAPersistance.closePm();
		%>
		
		<div class="row">
			<div class="span3">
				<h4>Statistiques : </h4>
				<ul>
					<li><strong><%= CpcUserPersistance.getAllUsersFromCache().size() %></strong> membres</li>
					<li><strong><%= opengas.size() %></strong> concours en cours</li>
					<li><strong><%= GAPersistance.getAllGASize() %></strong> concours créés</li>
				</ul>
			</div>
		</div>
		<hr />

		<div class="row">
			<div class="span3"><h4>Rechercher un concours : </h4></div>
			<div class="span9">
				<script>
				  (function() {
				    var cx = '007220030929328207636:wx1r0szfjry';
				    var gcse = document.createElement('script');
				    gcse.type = 'text/javascript';
				    gcse.async = true;
				    gcse.src = (document.location.protocol == 'https:' ? 'https:' : 'http:') +
				        '//www.google.com/cse/cse.js?cx=' + cx;
				    var s = document.getElementsByTagName('script')[0];
				    s.parentNode.insertBefore(gcse, s);
				  })();
				</script>
				<gcse:searchbox-only></gcse:searchbox-only>
			</div>
		</div>
		<hr />
		
		
		<%@ include file="footer.jspf"%>
		
		

	</div>
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	
	<script src="js/vendor/fuse.min.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			
			var giveaways = [];
			
			$("div[data-type='giveaway']").each( function(i,e) {
			    var ga = Object(); 
			    ga.id = $(e).data("id");
			    ga.title = "" + $(e).data("title");
			    ga.author = "" + $(e).data("author");
			    giveaways.push(ga);
			});
			
			var options = {
				keys: ['title', 'author'],
				threshold : 0.30
			}
			
			var f = new Fuse(giveaways, options);
			
			$("#search").removeClass("hidden");
			
			$("#search").keyup(function () {
				if($(this).val() != "") {
					$("div[data-type='giveaway']").parent().addClass("hidden");
					
					var results = f.search($(this).val());
					
					$(results).each(function(i, e) {
						$("div[data-type='giveaway'][data-id='"+ e.id +"']").parent().removeClass("hidden");
						console.log($("div[data-type='giveaway'][id='"+ e.id +"']"));
					});
					
				} else {
					$("div[data-type='giveaway']").parent().removeClass("hidden");
				}
				
			});
		});
	</script>

</body>
</html>
