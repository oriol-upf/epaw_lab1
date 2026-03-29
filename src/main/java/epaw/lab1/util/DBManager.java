package epaw.lab1.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.stream.Collectors;

public class DBManager implements AutoCloseable {

	private Connection connection = null;
	private static final String DB_FILE = "lab1.db";

	public DBManager() throws Exception {
		// SQLite connection
		Class.forName("org.sqlite.JDBC");
		boolean dbExists = Files.exists(Paths.get(DB_FILE));
		connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);

		// Enable foreign keys in SQLite
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("PRAGMA foreign_keys = ON;");
		}

		if (!dbExists) {
			initDatabase();
		}
	}

	private void initDatabase() throws Exception {
		String schemaPath = "DB.txt";
		try (BufferedReader reader = new BufferedReader(new FileReader(schemaPath))) {
			String schema = reader.lines().collect(Collectors.joining("\n"));
			String[] statements = schema.split(";");
			try (Statement stmt = connection.createStatement()) {
				for (String sql : statements) {
					if (!sql.trim().isEmpty()) {
						stmt.execute(sql);
					}
				}
			}
		}
	}

	public PreparedStatement prepareStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}

	@Override
	public void close() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
}