<html>
<head>
	<title>Admin console</title>
</head>
<body>
<h1>Admin console</h1>

<form action="/admin/gaWinner" method="get">
	<input type="radio" name="reqtype" value="addWinner" > Ajouter un gagnant
	<input type="radio" name="reqtype" value="removeWinner" > Supprimer un gagnant
	Giveaway : <input type="text" name="gaid">
	Gagnant : <input type="text" name="userid">
	<input type="submit" value="Valider">
</form>

<form action="/admin/gaEntry" method="get">
	<input type="radio" name="reqtype" value="addEntry" > Ajouter une entrée
	<input type="radio" name="reqtype" value="removeEntry" > Supprimer une entrée
	Giveaway : <input type="text" name="gaid">
	Utilisateur : <input type="text" name="userid">
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