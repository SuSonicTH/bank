package net.weichware.bank.database.entities;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.weichware.bank.base.State;
import net.weichware.bank.database.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Accessors(makeFinal = true, fluent = true)
@ToString
public class Invoice {
    private static final Logger log = LoggerFactory.getLogger(Invoice.class);
    private final long id;
    private final String name;
    private final LocalDateTime bookingTime;
    private final double bookingValue;
    private final String displayName;

    private Invoice(ResultSet resultSet) throws SQLException {
        id = resultSet.getLong("id");
        name = resultSet.getString("name");
        displayName = name.substring(0, 1).toUpperCase() + name.substring(1);
        bookingTime = resultSet.getObject("booking_time", LocalDateTime.class);
        double value = 0.0;
        try {
            value = resultSet.getDouble("booking_value");
        } catch (SQLException e) {
        }
        bookingValue = value;
    }

    private static Invoice getLast(String name) {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select * From Invoice where name = ? order by booking_time desc")
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Invoice(resultSet);
                } else {
                    throw new DatabaseException("Could not get invoice for " + name);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get invoice for " + name, e);
        }
    }

    public static List<Invoice> getList() {
        List<Invoice> list = new ArrayList<>();
        try (
                Connection connection = State.dataSource().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select i.id, i.name, i.booking_time, i.balance + sum(t.BOOKING_VALUE) as booking_value " +
                        "From Invoice i, Transaction t " +
                        "where i.id = t.invoice " +
                        "group by i.id, i.name, i.booking_time " +
                        "order by i.booking_time desc")
        ) {
            resultSet.setFetchSize(100);
            while (resultSet.next()) {
                list.add(new Invoice(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get invoices", e);
        }
        return list;
    }

    public static List<Invoice> getList(String name) {
        List<Invoice> list = new ArrayList<>();
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select i.id, i.name, i.booking_time, i.balance + sum(t.BOOKING_VALUE) as booking_value " +
                        "From Invoice i, Transaction t " +
                        "where i.name = ? and i.id = t.invoice " +
                        "group by  i.id, i.name, i.booking_time " +
                        "order by i.booking_time desc")
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.setFetchSize(100);
                while (resultSet.next()) {
                    list.add(new Invoice(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get invoices for " + name, e);
        }
        return list;
    }

    public static void createInvoice(String name) {
        List<Transaction> openTransactions = Transaction.getOpenTransactions(name);
        Double value = openTransactions.stream().map(Transaction::bookingValue).reduce(Double::sum).or(() -> Optional.of(0.0)).get();
        if (value == 0.0) {
            return;
        }
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into Invoice (name, booking_time, balance) values (?, ?, ?)")
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setObject(2, LocalDateTime.now());
            preparedStatement.setObject(3, Account.get(name).balance());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Could not create invoice for " + name, e);
        }
        Invoice invoice = getLast(name);
        openTransactions.forEach(transaction -> transaction.setInvoice(invoice.id));

        Account.get(name).updateBalance(value);
    }
}
