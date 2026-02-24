package net.weichware.bank.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.weichware.bank.base.State;
import net.weichware.bank.database.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(makeFinal = true, fluent = true)
@AllArgsConstructor
public class Account {
    private final String name;
    private final double balance;
    private final double openBalance;
    private final String displayName;

    public Account(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("name");
        this.displayName = name.substring(0, 1).toUpperCase() + name.substring(1);
        this.balance = resultSet.getDouble("balance");
        this.openBalance = resultSet.getDouble("open_balance");
    }

    public static Account get(String name) {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select name, balance, balance + (select sum(t.BOOKING_VALUE) from transaction t where t.name = a.name and t.STATE = 'open') as open_balance  From account a where name = ?")
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Account(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get account by name " + name, e);
        }
        throw new DatabaseException("Account " + name + " not found");
    }

    public static List<Account> getList() {
        List<Account> accounts = new ArrayList<>();
        try (
                Connection connection = State.dataSource().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select name, balance, balance +(select sum(t.BOOKING_VALUE) from transaction t where t.name = a.name and t.state = 'open') as open_balance  From account a")
        ) {
            while (resultSet.next()) {
                accounts.add(new Account(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get account list", e);
        }
        return accounts;
    }

    public void updateBalance(Double value) {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("update account set balance = ? where name = ?")
        ) {
            double v = value - Math.abs(balance);
            preparedStatement.setDouble(1, v < 0 ? v : 0.0);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update account balance for " + name + " with " + value, e);
        }
    }
}
