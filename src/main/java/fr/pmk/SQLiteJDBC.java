package fr.pmk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    private void setup() {
        // TODO : CREATE TABLE IF NOT EXIST
    }

    /*
     * GETTER
     */

    public Connection getConnection() {
        return connection;
    }

}
