package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBentry {

	Connection dbconn;
	ResultSet results = null;
	PreparedStatement sql;
	StringBuilder sb = new StringBuilder();

	//change URL to your database server as needed
	String dbPath="jdbc:mysql://localhost:3306";

	public DBentry() {

	}

	//Establish connection to MySQL server
	private Connection newConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try {
				dbconn = DriverManager.getConnection(dbPath, "root", "admin");
				return dbconn;
			}
			catch (Exception s){
				s.printStackTrace();} //Changed to printStackTrace()
		}
		catch (Exception err){
			err.printStackTrace(); //Changed to printStackTrace()
		}
		return null;
	}

	public void close() {
		if (dbconn != null) {
			try {
				dbconn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkLogin(String username, String password) {
		try {
			dbconn = newConnection();
			sql = dbconn.prepareStatement("SELECT * FROM cs485_project.accounts WHERE username = \"" + username + "\" AND password = \"" + password + "\";");
			ResultSet results;
			results = sql.executeQuery();
			boolean hasMatch = results.next();
			return hasMatch;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean checkUser(String username) {
		try {
			dbconn = newConnection();
			sql = dbconn.prepareStatement("SELECT * FROM cs485_project.accounts WHERE username = \"" + username + "\";");
			ResultSet results;
			results = sql.executeQuery();
			boolean hasMatch = results.next();
			return hasMatch;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean addUser (String username, String password, String first, String last) {
		try {
			dbconn = newConnection();
			sql.executeUpdate("INSERT INTO cs485_project.accounts VALUES (\"" + username + "\", \"" + password + "\");");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}