<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Library</title>
</head>
<body>
	<%@ page import="java.io.*"%>
	<%@ page import="db.*"%>
	<form action="${pageContext.request.contextPath}/LoginServlet" method="post">
		<!--  use post to hide password in URL -->
		<h2>Login</h2>
		<p>
			<label for="user">Username:</label><input type="text" id="user" name="username" />
		</p>
		<p>
			<label for="pass">Password:</label><input type="password" id="pass" name="password" />
		</p>
		<p>
			<label></label><input type="submit" Value="Login"></input>
		</p>
		<label></label>
		<div style="min-height: 20px; color: #FF0000;">${loginErrorMessage}</div>
	</form>
</body>
</html>