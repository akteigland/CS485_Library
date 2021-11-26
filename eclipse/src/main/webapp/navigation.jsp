<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Library</title>
<link rel="stylesheet" href="main.css">
</head>
<body>
	<h1>The Library</h1>
	<form class="navigation" action="BookServlet" method="get">
		<input type="submit" name="books" Value="New Arrivals">
	</form>
	<%
	if (session.getAttribute("user") == null) {
	%>
	<form class="navigation" action="index.jsp" method="get">
		<input type="submit" Value="Login"></input>
	</form>
	<form class="navigation" action="register.jsp" method="get">
		<!-- goes to register.jsp -->
		<input type="submit" Value="Register"></input>
	</form>
	<%
	} else {
	%>
	<form class="navigation" action="goodbye.jsp" method="get">
		<input type="submit" Value="Logout"></input>
	</form>
	<%
	}
	%>
	<form class="searchBox" action="BookServlet" method="get">
		<p>
			<label for="title">Title:</label>
			<input type="text" id="title" size=30 name="title" />
		</p>
		<p>
			<label for="author">Author:</label>
			<input type="text" id="author" size=30 name="author" />
		</p>
		<p>
			<label for="genre">Genre:</label>
			<input type="text" id="genre" size=30 name="genre" />
		</p>
		<p>
			<label for="award">Award:</label>
			<input type="text" id="award" size=30 name="award" />
		</p>
		<p>
			<label></label>
			<input type="submit" name="books" Value="Search">
		</p>
	</form>
</body>
</html>