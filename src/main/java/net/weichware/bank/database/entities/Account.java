package net.weichware.bank.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.weichware.bank.base.Bootstrap;
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

    public Account(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("name");
        this.balance = resultSet.getDouble("balance");
    }

    public static Account get(String name) {
        try (
                Connection connection = Bootstrap.getDataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select * From account where name = ?")
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
                Connection connection = Bootstrap.getDataSource().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * From account")
        ) {
            while (resultSet.next()) {
                accounts.add(new Account(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get account list", e);
        }
        return accounts;
    }


}
