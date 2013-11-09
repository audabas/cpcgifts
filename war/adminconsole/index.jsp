<html>
<head>
	<title>Admin console</title>
</head>
<body>
<h1>Admin console</h1>

<form action="/admin/gaWinner" method="get">
	<input type="radio" name="reqtype" id="addWinner" value="addWinner" > <label for="addWinner">Ajouter un gagnant</label>
	<input type="radio" name="reqtype" id="removeWinner" value="removeWinner" checked="checked" > <label for="removeWinner">Supprimer un gagnant</label>
	<label>Giveaway :</label> <input type="text" name="gaid">
	<label>Gagnant :</label> <input type="text" name="userid">
	<input type="submit" value="Valider">
</form>

<form action="/admin/gaEntry" method="get">
	<input type="radio" name="reqtype" id="addEntry" value="addEntry" > <label for="addEntry">Ajouter une entrée</label>
	<input type="radio" name="reqtype" id="removeEntry" value="removeEntry" checked="checked" > <label for="removeEntry">Supprimer une entrée</label>
	<label>Giveaway :</label> <input type="text" name="gaid">
	<label>Utilisateur :</label> <input type="text" name="userid">
	<input type="submit" value="Valider">
</form>

<form action="/admin/gaComment" method="get">
	<!-- <input type="radio" name="reqtype" id="addComment" value="addComment" > <label for="addComment">Ajouter un commentaire</label> -->
	<input type="radio" name="reqtype" id="removeComment" value="removeComment" checked="checked" > <label for="removeComment">Supprimer un commentaire</label>
	<label>Giveaway :</label> <input type="text" name="gaid">
	<label>Commentaire :</label> <input type="text" name="commentid">
	<input type="submit" value="Valider">
</form>

<form action="/admin/deleteGA" method="get">
	<!-- <input type="radio" name="reqtype" id="addComment" value="addComment" > <label for="addComment">Ajouter un commentaire</label> -->
	<input type="radio" name="reqtype" id="deleteGiveaway" value="deleteGiveaway" checked="checked" > <label for="deleteGiveaway">Supprimer un giveaway</label>
	<label>Giveaway :</label> <input type="text" name="gaid">
	<input type="submit" value="Valider">
</form>

<h2>Update datastore model</h2>
<ul>
<!-- <li><a href="gaupdate1.jsp">Update giveaways winners (single->set) and description (short[String]->long[Text])</a></li> -->
<!-- <li><a href="userupdate1.jsp">Update users (all unbanned)</a></li> -->
<!-- <li><a href="setUpdate.jsp">The Set update</a></li> -->
</ul>
</body>
</html>