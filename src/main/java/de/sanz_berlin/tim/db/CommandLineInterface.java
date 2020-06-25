package de.sanz_berlin.tim.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import de.sanz_berlin.tim.db.model.SchädigungsArt;

/**
 * Provides the user with an interface to the application on the command line.
 * 
 * @author tim
 *
 */
public class CommandLineInterface {
	private final Scanner scanner;

	/**
	 * Constructs a new CommandLineInterface. Call {@link #start()} to start the
	 * user interaction.
	 */
	public CommandLineInterface() {
		scanner = new Scanner(System.in);
	}

	/**
	 * Starts the user interaction. This method completes when the user chooses to
	 * quit the program. Do not call this twice! Construct a new instance instead.
	 */
	public void start() {
		System.out.println("Kommandozeileninterface für die Verwaltung der Schädigungen");
		loop();
		scanner.close();
		System.out.println("Programm beendet.");
	}

	private void loop() {
		boolean wantsToQuit = false;
		while (!wantsToQuit) {
			printFeatureList();
			wantsToQuit = handleFeatureInput();
		}
	}

	private void printFeatureList() {
		System.out.println("Wähle eine Funktion aus:");
		System.out.println("1. Die ganze Tabelle ausgeben");
		System.out.println("2. Einen neuen Eintrag eingeben");
		System.out.println("3. Einen Eintrag löschen");
		System.out.println("4. Navigieren durch die Tabelle");
		System.out.println("5. Beenden");
	}

	private boolean handleFeatureInput() {
		System.out.print("> ");
		switch (scanner.nextLine()) {
		case "1":
			feature1();
			break;
		case "2":
			feature2();
			break;
		case "3":
			feature3();
			break;
		case "4":
			feature4();
			break;
		case "5":
			return true;
		default:
			System.out.println("Bitte gib die Nummer der Funktion ein");
		}
		return false;
	}

	private void feature1() {
		System.out.println("Ausgabe der ganzen Tabelle:");
		try {
			final Connection dbcon = Database.openConnection();
			final Statement stmt = dbcon.createStatement();
			final ResultSet rs = stmt.executeQuery("select * from schädigungen");
			while (rs.next()) {
				System.out.println("-------------------------------");
				System.out.println("Nummer: " + rs.getInt("nummer"));
				System.out.println("Art: " + rs.getString("art"));
				System.out.println("Datum: " + rs.getDate("datum"));
				System.out.println("Baum: " + rs.getInt("baum"));
			}
			rs.close();
			stmt.close();
			dbcon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("-------------------------------");
		System.out.println("Ende der Tabelle");
	}

	private void feature2() {
		System.out.println("Eingabe eines neuen Eintrags:");
		SchädigungsArt damageType = null;
		while (damageType == null) {
			System.out.println("Gib die Art der Schädigung an (insekt, wild, pilz, sturm, dürre oder brand):");
			System.out.print("> ");
			final String input = scanner.nextLine().toLowerCase();
			switch (input) {
			case "insekt":
				damageType = SchädigungsArt.INSEKT;
				break;
			case "wild":
				damageType = SchädigungsArt.WILD;
				break;
			case "pilz":
				damageType = SchädigungsArt.PILZ;
				break;
			case "sturm":
				damageType = SchädigungsArt.STURM;
				break;
			case "dürre":
				damageType = SchädigungsArt.DÜRRE;
				break;
			case "brand":
				damageType = SchädigungsArt.BRAND;
				break;
			default:
				System.out.println(
						"Die Eingabe war nicht eindeutig, bitte nur eine von den vorgebenen Schädigungsarten angeben");
				continue;
			}
		}
		LocalDate date = null;
		while (date == null) {
			System.out.println("Gib das Datum der Schädigung an (ISO-Format JJJJ-MM-TT):");
			System.out.print("> ");
			final String input = scanner.nextLine();
			try {
				date = LocalDate.parse(input);
			} catch (DateTimeParseException e) {
				System.out.println("Bitte nutze das ISO-8601-Datumsformat, heute ist z.B.: \""
						+ LocalDate.now().toString() + "\"");
				continue;
			}
		}
		try {
			final Connection dbcon = Database.openConnection();
			final Statement stmt = dbcon.createStatement();
			int treeNumber = -1;
			ResultSet rs;
			while (true) {
				System.out.println("Gib die Nummer des geschädigten Baumes an:");
				System.out.print("> ");
				try {
					treeNumber = Integer.parseInt(scanner.nextLine());
				} catch (NumberFormatException e) {
					System.out.println("Die Baumnummer muss eine Nummer sein!");
					continue;
				}
				rs = stmt.executeQuery("select count(nummer) > 0 from bäume where nummer = " + treeNumber);
				rs.next();
				if (rs.getBoolean(1)) {
					break;
				}
				System.out.println("Ein Baum mit dieser Nummer existiert leider nicht.");
				System.out.println("Meintest du eine der folgenden Baumnummern?");
				rs = stmt.executeQuery("select nummer from bäume");
				while (rs.next()) {
					System.out.print(rs.getInt(1) + " ");
				}
				System.out.println();
			}

			final String update = String.format(
					"insert into schädigungen (art, datum, baum) values ('%s', date '%s', %s)", damageType.getValue(),
					date.toString(), treeNumber);
			stmt.executeUpdate(update);

			rs.close();
			stmt.close();
			dbcon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neue Schädigung wurde eingetragen");
	}

	private void feature3() {
		int damageNumber = -1;
		while (damageNumber < 0) {
			System.out.println("Gib die Nummer der zu löschenden Schädigung ein:");
			System.out.print("> ");
			try {
				damageNumber = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Die Schädigungsnummer muss eine Nummer sein!");
				continue;
			}
		}
		boolean deleted = false;
		try {
			final Connection dbcon = Database.openConnection();
			final Statement stmt = dbcon.createStatement();
			deleted = stmt.executeUpdate("delete from schädigungen where nummer = " + damageNumber) > 0;
			stmt.close();
			dbcon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (deleted) {
			System.out.println("Eintrag gelöscht.");
		} else {
			System.out.println("Kein Eintrag mit der Nummer " + damageNumber + " gefunden – nichts gelöscht");
		}
	}

	private void feature4() {
		System.out.println("Navigation durch die Einträge:");
		try {
			final Connection dbcon = Database.openConnection();
			final Statement stmt = dbcon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = stmt.executeQuery("select * from schädigungen");
			schleife: while (true) {
				System.out.println("Navigation: p (previous), n (next), q (quit)");
				System.out.print("> ");
				final String input = scanner.nextLine().toLowerCase();
				switch (input) {
				case "n":
					if (rs.isLast()) {
						rs.beforeFirst();
						System.out.println("Ende erreicht, springe zum Anfang…");
					}
					rs.next();
					break;
				case "p":
				case "v":
					if (rs.isFirst() || rs.isBeforeFirst()) {
						rs.afterLast();
						System.out.println("Vorderstes Element, springe zum Ende…");
					}
					rs.previous();
					break;
				case "q":
					break schleife;
				default:
					System.out.println("Befehl nicht eindeutig");
					continue;
				}

				System.out.println("-------------------------------");
				System.out.println("Nummer: " + rs.getInt("nummer"));
				System.out.println("Art: " + rs.getString("art"));
				System.out.println("Datum: " + rs.getDate("datum"));
				System.out.println("Baum: " + rs.getInt("baum"));
			}
			rs.close();
			stmt.close();
			dbcon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
