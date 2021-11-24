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
		<form action="${pageContext.request.contextPath}/LoginServlet" method="post"> <!--  use post to hide password in URL -->
			<b>Login</b><br>
			Username: <input type="text" id="user" name="username"/><br>
			Password: <input type="password" id="pass" name="password"/><br>
			<input type="submit" Value="Submit"></input>
		</form>
		<div style="min-height: 20px; color: #FF0000;">${errorMessage}</div>
		<form action="register.jsp" method="get"> <!-- goes to register.jsp -->
			<input type="submit" Value="Register"></input>
		</form>
	</body>
</html>