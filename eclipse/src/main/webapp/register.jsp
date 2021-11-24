<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Library</title>
	</head>
	<body>
		<%@ page import="java.io.*" %> 
		<%@ page import="db.*" %>
		<form action="register.jsp" method="get">
			Register your account:<br>
			Username: <input type="text" id="user" name="username"/><br>
			Password: <input type="password" id="pass" name="password"/><br>
			Confrim Password: <input type="password" id="pass2" name="password2"/><br>
			First Name: <input type="text" id="first" name="firstname"/><br>
			Last Name: <input type="text" id="last" name="lastname"/><br>
			<input type="submit" Value="Submit"></input>
		</form>
		<form action="index.jsp" method="get"> <!-- goes to index.jsp -->
			<input type="submit" Value="Back"></input>
		</form>
		<%
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("firstname");
			
			// if user entered a username
			if (username != null && username.trim() != ""){
				if (password != null && password.trim() != ""){
					if (password.equals(password2)){
					if (DBentry.checkUser(username)){
						%><div class="errorMessage">Username is already taken</div><%
					} else {
						DBentry.addUser(username, password, firstname, lastname);
						%><script type="text/javascript">window.alert("Sucessfully registered!")</script><%	
					}
					} else {
						%><div class="errorMessage">Passwords do not match</div><%
					}
				} else {
					%><div class="errorMessage">You must enter a password</div><%
				}
			}%>
	</body>
</html>