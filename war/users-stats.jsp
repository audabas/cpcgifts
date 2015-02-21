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

<link rel="stylesheet" href="//cdn.datatables.net/1.10.5/css/jquery.dataTables.css">
<link rel="stylesheet" href="http://cdn.datatables.net/plug-ins/f2c75b7247b/integration/bootstrap/2/dataTables.bootstrap.css">

<script src="/js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<%@ include file="menubar.jspf"%>

	<div class="container">
	
		<table id="usertable" class="display table table-striped table-bordered" cellspacing="0" width="100%">
	        <thead>
	            <tr>
	                <th>Identifiant</th>
	                <th>Nombre de concours crées</th>
	                <th>Nombre de participations</th>
	                <th>Nombre de concours gagnés</th>
	            </tr>
	        </thead>
	 
	        <tbody>
	        	<%
		
				List<CpcUser> users = CpcUserPersistance.getAllUsers();
				
						
				for(CpcUser userToDisplay : users) {
				%>
	            <tr>
	            	<td><a href="/user?userID=<%=  userToDisplay.getKey().getId() %>"><%= userToDisplay.getCpcNickname() %></a></td>
	            	<td><%= userToDisplay.getGiveaways().size() %></td>
	            	<td><%= userToDisplay.getEntries().size() %></td>
	            	<td><%= userToDisplay.getWon().size() %></td>
	            </tr>
	            
				<%
				}
				%>
            </tbody>
		</table>
		
		<br />
		<br />

		
		<%@ include file="footer.jspf"%>


	</div>
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	
	<script src="//cdn.datatables.net/1.10.5/js/jquery.dataTables.min.js"></script>
	<script src="http://cdn.datatables.net/plug-ins/f2c75b7247b/integration/bootstrap/2/dataTables.bootstrap.js"></script>
	
	
	<script type="text/javascript">
		$(document).ready(function() {
		    $('#usertable').DataTable( {
		    	"paging" : false,
		    	"language": {
	                "url": "//cdn.datatables.net/plug-ins/f2c75b7247b/i18n/French.json"
	            }
		    } );
		} );
	</script>

</body>
</html>
