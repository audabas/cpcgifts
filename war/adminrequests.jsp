<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="fr.cpcgifts.model.AdminRequest"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="fr.cpcgifts.persistance.AdminRequestPersistance"%>
<%@page import="com.google.appengine.api.memcache.jsr107cache.GCacheFactory"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.jsr107cache.CacheException"%>
<%@page import="java.util.Collections"%>
<%@page import="net.sf.jsr107cache.CacheManager"%>
<%@page import="net.sf.jsr107cache.Cache"%>
<%@page import="java.util.List"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<% UserService userService = UserServiceFactory.getUserService(); %>
<%	User user; %>
<%
	user = userService.getCurrentUser();
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
<title>Requêtes administrateurs - Canard PC Gifts</title>
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
	
		<div class="tabbable">
			<ul class="nav nav-tabs">
				<li class="active"><a href="#requests" data-toggle="tab">Demandes</a></li>
				<li><a href="#info" data-toggle="tab">Mémento administrateur</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active" id="requests">

	<%
	
		List<AdminRequest> adminrequests = AdminRequestPersistance.getAllRequestsFromCache();
	
	%>


		<%
			for(AdminRequest ar : adminrequests) {
		%>
		
		<div>
			<%@ include file="/templates/adminrequestview.jspf" %>
			<hr>
		</div>
		
		<%
			}
		%>
		
		</div>
			<div class="tab-pane" id="info">
				<%@ include file="memento.jspf" %>
			</div>
		</div>
		</div>

	</div>
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	
	<script src="js/vendor/fuse.min.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("div[data-type='admin-request']").each(function(i) {
				var id = $(this).data("arid");
				var type = $(this).data("artype");
				var state = $(this).data("arstate");
				
				/* Markdown sur les textes */
				var text = $("#reqtext-" + id).val();
				
				$("#reqtextdisplay-" + id).html(markdown.toHTML(text));
				
				/* Écriture de l'état de la demande */
				var reqstatedisplay = $("#statedisplay-" + id);
				switch (state) {
	 				case "<%= AdminRequest.State.Open %>":
						reqstatedisplay.html("Ouverte");
	 					reqstatedisplay.addClass("label-warning");
					 	break;
				 	case "<%= AdminRequest.State.Processed %>":
				 		reqstatedisplay.html("Traitée");
	 					reqstatedisplay.addClass("label-success");
	 					$(this).parent().addClass("closed-admin-request");
					 	break;
			 		case "<%= AdminRequest.State.Denied %>":
			 			reqstatedisplay.html("Refusée");
	 					reqstatedisplay.addClass("label-important");
	 					$(this).parent().addClass("closed-admin-request");
						break;
				 	default:
						break;
				}
				
				/* Écriture du type de demande */
				var reqtypedisplay = $("#typedisplay-" + id);
				switch (type) {
	 				case "<%= AdminRequest.Type.Reroll %>":
	 					reqtypedisplay.html("Reroll");
					 	break;
				 	case "<%= AdminRequest.Type.TitleModification %>":
				 		reqtypedisplay.html("Modification du titre");
					 	break;
			 		case "<%= AdminRequest.Type.RulesModification %>":
			 			reqtypedisplay.html("Modification des règles");
						break;
			 		case "<%= AdminRequest.Type.ReportPost %>":
			 			reqtypedisplay.html("Commentaire signalé");
						break;
			 		case "<%= AdminRequest.Type.ReportUser %>":
			 			reqtypedisplay.html("Utilisateur signalé");
						break;
				 	default:
						break;
				}
				
				/* Ajout des listeners sur les boutons */
				$("#proceed-" + id).click(function (e) {
					editRequest(id, "process");
				});
				$("#deny-" + id).click(function (e) {
					editRequest(id, "deny");
				});
				
            });
			
            $("textarea[id^='comment-']").each(function(i) {
            	$("#" + $(this).attr("id") + "-display").html(markdown.toHTML($(this).val()));
            });
	            
		});
		
		function editRequest(requestId,reqType) {
			$.post( "/adminrequest",
					{ req: reqType, reqid: requestId }
			);
			
			alert("Statut de la demande modifié.");
			
			location.reload(true);
		}
	</script>

</body>
</html>
