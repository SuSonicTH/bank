package net.weichware.bank.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static void init(DataSource dataSource) throws SQLException {
        try(
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
            statement.execute("create table USERS(name varchar(30), salt varchar(32), hash varchar(32))");
            statement.execute("insert into USERS(name, salt, hash) values ('mike', 'e46f28f21a68ad0e95462cc24d96817c', '024444e011d33f9759cc6b5babe3d373')"); //pw:test
        }
    }
}
