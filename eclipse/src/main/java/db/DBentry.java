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
			String[] awards, String lang, long isbn, String edition, int pages, String publisher, String date,
			String user, int total, int available, boolean isChecked, String bookId) {
		StringBuilder book = new StringBuilder();
		// open div
		book.append("<div class=\"bookBlock\">");

		// image
		book.append("<img alt=\"book cover\" width=\"250\" height=\"400\" align=\"left\" src=\"");
		book.append(img);
		book.append("\">");

		// main info
		book.append("<p>");
		book.append("<b>");
		book.append(title);
		book.append("</b>");
		book.append("<br> Written By: ");
		book.append(author);
		book.append("<br><br>");
		book.append(desc);

		// extra info
		book.append("<br>");

		book.append("<br>ISBN: ");
		if (isbn != 0 && isbn != 9999999999999L) {
			book.append(isbn);
		} else {
			book.append("N/A");
		}

		book.append("<br>Language: ");
		if (!lang.isBlank()) {
			book.append(lang);
		} else {
			book.append("N/A");
		}

		book.append("<br>Edition: ");
		if (!edition.isBlank()) {
			book.append(edition);
		} else {
			book.append("N/A");
		}

		book.append("<br>Pages: ");
		if (pages != 0) {
			book.append(pages);
		} else {
			book.append("N/A");
		}

		book.append("<br>Publisher: ");
		if (!publisher.isBlank()) {
			book.append(publisher);
		} else {
			book.append("N/A");
		}

		book.append("<br>Published Date: ");
		if (!date.isBlank()) {
			book.append(date);
		} else {
			book.append("N/A");
		}

		book.append("<br>Genres: ");
		if (genres != null) {
			book.append(genres.toString());
		} else {
			book.append("N/A");
		}

		book.append("<br>Awards: ");
		if (awards != null) {
			book.append(awards.toString());
		} else {
			book.append("N/A");
		}

		book.append("<br><br>");
		// only show if logged in
		if (user != null) {
			if (isChecked) {
				book.append("You currently have a copy of this book checked out.");
				book.append("<input type='button' value='Return' onclick='return(");
				book.append(user);
				book.append(", ");
				book.append(bookId);
				book.append(")'/>");
				book.append("<br>");
			} else {
				book.append(available);
				book.append(" of ");
				book.append(total);
				book.append(" copies are available");
				book.append("<br>");
				book.append("<input type='button' value='Checkout' onclick='checkout(");
				book.append(user);
				book.append(", ");
				book.append(bookId);
				book.append(")'/>");
				book.append("<br>");
			}
		} else {
			book.append("Want to know if this book is available? Login now!");
		}

		book.append("</p>");

		// close div
		book.append("</div>");
		return book.toString();
	}

	public static String printBooks(String user) {
		// TODO: combine queries?
		Connection dbconn = newConnection();
		Connection dbconn2 = newConnection();

		try {
			StringBuilder table = new StringBuilder();
			Statement sql = dbconn.createStatement();
			Statement sql2 = dbconn2.createStatement();

			ResultSet data = sql.executeQuery("SELECT * from cs485_project.books LIMIT " + BOOKS_PER_PAGE);
			while (data.next()) {
				int total = 0;
				int available = 0;
				boolean isChecked = false;
				// if there is a user logged in, get copy info
				if (user != null) {
					// combining these into 1 query would be great
					ResultSet totalCopies = sql2
							.executeQuery("SELECT COUNT(copyNumber) FROM cs485_project.inventory WHERE bookID = \""
									+ data.getString(1) + "\"");
					if (totalCopies.next()) {
						total = totalCopies.getInt(1);
					}
					;
					ResultSet checkedCopies = sql2
							.executeQuery("SELECT COUNT(copyNumber) FROM cs485_project.inventory WHERE bookID = \""
									+ data.getString(1) + "\" AND ISNULL(username)");
					if (checkedCopies.next()) {
						available = checkedCopies.getInt(1);
					}
					;
					ResultSet userCopies = sql2
							.executeQuery("SELECT COUNT(copyNumber) FROM cs485_project.inventory WHERE bookID = \""
									+ data.getString(1) + "\" AND username = \"" + user + "\"");
					if (userCopies.next()) {
						isChecked = userCopies.getInt(1) > 0 ? true : false;
					}
				}

				// get div
				table.append(bookDivGenerator(data.getString("coverImg"), data.getString("title"),
						data.getString("author"), data.getString("description"), null, null, data.getString("language"),
						data.getLong("isbn"), data.getString("edition"), data.getInt("pages"),
						data.getString("publisher"), data.getString("firstPublishDate"), user, total, available,
						isChecked, data.getString("bookId")));
			}
			return table.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error: " + ex.getMessage();
		}
	}
}