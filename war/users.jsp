<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="java.util.List"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<% UserService userService = UserServiceFactory.getUserService(); %>
<% User user = userService.getCurrentUser(); %>
	
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
<title>Liste des utilisateurs - CPCGifts</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<%@ include file="css.jspf" %>

<script src="/js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<%@ include file="menubar.jspf"%>

	<div class="container">


		<%
		
		List<CpcUser> users = CpcUserPersistance.getAllUsers();
		
				
		for(CpcUser userToDisplay : users) {
		%>
		
		<div>
			
			<%@ include file="/templates/userview.jspf" %>
			<hr>
		</div>

		<%
		}
		%>
		
		<%@ include file="footer.jspf"%>


	</div>
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	
	<script src="/js/vendor/fuse.min.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			
			var users = [];
			
			$("div[data-type='user']").each( function(i,e) {
			    var u = Object(); 
			    u.id = $(e).data("id");
			    u.nickname = "" + $(e).data("nickname");
			    users.push(u);
			});
			
			var options = {
				keys: ['nickname'],
				threshold : 0.15
			}
			
			var f = new Fuse(users, options);
			
			$("#search").removeClass("hidden");
			
			$("#search").keyup(function () {
				if($(this).val() != "") {
					$("div[data-type='user']").parent().addClass("hidden");
					
					var results = f.search($(this).val());
					
					$(results).each(function(i, e) {
						$("div[data-type='user'][data-id='"+ e.id +"']").parent().removeClass("hidden");
						console.log($("div[data-type='user'][id='"+ e.id +"']"));
					});
					
				} else {
					$("div[data-type='user']").parent().removeClass("hidden");
				}
				
			});
			
		});
	</script>

</body>
</html>
