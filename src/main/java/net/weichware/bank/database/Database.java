package net.weichware.bank.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static void init(DataSource dataSource) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute("create table USERS(name varchar(30), salt varchar(32), hash varchar(32), admin char default 'N')");
            statement.execute("insert into USERS(name, salt, hash, admin) values ('mike', 'e46f28f21a68ad0e95462cc24d96817c', '024444e011d33f9759cc6b5babe3d373', 'Y')"); //pw:test
            statement.execute("insert into USERS(name, salt, hash) values ('christoph', '13ba68aa601f392f34c9f0e25387d99f', 'efc38ddf4f4c208e182f5d98855c3dcf')"); //pw:dimnpw
            statement.execute("insert into USERS(name, salt, hash) values ('katharina', '13ba68aa601f392f34c9f0e25387d99f', 'efc38ddf4f4c208e182f5d98855c3dcf')"); //pw:dimnpw
            statement.execute("insert into USERS(name, salt, hash) values ('maximilian', '13ba68aa601f392f34c9f0e25387d99f', 'efc38ddf4f4c208e182f5d98855c3dcf')"); //pw:dimnpw
            statement.execute("create table ACCOUNT(name varchar(30), balance numeric(4,2))");
            statement.execute("insert into ACCOUNT(name, balance) values ('christoph', 0.0)");
            statement.execute("insert into ACCOUNT(name, balance) values ('katharina', 1.96)");
            statement.execute("insert into ACCOUNT(name, balance) values ('maximilian', -20)");
            statement.execute("create table TRANSACTION(name varchar(30), booking_time timestamp, description varchar(50), booking_value numeric(4,2),  value_date date, state varchar(20))");

            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('katharina',to_date('202512101447', 'yyyymmddhhmi'), 'Billa', 25, to_date('20251210', 'yyyymmdd'), 'open')");
            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('katharina',to_date('202601291626', 'yyyymmddhhmi'), 'Vorschuss', -10, to_date('20260129', 'yyyymmdd'), 'open')");

            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('maximilian',to_date('202512151612', 'yyyymmddhhmi'), 'Kebab Vorschuss', -50, to_date('20251215', 'yyyymmdd'), 'open')");
            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('maximilian',to_date('202512151659', 'yyyymmddhhmi'), 'Kebab', 54.70, to_date('20251215', 'yyyymmdd'), 'open')");
            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('maximilian',to_date('202512181450', 'yyyymmddhhmi'), 'Billa', 20.54, to_date('20251212', 'yyyymmdd'), 'open')");
            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('maximilian',to_date('202512181450', 'yyyymmddhhmi'), 'Lidl', 4.53, to_date('20251218', 'yyyymmdd'), 'open')");
            statement.execute("insert into TRANSACTION(name, booking_time, description, booking_value, value_date, state) values ('maximilian',to_date('202601291322', 'yyyymmddhhmi'), 'Billa', 26.65, to_date('20260129', 'yyyymmdd'), 'open')");
        }
    }
}
