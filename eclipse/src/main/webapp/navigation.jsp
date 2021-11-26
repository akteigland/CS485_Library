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
	<form class="navigation" style="margin-left: 10px;" action="BookServlet" method="get">
		<input type="submit" name="books" Value="New Arrivals">
	</form>
	<form class="navigation" action="EventServlet" method="get">
		<input type="submit" name="events" Value="Events">
	</form>
	<form class="navigation" action="RoomServlet" method="get">
		<input type="submit" name="rooms" Value="Study Rooms">
	</form>
</body>
</html>