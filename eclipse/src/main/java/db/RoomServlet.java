package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RoomServlet extends HttpServlet {

	private Connection conn = null;
	private String dbPath = "jdbc:mysql://localhost:3306";
	private String dateForm = "<h2>Search Study Rooms</h2>"
			+ "<form action=\"RoomServlet\" method=\"get\">"
			+ "<p><label for=\"date\">Date:</label><input type=\"date\" id=\"date\" name=\"date\"></p>"
			+ "<p><label></label><input type=\"submit\" name=\"submit\" Value=\"View Rooms\"></p>"
			+ "</form>";

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
		
		// if not logged in, redirect to latest books
		if (session.getAttribute("user") == null) {
			response.sendRedirect("BookServlet");
			return;
		}

		// get date from form, or today if null
		String date = request.getParameter("date") != null ? (String) request.getParameter("date") : java.time.LocalDate.now().toString();

		// construct result
		StringBuilder responseHtml = new StringBuilder();
		responseHtml.append("<div class=\"resultColumn\"><h1>Study Rooms</h1>");
		responseHtml.append(dateForm);
		responseHtml.append("<h2>Showing Study Rooms for ");
		responseHtml.append(readableDate(date));
		responseHtml.append("</h2>");
		responseHtml.append(printRoom(110, "Test Room", "Floor 1"));
		responseHtml.append("</div>");
		
		// send info
		session.setAttribute("result",responseHtml.toString());
		response.sendRedirect("index.jsp");
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
	
	private String printRoom(int id, String name, String location) {
		StringBuilder room = new StringBuilder();
		room.append("<div class=\"roomInfo\">");
		room.append("<h3>");
		room.append(name);
		room.append(" ");
		room.append(id);
		room.append("</h3>");
		room.append("<i>");
		room.append(location);
		room.append("</i>");
		room.append("</div>");
		return room.toString();
	}
	
	/**
	 * readableDate
	 * @param oldDate - a date string in the format "yyyy-MM-dd"
	 * @return a string in the format "MMMM d yyyy", including a suffix on the day
	 * Example: 2021-11-27 => November 27th 2021
	 */
	private String readableDate(String oldDate) {
		// get suffix
		int n = Integer.parseInt(oldDate.substring(oldDate.length() - 2));
		String suffix;
		if (n >= 11 && n <= 13) {
	        suffix = "th";
	    }
	    switch (n % 10) {
	        case 1:  suffix = "st";
	        case 2:  suffix = "nd";
	        case 3:  suffix = "rd";
	        default: suffix = "th";
	    }
	    
	    LocalDate datetime = LocalDate.parse(oldDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    return datetime.format(DateTimeFormatter.ofPattern("MMMM d'" + suffix + "' yyyy"));
	}

}