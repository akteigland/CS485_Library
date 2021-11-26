package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	Connection conn = null;
	static String dbPath = "jdbc:mysql://localhost:3306";

	@Override
	public void init() throws ServletException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbPath, "root", "admin");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.invalidate();
		response.sendRedirect("BookServlet");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();

		if (request.getParameter("submitRegister") != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");

			if (username == null || password == null || password2 == null || firstname == null || lastname == null
					|| username.isBlank() || password.isBlank() || password2.isBlank() || firstname.isBlank()
					|| lastname.isBlank()) {
				request.setAttribute("registerErrorMessage", "All fields are required");
				request.getRequestDispatcher("index.jsp").forward(request, response);
				return;
			}

			// if user entered a username
			if (password.equals(password2)) {
				String message = addUser(username, password, firstname, lastname);
				if (message != null) {
					request.setAttribute("registerErrorMessage", message);
					request.getRequestDispatcher("index.jsp").forward(request, response);
				} else {
					session.setAttribute("user", username);
					session.setAttribute("name", getName(username));
					session.setAttribute("message", "Registration Success");
					response.sendRedirect("BookServlet");
				}
			} else {
				request.setAttribute("registerErrorMessage", "Passwords must match");
				request.getRequestDispatcher("index.jsp").forward(request, response);
			}

		} else {
			String username = request.getParameter("username");
			String password = request.getParameter("password");

			// if user entered a username
			if (username != null && username.trim() != "") {
				if (password != null && password.trim() != "") {
					if (checkLogin(username, password)) {
						session.setAttribute("user", username);
						session.setAttribute("name", getName(username));
						response.sendRedirect("BookServlet");
					} else {
						request.setAttribute("loginErrorMessage", "Incorrect username or password");
						request.getRequestDispatcher("index.jsp").forward(request, response);
					}
				} else {
					request.setAttribute("loginErrorMessages", "You must enter a password");
					request.getRequestDispatcher("index.jsp").forward(request, response);
				}
			} else {
				request.setAttribute("loginErrorMessage", "You must enter a username");
				request.getRequestDispatcher("index.jsp").forward(request, response);
			}
		}
	}

	@Override
	public void destroy() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String getName(String username) {
		try {
			PreparedStatement sql = conn
					.prepareStatement("SELECT firstName, lastName FROM cs485_project.accounts WHERE username = ?");
			sql.setString(1, username);
			ResultSet results = sql.executeQuery();
			if (results.next()) {
				return results.getString("firstName") + " " + results.getString("lastName");
			} else {
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if login credentials are valid
	 *
	 * @param username
	 * @param password
	 * @return a boolean
	 */
	private boolean checkLogin(String username, String password) {
		try {
			PreparedStatement sql = conn
					.prepareStatement("SELECT * FROM cs485_project.accounts WHERE username = ? AND password = ?");
			sql.setString(1, username);
			sql.setString(2, password);
			ResultSet results = sql.executeQuery();
			return results.next();
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
	private String addUser(String username, String password, String first, String last) {
		try {
			PreparedStatement sql = conn.prepareStatement("INSERT INTO cs485_project.accounts VALUES (?, ?, ?, ?)");
			sql.setString(1, username);
			sql.setString(2, password);
			sql.setString(3, first);
			sql.setString(4, last);
			sql.executeUpdate();
			return null;
		} catch (SQLIntegrityConstraintViolationException ex) {
			return "Username " + username + " is already taken.";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error, please try again.";
		}
	}
}