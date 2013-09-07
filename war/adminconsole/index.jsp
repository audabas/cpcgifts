<html>
<head>
	<title>Admin console</title>
</head>
<body>
<h1>Admin console</h1>

<h2>Ga Admin</h2>
<form action="/admin/addWinner" method="get">
	<input type="radio" name="reqtype" value="addWinner" > Ajouter un gagnant
	<input type="radio" name="reqtype" value="removeWinner" > Supprimer un gagnant
	Giveaway : <input type="text" name="gaid">
	Gagnant : <input type="text" name="user">
	<input type="submit" value="Valider">
</form>

<h2>Update datastore model</h2>
<ul>
<li><a href="gaupdate1.jsp">Update giveaways winners (single->set) and description (short[String]->long[Text])</a></li>
</ul>
</body>
</html>