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
		<form action="register.jsp" method="post"> <!--  use post to hide password in URL -->
			<b>Register</b><br>
			Username: <input type="text" id="user" name="username" required/><br>
			Password: <input type="password" id="pass" name="password" required/><br>
			Confirm Password: <input type="password" id="pass2" name="password2" required/><br>
			First Name: <input type="text" id="first" name="firstname" required/><br>
			Last Name: <input type="text" id="last" name="lastname" required/><br>
			<input type="submit" name="submitRegister"  Value="Submit"></input>
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
			if (request.getParameter("submitRegister") != null) { // if form was submitted
				if (password.equals(password2)){
					String message  = DBentry.addUser(username, password, firstname, lastname);
					if (message != null) {
						%><div class="errorMessage"><%=message%></div><%	
					} else {
						%><script type="text/javascript">window.alert("Sucessfully registered!")</script><%	
					}
				} else {
					%><div class="errorMessage">Passwords do not match</div><%
				}
			}%>
	</body>
</html>