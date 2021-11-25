package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BookServlet extends HttpServlet {

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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		request.setAttribute("result", printBooks((String) session.getAttribute("user")));
		request.getRequestDispatcher("books.jsp").forward(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		String buttonPressed = request.getParameter("act");
		String user = (String) session.getAttribute("user");
		/*
		 * switch (buttonPressed) { case "Checkout": checkout(user); break; case
		 * "Return": }
		 */
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

	private String printBooks(String user) {
		try {
			StringBuilder books = new StringBuilder();
			Statement sql = conn.createStatement();
			// TODO: prepared statment
			ResultSet data = sql.executeQuery(
					"SELECT books.*, genres.genres, awards.awards, total, available, checked from cs485_project.books as books, \r\n"
							+ "(SELECT bookId, group_concat(distinct genre order by genre ASC SEPARATOR ', ') as genres FROM cs485_project.genreslist GROUP BY bookId) as genres, "
							+ "(SELECT bookId, group_concat(distinct award order by award ASC SEPARATOR ', ') as awards FROM cs485_project.awardslist GROUP BY bookId) as awards, "
							+ "(SELECT bookId, COUNT(*) AS Total, COUNT(IF(ISNULL(username), copyNumber, NULL)) as Available, IF(username='"
							+ user + "',TRUE,FALSE) as Checked from cs485_project.inventory group by bookId) as counts "
							+ "WHERE genres.bookId = books.bookId " + "AND awards.bookId = books.bookId "
							+ "AND counts.bookId = books.bookId LIMIT 20");
			while (data.next()) {
				books.append(bookDivGenerator(data.getString("coverImg"), data.getString("title"),
						data.getString("author"), data.getString("description"), data.getString("genres"),
						data.getString("awards"), data.getString("language"), data.getLong("isbn"),
						data.getString("edition"), data.getInt("pages"), data.getString("publisher"),
						data.getString("firstPublishDate"), user, data.getInt("total"), data.getInt("available"),
						data.getBoolean("checked"), data.getString("bookId")));
			}
			return books.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error: " + ex.getMessage();
		}
	}

	/**
	 * Generates a Div displaying the book information
	 */
	private String bookDivGenerator(String img, String title, String author, String desc, String genres, String awards,
			String lang, long isbn, String edition, int pages, String publisher, String date, String user, int total,
			int available, boolean isChecked, String bookId) {
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
		if (!genres.isBlank()) {
			book.append(genres);
		} else {
			book.append("N/A");
		}

		book.append("<br>Awards: ");
		if (!awards.isBlank()) {
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
				book.append("<input type='submit' name='act' value='Return' onclick='return(");
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
				if (available > 0) {
					book.append("<input type='submit' name='act' value='Checkout' onclick='checkout(");
				} else {
					book.append("<input type='submit' name='act' value='Join Waitlist' onclick='waitlist(");
				}
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

}