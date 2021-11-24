package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	Connection conn = null;
	static String dbPath = "jdbc:mysql://localhost:3306";

	public void init() throws ServletException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbPath, "root", "admin");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();

		if (request.getParameter("submitRegister") != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("firstname");

			// if user entered a username
			if (request.getParameter("submitRegister") != null) { // if form was submitted
				if (password.equals(password2)) {
					String message = addUser(username, password, firstname, lastname);
					if (message != null) {
						session.setAttribute("errorMessage", message);
						response.sendRedirect("register.jsp");
					} else {
						session.setAttribute("user", username);
						response.sendRedirect("welcome.jsp");
					}
				} else {
					session.setAttribute("errorMessage", "Passwords must match");
					response.sendRedirect("register.jsp");
				}
			}
		} else {
			String username = request.getParameter("username");
			String password = request.getParameter("password");

			// if user entered a username
			if (username != null && username.trim() != "") {
				if (password != null && password.trim() != "") {
					if (checkLogin(username, password)) {
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

	public void destroy() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if login credentials are valid
	 * 
	 * @param username
	 * @param password
	 * @return a boolean
	 */
	public boolean checkLogin(String username, String password) {
		try {
			PreparedStatement sql = conn.prepareStatement("SELECT * FROM cs485_project.accounts WHERE username = \""
					+ username + "\" AND password = \"" + password + "\";");
			ResultSet results;
			results = sql.executeQuery();
			boolean hasMatch = results.next();
			return hasMatch;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Adds a user to the database
	 * 
	 * @param username
	 * @param password
	 * @param first    - first name of the user
	 * @param last     - last name of the user
	 * @return a String error message or null
	 */
	public String addUser(String username, String password, String first, String last) {
		try {
			Statement sql = conn.createStatement();
			sql.executeUpdate("INSERT INTO cs485_project.accounts VALUES (\"" + username + "\", \"" + password
					+ "\", \"" + first + "\", \"" + last + "\");");
			return null;
		} catch (SQLIntegrityConstraintViolationException ex) {
			return "Username " + username + " is already taken.";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error, please try again.";
		}
	}
}