package fr.pmk.lucksecure.common.database;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class LuckSecureDatabase {

    private JdbcPooledConnectionSource connection;
    private Dao<UserToken, UUID> totpDao; 
    private Logger logger;

    public LuckSecureDatabase(Logger logger, String url) throws SQLException {
        this.logger = logger;

        this.connection = new JdbcPooledConnectionSource(url);
        this.logger.info("Opened database successfully");

        this.totpDao = DaoManager.createDao(this.connection, UserToken.class);

        setup();
    }

    public void close() throws Exception {
        this.connection.close();
        this.logger.info("Closed database successfully");
    }

    private void setup() throws SQLException {
        this.logger.info("Initialize database.");
        TableUtils.createTableIfNotExists(this.connection, UserToken.class);
    }

    /*
     * GETTER
     */

    public JdbcPooledConnectionSource getConnection() {
        return connection;
    }

    public Dao<UserToken, UUID> getTotpDao() {
        return totpDao;
    }

}
