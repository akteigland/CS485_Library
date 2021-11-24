package db;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
	    HttpSession session = request.getSession();
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		// if user entered a username
		if (username != null && username.trim() != ""){
			if (password != null && password.trim() != ""){
				if (DBentry.checkLogin(username, password)){
					session.setAttribute("user", username);
					response.sendRedirect("welcome.jsp");
				} else {
	                session.setAttribute("errorMessage", "Incorrect username or password");
					response.sendRedirect("index.jsp");
				}
			} else {
				session.setAttribute("errorMessage", "You must enter a password");
				response.sendRedirect("index.jsp");
			}
		} else {
			session.setAttribute("errorMessage", "You must enter a username");
			response.sendRedirect("index.jsp");
		}
	}
}