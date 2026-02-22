package net.weichware.bank.database.entities;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.weichware.bank.base.State;
import net.weichware.bank.database.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(makeFinal = true, fluent = true)
@ToString
public class Transaction {
    private final long id;
    private final String name;
    private final LocalDateTime bookingTime;
    private final String description;
    private final double bookingValue;
    private final LocalDate valueDate;
    private final String state;

    public Transaction(String name, LocalDateTime bookingTime, String description, double bookingValue, LocalDate valueDate, String state) {
        this.id = 0;
        this.name = name;
        this.bookingTime = bookingTime;
        this.description = description;
        this.bookingValue = bookingValue;
        this.valueDate = valueDate;
        this.state = state;
    }

    private Transaction(ResultSet resultSet) throws SQLException {
        id = resultSet.getLong("id");
        name = resultSet.getString("name");
        bookingTime = resultSet.getObject("booking_time", LocalDateTime.class);
        description = resultSet.getString("description");
        bookingValue = resultSet.getDouble("booking_value");
        valueDate = resultSet.getObject("value_date", LocalDate.class);
        state = resultSet.getString("state");
    }

    public static List<Transaction> getOpenTransactions() {
        List<Transaction> openTransactions = new ArrayList<>();
        try (
                Connection connection = State.dataSource().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * From TRANSACTION where state = 'open' order by booking_time desc")
        ) {
            while (resultSet.next()) {
                openTransactions.add(new Transaction(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get open transactions", e);
        }
        return openTransactions;
    }

    public static List<Transaction> getOpenTransactions(String name) {
        List<Transaction> openTransactions = new ArrayList<>();
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select * From TRANSACTION where state = 'open' and name = ? order by booking_time desc")
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    openTransactions.add(new Transaction(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get open transactions", e);
        }
        return openTransactions;
    }

    public void save() {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into TRANSACTION (name, booking_time, description, booking_value, value_date, state) values (?,?,?,?,?,?)")
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setObject(2, bookingTime);
            preparedStatement.setString(3, description);
            preparedStatement.setDouble(4, bookingValue);
            preparedStatement.setObject(5, valueDate);
            preparedStatement.setString(6, state);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not save transaction " + this, e);
        }
    }

    public void delete() {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("delete from TRANSACTION where id = ?")
        ) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete transaction " + this, e);
        }
    }

    public void update(String account, String description, Double value, LocalDate valueDate) {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("update TRANSACTION set name = ?, description = ? , booking_value = ?, value_date = ? where id = ?")
        ) {
            preparedStatement.setString(1, account);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, value);
            preparedStatement.setObject(4, valueDate);

            preparedStatement.setLong(5, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update transaction " + this, e);
        }
    }

    public void setInvoice(long invoice) {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("update TRANSACTION set invoice = ?, state = 'closed' where id = ?")
        ) {
            preparedStatement.setLong(1, invoice);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not set invoice " + invoice + " for  transaction " + this, e);
        }
    }
}