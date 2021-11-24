package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class DBentry {

	static String dbPath = "jdbc:mysql://localhost:3306";
	static final int BOOKS_PER_PAGE = 10;

	/**
	 * Establishes a connection to the MySQL server
	 * 
	 * @return a connection or null on error
	 */
	private static Connection newConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection dbconn = DriverManager.getConnection(dbPath, "root", "admin");
			return dbconn;
		} catch (Exception err) {
			err.printStackTrace(); // Changed to printStackTrace()
		}
		return null;
	}

	/**
	 * Checks if login credentials are valid
	 * 
	 * @param username
	 * @param password
	 * @return a boolean
	 */
	public static boolean checkLogin(String username, String password) {
		try {
			Connection dbconn = newConnection();
			PreparedStatement sql = dbconn.prepareStatement("SELECT * FROM cs485_project.accounts WHERE username = \""
					+ username + "\" AND password = \"" + password + "\";");
			ResultSet results;
			results = sql.executeQuery();
			boolean hasMatch = results.next();
			dbconn.close();
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
	public static String addUser(String username, String password, String first, String last) {
		Connection dbconn = newConnection();
		try {
			Statement sql = dbconn.createStatement();
			sql.executeUpdate("INSERT INTO cs485_project.accounts VALUES (\"" + username + "\", \"" + password
					+ "\", \"" + first + "\", \"" + last + "\");");
			dbconn.close();
			return null;
		} catch (SQLIntegrityConstraintViolationException ex) {
			return "Username " + username + " is already taken.";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error, please try again.";
		}
	}

	private static String bookDivGenerator(String img, String title, String author, String desc, String[] genres,
			String[] awards, String lang, int isbn, String edition, int pages, String publisher, String date,
			String user) {
		StringBuilder book = new StringBuilder();
		// open div
		book.append("<div class=\"bookBlock\">");

		// image
		book.append("<img alt=\"book cover\" width=\"200\" height=\"320\" align=\"left\" src=\"");
		book.append(img);
		book.append("\">");

		// close div
		book.append("</div>");
		return book.toString();
	}

	public static String printBooks() {
		Connection dbconn = newConnection();
		try {
			StringBuilder table = new StringBuilder();
			Statement sql = dbconn.createStatement();
			ResultSet data = sql.executeQuery("SELECT * from cs485_project.books LIMIT " + BOOKS_PER_PAGE);
			while (data.next()) {
				table.append(bookDivGenerator(data.getString("coverImg"),data.getString("title"),data.getString("author"), dbPath, null, null, dbPath, 0, dbPath, 0, dbPath, dbPath, dbPath));
			}
			return table.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error: " + ex.getMessage();
		}
	}
}