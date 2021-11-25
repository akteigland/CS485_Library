<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Library</title>
	</head>
	<body>
		<%@ page import="java.io.*" %> 
		<%@ page import="db.*" %>
		<h1>The Library</h1>
		<form action="BookServlet" method="post">
			<input type="submit" name="books" Value="View Books">
		</form>
		<form action="goodbye.jsp" method="get">
			<input type="submit" Value="Logout"></input>
		</form>
	</body>
</html>