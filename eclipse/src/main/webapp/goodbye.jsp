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
		Sucessfully logged out.
		<form action="index.jsp" method="get"> <!-- goes to index.jsp -->
			<input type="submit" Value="Back"></input>
		</form>
	</body>
	<% session.invalidate(); %>
</html>