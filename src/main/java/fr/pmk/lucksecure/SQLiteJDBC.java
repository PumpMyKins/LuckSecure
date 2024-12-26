package fr.pmk.lucksecure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBC {

    private Connection connection;

    public SQLiteJDBC(String url) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection(url);
        MainLuckSecure.LOGGER.fine("Opened database successfully");

        setup();
    }

    public void close() throws SQLException {
        this.connection.close();
        MainLuckSecure.LOGGER.fine("Closed database successfully");
    }

    private void setup() throws SQLException {
        Statement stmt = this.connection.createStatement();
        try {
            final boolean execute = stmt.execute("CREATE TABLE IF NOT EXISTS tokens " +
            "(uuid INT PRIMARY KEY     NOT NULL," +
            " token          TEXT    NOT NULL)");
            MainLuckSecure.LOGGER.fine("Successfully setup database (" + execute + ")");
        } finally {
            stmt.close();
        }
    }

    /*
     * GETTER
     */

    public Connection getConnection() {
        return connection;
    }

}
