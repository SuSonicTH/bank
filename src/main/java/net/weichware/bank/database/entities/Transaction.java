package net.weichware.bank.database.entities;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ToString
public class Transaction {
    private final String name;
    private final LocalDateTime bookingTime;
    private final String description;
    private final double bookingValue;
    private final LocalDate valueDate;
    private final String state;

    private Transaction(ResultSet resultSet) throws SQLException {
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
}