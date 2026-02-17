package net.weichware.bank.base;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import net.weichware.bank.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebListener
public class Bootstrap implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean isNew = !Files.exists(Paths.get("./bank.mv.db"));

        State.init(new StateConfig() {
            @Override
            public String jdbcUrl() {
                return "jdbc:h2:./bank;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH";
            }

            @Override
            public String userName() {
                return null;
            }

            @Override
            public String password() {
                return null;
            }
        });

        if (isNew) {
            try {
                Database.init(State.dataSource());
            } catch (SQLException e) {
                log.error("Cannot init database", e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
