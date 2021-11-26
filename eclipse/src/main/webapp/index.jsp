<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Library</title>
<link rel="stylesheet" href="main.css">
</head>
<body>
	<%@ page import="java.io.*"%>
	<%@ page import="db.*"%>
	<div class="infoColumn">
		<%@ include file="navigation.jsp"%>
		<%
		if (session.getAttribute("user") != null) {
		%>
		<form class="navigation" action="LoginServlet" method="get">
			<input type="submit" Value="Logout"></input>
		</form>
		<%
		}
		%>
		<div style="min-height: 20px; color: #00cc00;">${message}</div>
		<form class="searchBox" action="BookServlet" method="get">
			<h2>Search</h2>
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
${result}
</html>