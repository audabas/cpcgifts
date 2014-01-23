<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="javax.jdo.JDOObjectNotFoundException"%>
<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="fr.cpcgifts.persistance.CommentPersistance"%>
<%@page import="fr.cpcgifts.model.Comment"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="fr.cpcgifts.persistance.CpcUserPersistance"%>
<%@page import="fr.cpcgifts.model.CpcUser"%>
<% 

String scid = request.getParameter("commentID");

Long cid = Long.parseLong(scid);

Key k = KeyFactory.createKey(Comment.class.getSimpleName(), cid);

Comment c = null;

try {
	c = CommentPersistance.getCommentFromCache(k);
} catch(JDOObjectNotFoundException e) {
	out.write("<p>Commentaire supprimé : " + scid + "</p>");
	return;
}

CpcUser u = CpcUserPersistance.getUserFromCache(c.getAuthor());

%>

<div class='media'>
	<a class='pull-left' href='/user?userID=<%= u.getKey().getId() %>'>
		<img alt="Avatar" class='media-object img-small-avatar' src='<%= u.getAvatarUrl() %>'>
	</a>
	<div class='media-body'>
		<h6 class='media-heading'>
			<a href='/user?userID=<%= u.getKey().getId() %>'><%= u.getCpcNickname() %></a>
			<small>(il y a <%= DateTools.dateDifference(c.getCommentDate()) %>)</small>
		</h6>
		<textarea class='hidden' id='comment-<%= c.getKey().getId() %>'><%= c.getCommentText() %></textarea>
		<p class='' id='comment-<%= c.getKey().getId() %>-display'></p>
	</div>
</div>