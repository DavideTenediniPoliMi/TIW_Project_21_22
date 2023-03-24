package it.polimi.tiw.documents.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionHandler {
	public static Connection getConnection(ServletContext context) throws UnavailableException {
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");

			Class.forName(driver);

			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get database connection");
		}
	}

	public static void closeConnection(Connection connection) {
		if (connection == null) return;

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
