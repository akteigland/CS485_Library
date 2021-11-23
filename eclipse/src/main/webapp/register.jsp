<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>CS485 Homework</title>
	</head>
	<body>
		<%@ page import="java.io.*" %> 
		<%@ page import="db.*" %>
		<form action="register.jsp" method="get">
			Register your account:<br>
			Username: <input type="text" id="user" name="username"/><br>
			Password: <input type="password" id="pass" name="password"/><br>
			<input type="submit" Value="Submit"></input>
		</form>
		<form action="index.jsp" method="get"> <!-- goes to index.jsp -->
			<input type="submit" Value="Back"></input>
		</form>
		<%
			DBentry dbentry = new DBentry();
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			// if user entered a username
			if (username != null && username.trim() != ""){
				if (password != null && password.trim() != ""){
					if (dbentry.checkUser(username)){
						%><div class="errorMessage">Username is already taken</div><%
					} else {
						dbentry.addUser(username, password);
						dbentry.close();
						%><script type="text/javascript">window.alert("Sucessfully registered!")</script><%	
					}
				} else {
					%><div class="errorMessage">You must enter a password</div><%
				}
			}%>
	</body>
</html>