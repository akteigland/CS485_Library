<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Library</title>
<link rel="stylesheet" href="main.css">
</head>
<body>
	<div class="infoColumn">
		<%@ include file="navigation.jsp"%>
		<div class="successMessage">${message}</div>
		<h2>Search for Books</h2>
		<form class="searchBox" action="BookServlet" method="get">
			<p>
				<label for="title">Title:</label> <input type="text" id="title" size=30 name="title" />
			</p>
			<p>
				<label for="author">Author:</label> <input type="text" id="author" size=30 name="author" />
			</p>
			<p>
				<label for="genre">Genre:</label> <input type="text" id="genre" size=30 name="genre" />
			</p>
			<p>
				<label for="award">Award:</label> <input type="text" id="award" size=30 name="award" />
			</p>
			<p>
				<label for="lang">Language:</label> <input type="text" id="lang" size=30 name="lang" />
			</p>
			<p>
				<label></label> <input type="submit" name="books" Value="Search">
			</p>
		</form>
		<%
		if (session.getAttribute("user") == null) {
		%>
		<%@ include file="login.jsp"%>
		<%@ include file="register.jsp"%>
		<%
		}
		session.setAttribute("message", ""); // clear message
		%>
	</div>
</body>
</html>