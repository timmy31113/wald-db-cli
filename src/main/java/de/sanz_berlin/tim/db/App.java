package de.sanz_berlin.tim.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		Connection dbcon;
		Statement stmt;

		try {
			dbcon = DriverManager.getConnection("jdbc:postgresql://localhost/wald");
			stmt = dbcon.createStatement();
		} catch (SQLException e) {
			System.err.println("Can't connect to database: " + e);
			System.exit(1);
			return;
		}

		try {
			ResultSet rs = stmt.executeQuery("select * from schädigungen");
			while (rs.next()) {
				System.out.println("-------------------------------");
				System.out.println("Nummer: " + rs.getInt("nummer"));
				System.out.println("Art: " + rs.getString("art"));
				System.out.println("Datum: " + rs.getDate("datum"));
				System.out.println("Baum: " + rs.getInt("baum"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
