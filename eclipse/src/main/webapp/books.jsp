<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Library</title>
		<link rel="stylesheet" href="books.css">
	</head>
	<body>
		<%@ page import="java.io.*" %> 
		<%@ page import="db.*" %>
		<h1>The Library</h1>
		<%
		if (session.getAttribute("user") == null) {
			%>
			<form action="index.jsp" method="get">
				<input type="submit" Value="Login"></input>
			</form>
			<%
		} else {
			%>
			<form action="goodbye.jsp" method="get">
				<input type="submit" Value="Logout"></input>
			</form>
			<%
		}
		%>
	</body>
	<%
		out.print(request.getAttribute("result"));
	%>
</html>