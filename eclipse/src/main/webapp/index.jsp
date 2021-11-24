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
		<form action="index.jsp" method="post"> <!--  use post to hide password in URL -->
			<b>Login</b><br>
			Username: <input type="text" id="user" name="username"/><br>
			Password: <input type="password" id="pass" name="password"/><br>
			<input type="submit" Value="Submit"></input>
		</form>
		<form action="register.jsp" method="get"> <!-- goes to register.jsp -->
			<input type="submit" Value="Register"></input>
		</form>
		<%
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			// if user entered a username
			if (username != null && username.trim() != ""){
				if (password != null && password.trim() != ""){
					if (DBentry.checkLogin(username, password)){
						%><script type="text/javascript">window.location.replace("welcome.jsp")</script><%	
					} else {
						%><div class="errorMessage">Incorrect username or password</div><%
					}
				} else {
					%><div class="errorMessage">You must enter a password</div><%
				}
			}%>
	</body>
</html>