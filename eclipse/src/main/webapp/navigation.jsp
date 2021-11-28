<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Library</title>
</head>
<body>
	<h1>The Library</h1>
	<%
	if (session.getAttribute("user") != null) {
	%>
	<h4>Logged in as ${name}</h4>
	<%
	}
	%>
	<form class="navigation" style="margin-left: 10px;" action="BookServlet" method="get">
		<input type="submit" name="books" Value="New Arrivals">
	</form>
	<form class="navigation" action="EventServlet" method="get">
		<input type="submit" name="events" Value="Events">
	</form>
	<%
	// only show these buttons if logged in
	if (session.getAttribute("user") != null) {
	%>
	<form class="navigation" action="RoomServlet" method="get">
		<input type="submit" name="rooms" Value="Study Rooms">
	</form>
	<form class="navigation" action="BookServlet" method="get">
		<input type="submit" name="books" Value="Borrowed Books">
	</form>
	<form class="navigation" action="LoginServlet" method="get">
		<input type="submit" Value="Logout"></input>
	</form>
	<%
	}
	%>
</body>
</html>