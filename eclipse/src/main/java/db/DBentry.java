package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBentry {

	static String dbPath = "jdbc:mysql://localhost:3306";

	/**
	 * Establishes a connection to the MySQL server
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
	 * Checks if a username is already in the database
	 * @param username
	 * @return a boolean
	 */
	public static boolean checkUser(String username) {
		try {
			Connection dbconn = newConnection();
			PreparedStatement sql = dbconn
					.prepareStatement("SELECT * FROM cs485_project.accounts WHERE username = \"" + username + "\";");
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
	 * @param username
	 * @param password
	 * @param first - first name of the user
	 * @param last - last name of the user
	 * @return a boolean (true on success, false on failure)
	 */
	public static boolean addUser(String username, String password, String first, String last) {
		Connection dbconn = newConnection();
		try {
			Statement sql = dbconn.createStatement();
			sql.executeUpdate(
					"INSERT INTO cs485_project.accounts VALUES (\"" + username + "\", \"" + password + "\");");
			dbconn.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}