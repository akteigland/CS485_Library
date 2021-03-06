package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * RoomServlet
 * handles all study room related requests
 * @author Alissa Teigland
 */
public class RoomServlet extends HttpServlet {

	private Connection conn = null;
	private final String dbPath = "jdbc:mysql://localhost:3306";
	// Study rooms are offered every hour between MIN_HOUR and MAX_HOUR. Uses 24 hour time internally
	private final int MIN_HOUR = 8;
	private final int MAX_HOUR = 17; // 5pm
	// store the previous search
	private String previousDate = java.time.LocalDate.now().toString();

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
	/**
	 * Handles getting page results
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// set up
		response.setContentType("text/html");
		HttpSession session = request.getSession();

		// if not logged in, redirect to latest books
		String user = (String) session.getAttribute("user");
		if (user == null) {
			response.sendRedirect("BookServlet");
			return;
		}

		// get date from form, or today if null
		String date_s = request.getParameter("date");
		String date = date_s == null || date_s.isBlank() ? previousDate : date_s;
		previousDate = date;
		
		String dateForm = "<h2>Search Study Rooms</h2>" + "<form action=\"RoomServlet\" method=\"get\">"
				+ "<p><label for=\"date\">Date:</label><input type=\"date\" id=\"date\" name=\"date\"></p>"
				+ "<p><label></label><input type=\"submit\" name=\"submit\" Value=\"View Rooms\"></p>" + "</form>";

		// construct result
		StringBuilder responseHtml = new StringBuilder();
		responseHtml.append("<div class=\"resultColumn\"><h1>Study Rooms</h1>");
		responseHtml.append(dateForm);
		responseHtml.append("<h2>Showing Study Rooms for ");
		responseHtml.append(readableDate(date));
		responseHtml.append("</h2>");
		responseHtml.append(printAll(user, date));
		responseHtml.append("</div>");

		// send info
		session.setAttribute("result", responseHtml.toString());
		response.sendRedirect("index.jsp");
	}

	@Override
	/**
	 * Handles buttons
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		String buttonPressed = request.getParameter("act");
		String room = request.getParameter("room");
		String hour = request.getParameter("time");
		String user = (String) session.getAttribute("user");

		switch (buttonPressed.toLowerCase()) {
		case "cancel":
			cancelRoom(user, room, hour, previousDate);
			break;
		case "reserve":
			reserveRoom(user, room, hour, previousDate);
			break;
		default:
		}
		
		// load room page again
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
	 * Cancels a study room reservation
	 * @param user
	 * @param room
	 * @param hour
	 * @param date
	 */
	private void cancelRoom(String user, String room, String hour, String date) {
		try {
			PreparedStatement sql = conn.prepareStatement(
					"DELETE FROM cs485_project.reservedrooms WHERE roomId = ? AND roomHour = ? AND roomDate = STR_TO_DATE(?, '%Y-%m-%d') AND username = ?");
			sql.setString(1, room);
			sql.setString(2, hour);
			sql.setString(3, date);
			sql.setString(4, user);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reserves a study room
	 * @param user
	 * @param room
	 * @param hour
	 * @param date
	 */
	private void reserveRoom(String user, String room, String hour, String date) {
		try {
			PreparedStatement sql = conn.prepareStatement(
					"INSERT INTO cs485_project.reservedrooms VALUES(?,?,STR_TO_DATE(?, '%Y-%m-%d'),?)");
			sql.setString(1, room);
			sql.setString(2, hour);
			sql.setString(3, date);
			sql.setString(4, user);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a div containing all information for a study room
	 * @param id
	 * @param name
	 * @param location
	 * @param hoursUnavailable
	 * @param hoursReserved
	 * @return
	 */
	private String roomDivGenerator(int id, String name, String location, String[] hoursUnavailable,
			String[] hoursReserved) {
		StringBuilder room = new StringBuilder();
		room.append("<div class=\"roomBlock\">");
		room.append("<div class=\"roomInfo\">");
		room.append("<h3>");
		// Show name and id like "Study Room 100"
		room.append(name);
		room.append(" ");
		room.append(id);
		room.append("</h3>");
		room.append("<i>");
		room.append(location);
		room.append("</i>");
		room.append("</div>");
		
		// Create Table
		room.append("<table>");
		room.append("<tr><th>Timeslot</th><th>Status</th><th></th></tr>");
		for (int i = MIN_HOUR; i < MAX_HOUR; i++) {
			room.append("<tr>");

			// convert to 12 hour time
			String suffix = i < 12 ? "AM" : "PM";
			int hour = (i > 12) ? i - 12 : i; 
			
			// print timeslot
			room.append("<td class=\"timeslot\">");
			room.append(hour);
			room.append(":00");
			room.append(suffix);
			room.append(" - ");
			room.append(hour);
			room.append(":59");
			room.append(suffix);
			room.append("</td>");

			// print status + button
			room.append("<td>");
			if (arrayContains(hoursUnavailable, i) != -1) {
				room.append("Unavailable");
				room.append("</td>");
			} else if (arrayContains(hoursReserved, i) != -1) {
				room.append("Reserved");
				room.append("</td><td>");
				room.append("<form action=\"RoomServlet\" method=\"post\">");
				room.append("<input type='hidden' name='room' value='" + id + "'>");
				room.append("<input type='hidden' name='time' value='" + i + "'>");
				room.append("<input type='submit' name='act' value='Cancel'>");
				room.append("</form></td>");
			} else {
				room.append("Available");
				room.append("</td><td>");
				room.append("<form action=\"RoomServlet\" method=\"post\">");
				room.append("<input type='hidden' name='room' value='" + id + "'>");
				room.append("<input type='hidden' name='time' value='" + i + "'>");
				room.append("<input type='submit' name='act' value='Reserve'>");
				room.append("</form></td>");
			}
			room.append("</tr>");

		}
		room.append("</table>");
		room.append("</div>");
		return room.toString();
	}

	/**
	 * Prints all study rooms
	 * @param user - the user logged in
	 * @param date - the date being viewed
	 * @return a String containing HTML
	 */
	private String printAll(String user, String date) {
		try {
			StringBuilder rooms = new StringBuilder();
			PreparedStatement sql = conn
					.prepareStatement("SELECT rooms.*, unavailable, reserved FROM cs485_project.studyrooms as rooms "
							+ "LEFT JOIN (SELECT roomId, group_concat(roomHour) as unavailable FROM cs485_project.reservedrooms WHERE NOT ISNULL(username) AND NOT username = ? AND roomDate = ? GROUP BY roomId) as una "
							+ "ON una.roomId = rooms.roomId "
							+ "LEFT JOIN (SELECT roomId, group_concat(roomHour) as reserved FROM cs485_project.reservedrooms WHERE username = ? AND roomDate = ? GROUP BY roomId) as res "
							+ "on res.roomId = rooms.roomId");
			sql.setString(1, user);
			sql.setString(2, date);
			sql.setString(3, user);
			sql.setString(4, date);
			ResultSet data = sql.executeQuery();
			while (data.next()) {
				// getArray doesn't seem to work with mySQL, so convert a String into String[]
				String unavailable = data.getString("unavailable");
				String[] una = (unavailable != null) ? unavailable.split(",") : null;
				String reserved = data.getString("reserved");
				String[] res = (reserved != null) ? reserved.split(",") : null;
				rooms.append(roomDivGenerator(data.getInt("roomId"), data.getString("roomName"),
						data.getString("roomLocation"), una, res));
			}
			return rooms.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Unknown error: " + ex.getMessage();
		}
	}

	/**
	 * readableDate
	 * 
	 * @param oldDate - a date string in the format "yyyy-MM-dd"
	 * @return a string in the format "MMMM d yyyy", including a suffix on the day
	 *         Example: 2021-11-27 => November 27th 2021
	 */
	private String readableDate(String oldDate) {
		// get suffix
		int n = Integer.parseInt(oldDate.substring(oldDate.length() - 2));
		String suffix;

		switch (n % 10) {
		case 1:
			suffix = "st";
			break;
		case 2:
			suffix = "nd";
			break;
		case 3:
			suffix = "rd";
			break;
		default:
			suffix = "th";
		}
		if (n >= 11 && n <= 13) {
			suffix = "th";
		}

		LocalDate datetime = LocalDate.parse(oldDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return datetime.format(DateTimeFormatter.ofPattern("MMMM d'" + suffix + "' yyyy"));
	}

	/**
	 * Specifically searches String arrays for a specific int
	 * @param arr - the array to search in
	 * @param key - the int to search for
	 * @return the index of key or -1 if not found
	 */
	private int arrayContains(String[] arr, int key) {
		if (arr == null) {
			return -1;
		}
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(Integer.toString(key))) {
				return i;
			}
		}
		return -1;
	}

}