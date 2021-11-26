package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BookServlet extends HttpServlet {

	private Connection conn = null;
	private static String dbPath = "jdbc:mysql://localhost:3306";
	private static final int MAX_BOOKS = 15;

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
		// set up
		response.setContentType("text/html");
		HttpSession session = request.getSession();

		// get search
		String author = "%", title = "%", genre = "%", award = "%", language = "%";
		Boolean searched = request.getParameter("books") != null && request.getParameter("books").equals("Search");
		if (searched) {
			author = request.getParameter("author") == null ? "%" : "%" + request.getParameter("author") + "%";
			title = request.getParameter("title") == null ? "%" : "%" + request.getParameter("title") + "%";
			genre = request.getParameter("genre") == null ? "%" : "%" + request.getParameter("genre") + "%";
			award = request.getParameter("award") == null ? "%" : "%" + request.getParameter("award") + "%";
			language = request.getParameter("lang") == null ? "%" : "%" + request.getParameter("lang") + "%";
		}

		// send info
		String books = printBooks((String) session.getAttribute("user"), author, title, genre, award, language);
		session.setAttribute("result", "<div class=\"resultColumn\"><h1>Books</h1>" + books + "</div>");
		response.sendRedirect("index.jsp");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		String buttonPressed = request.getParameter("act");
		String book = request.getParameter("book");
		String user = (String) session.getAttribute("user");

		switch (buttonPressed.toLowerCase()) {
		case "checkout":
			checkoutBook(user, book);
			break;
		case "return":
			returnBook(user, book);
			break;
		case "join waiting list":
			waitForBook(user, book);
			break;
		case "leave waiting list":
			leaveWaitForBook(user, book);
			break;
		default:
		}

		// update book list
		doGet(request, response);
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

	/**
	 * checks out a book
	 */
	private void checkoutBook(String user, String bookId) {
		try {
			PreparedStatement sql = conn.prepareStatement(
					"UPDATE cs485_project.inventory SET username = ? WHERE bookId = ? AND copyNumber = (SELECT copyNumber FROM (SELECT * FROM cs485_project.inventory) AS temp WHERE bookId = ? AND ISNULL(username) LIMIT 1)");
			sql.setString(1, user);
			sql.setString(2, bookId);
			sql.setString(3, bookId);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns a users book
	 */
	private void returnBook(String user, String bookId) {
		try {
			PreparedStatement sql = conn.prepareStatement(
					"UPDATE cs485_project.inventory SET username = null WHERE bookId = ? AND username = ?");
			sql.setString(1, bookId);
			sql.setString(2, user);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds a user to a books waiting list
	 */
	private void waitForBook(String user, String bookId) {
		try {
			PreparedStatement sql = conn.prepareStatement("INSERT INTO cs485_project.waitinglist VALUES (?, ?)");
			sql.setString(1, bookId);
			sql.setString(2, user);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes the user from the books waiting list
	 */
	private void leaveWaitForBook(String user, String bookId) {
		try {
			PreparedStatement sql = conn
					.prepareStatement("DELETE FROM cs485_project.waitinglist WHERE bookId = ? AND username = ?");
			sql.setString(1, bookId);
			sql.setString(2, user);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the book results
	 */
	private String printBooks(String user, String author, String title, String genre, String award, String language) {
		try {
			StringBuilder books = new StringBuilder();
			PreparedStatement sql = conn.prepareStatement(
					"SELECT books.*, genres.genres, awards.awards, total, available, checked, IFNULL(waiting,0) as waitingFor from cs485_project.books "
							+ "LEFT JOIN (SELECT bookId, group_concat(distinct genre order by genre ASC SEPARATOR ', ') as genres FROM cs485_project.genreslist GROUP BY bookId) as genres "
							+ "on genres.bookId = books.bookId "
							+ "LEFT JOIN (SELECT bookId, group_concat(distinct award order by award ASC SEPARATOR ', ') as awards FROM cs485_project.awardslist GROUP BY bookId) as awards "
							+ "on awards.bookId = books.bookId "
							+ "LEFT JOIN (SELECT bookId, COUNT(*) AS Total, COUNT(IF(ISNULL(username), copyNumber, NULL)) as Available, COUNT(IF(username=?"
							+ ",1,NULL)) as Checked from cs485_project.inventory group by bookId) as counts "
							+ "on counts.bookId = books.bookId " + "LEFT JOIN (SELECT bookId, IF(username=?"
							+ ", TRUE, FALSE) as waiting from cs485_project.waitinglist) as waiting "
							+ "on waiting.bookID = books.bookId WHERE IFNULL(title,'') LIKE ? AND IFNULL(author,'') LIKE ? and IFNULL(genres,'') LIKE ? and IFNULL(awards,'') LIKE ? and IFNULL(language,'') LIKE ?"
							+ " ORDER BY firstPublishDate DESC LIMIT ? ");
			sql.setString(1, user);
			sql.setString(2, user);
			sql.setString(3, title);
			sql.setString(4, author);
			sql.setString(5, genre);
			sql.setString(6, award);
			sql.setString(7, language);
			sql.setInt(8, MAX_BOOKS);
			ResultSet data = sql.executeQuery();
			if (data.next() == false) {
				books.append("<p><b>No results.</b></p>");
			} else {
				while (data.next()) {
					books.append(bookDivGenerator(data.getString("coverImg"), data.getString("title"),
							data.getString("author"), data.getString("description"), data.getString("genres"),
							data.getString("awards"), data.getString("language"), data.getLong("isbn"),
							data.getString("edition"), data.getInt("pages"), data.getString("publisher"),
							data.getString("firstPublishDate"), user, data.getInt("total"), data.getInt("available"),
							data.getBoolean("checked"), data.getBoolean("waitingFor"), data.getString("bookId")));
				}
			}
			return books.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error: " + ex.getMessage();
		}
	}

	/**
	 * Generates a div displaying a books information
	 */
	private String bookDivGenerator(String img, String title, String author, String desc, String genres, String awards,
			String lang, long isbn, String edition, int pages, String publisher, String date, String user, int total,
			int available, boolean isChecked, boolean isWaitingFor, String bookId) {
		StringBuilder book = new StringBuilder();
		// open div
		book.append("<div class=\"bookBlock\">");

		// image
		book.append("<img alt=\"book cover\" width=\"250\" height=\"400\" align=\"left\" src=\"");
		book.append(img);
		book.append("\">");

		// main info
		book.append("<div class=\"bookInfo\">");
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
		if (date != null && !date.isBlank()) {
			book.append(date);
		} else {
			book.append("N/A");
		}

		book.append("<br>Genres: ");
		if (genres != null && !genres.isBlank()) {
			book.append(genres);
		} else {
			book.append("N/A");
		}

		book.append("<br>Awards: ");
		if (awards != null && !awards.isBlank()) {
			book.append(awards);
		} else {
			book.append("N/A");
		}

		book.append("<br><br>");
		// only show if logged in
		if (user != null) {
			if (isChecked) {
				book.append("You currently have a copy of this book checked out.");
				book.append("<br>");
				book.append("<form action=\"BookServlet\" method=\"post\">");
				book.append("<input type='hidden' name='book' value='" + bookId + "'>");
				book.append("<input type='submit' name='act' value='Return'>");
				book.append("</form><br>");
			} else if (isWaitingFor) {
				book.append("You are currently on the waiting list for this book.");
				book.append("<br>");
				book.append("<form action=\"BookServlet\" method=\"post\">");
				book.append("<input type='hidden' name='book' value='" + bookId + "'>");
				book.append("<input type='submit' name='act' value='Leave Waiting List'>");
				book.append("</form><br>");
			} else {
				book.append(available);
				book.append(" of ");
				book.append(total);
				book.append(" copies are available");
				book.append("<br>");
				book.append("<form action=\"BookServlet\" method=\"post\">");
				book.append("<input type='hidden' name='book' value='" + bookId + "'>");
				if (available > 0) {
					book.append("<input type='submit' name='act' value='Checkout'>");
				} else {
					book.append("<input type='submit' name='act' value='Join Waiting list'>");
				}
				book.append("</form><br>");
			}
		} else {
			book.append("Want to know if this book is available? Login now!");
		}

		book.append("</div>");

		// close div
		book.append("</div>");
		return book.toString();
	}

}