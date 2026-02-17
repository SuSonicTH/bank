package net.weichware.bank.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class ConnectionPool {
    private final HikariDataSource dataSource;

    public ConnectionPool(String jdbcUrl, String userName, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(userName);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("minimumIdle", "3");
        config.addDataSourceProperty("maximumPoolSize", "50");

        dataSource = new HikariDataSource(config);
    }

    public DataSource dataSource() {
        return dataSource;
    }

}
