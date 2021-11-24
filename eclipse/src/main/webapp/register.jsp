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
			<b>Register</b><br>
			Username: <input type="text" id="user" name="username" required/><br>
			Password: <input type="password" id="pass" name="password" required/><br>
			Confirm Password: <input type="password" id="pass2" name="password2" required/><br>
			First Name: <input type="text" id="first" name="firstname" required/><br>
			Last Name: <input type="text" id="last" name="lastname" required/><br>
			<div style="min-height: 20px; color: #FF0000;">${errorMessage}</div>
			<input type="submit" name="submitRegister"  Value="Submit"></input>
		</form>
		<form action="index.jsp" method="get"> <!-- goes to index.jsp -->
			<input type="submit" Value="Back"></input>
		</form>
	</body>
</html>