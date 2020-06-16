package de.sanz_berlin.tim.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Static helper class for database access
 * 
 * @author tim
 *
 */
public class Database {
	public static final String dburl = "jdbc:postgresql://localhost/wald";

	/**
	 * Opens a connection to the database.
	 * 
	 * @return the connection
	 * @throws SQLException when a connection can not be established
	 */
	public static Connection openConnection() throws SQLException {
		return DriverManager.getConnection(dburl);
	}
}
