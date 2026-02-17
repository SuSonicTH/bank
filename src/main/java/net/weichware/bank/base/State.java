package net.weichware.bank.base;

import net.weichware.bank.database.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class State {
    private static final Logger log = LoggerFactory.getLogger(State.class);
    private static ConnectionPool connectionPool;

    public static void init(StateConfig config) {
        connectionPool = new ConnectionPool(config.jdbcUrl(), config.userName(), config.password());
    }

    public static DataSource dataSource() {
        return connectionPool.dataSource();
    }

}
