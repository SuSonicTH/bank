package net.weichware.bank.base;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import net.weichware.bank.database.ConnectionPool;
import net.weichware.bank.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * The {@link #contextInitialized(ServletContextEvent)} is called once,
 * before the Vaadin app receives any requests. The {@link #contextDestroyed(ServletContextEvent)}
 * is called exactly once when the app is shutting down.
 */
@WebListener
public class Bootstrap implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    private static String JDBC_URL = "jdbc:h2:./bank;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH";
    private static ConnectionPool connectionPool;

    public static void setJdbcUrl(String jdbcUrl) {
        JDBC_URL = jdbcUrl;
    }

    public static DataSource getDataSource() {
        return connectionPool.dataSource();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean isNew = !Files.exists(Paths.get("./bank.mv.db"));
        connectionPool = new ConnectionPool(JDBC_URL, null, null);
        if (isNew) {
            try {
                Database.init(connectionPool.dataSource());
            } catch (SQLException e) {
                log.error("Cannot init connection pool", e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // to be implemented by actual apps.
        // possibly stop & kill background task executor, or cleanup temp files, or such.
    }
}
