<%@page import="fr.cpcgifts.utils.Constants"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.googlecode.objectify.Key"%>
<%@page import="fr.cpcgifts.persistance.GiveawayPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway" %>

<% 
	String gaid = request.getParameter("gaid");
	Long gid = Long.parseLong(gaid);
	Key<Giveaway> k = Key.create(Giveaway.class, gid);
	
	Giveaway ga = GiveawayPersistance.getGA(k);
	
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.FRANCE);
%>
<html>
<head>
	<title>Giveaway Edition</title>
</head>

<body>

<h1><%= ga.getTitle() %></h1>

<form action="/admin/changeGiveawayDate" method="get">
	<input name="reqtype" value="changeGiveawayDate" type="hidden">
	<input name="gaid" value="<%= ga.getId() %>" type="hidden">
	<label>Date :</label> <input type="text" value="<%= sdf.format(ga.getEndDate()) %>" name="newDate">
	<input type="submit" value="Valider">
</form>

</body>
</html>