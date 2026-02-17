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
import java.util.Optional;

@Getter
@Accessors(makeFinal = true, fluent = true)
@AllArgsConstructor
public class User {
    private final String name;
    private final String salt;
    private final String hash;
    private final boolean isAdmin;

    public User(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("name");
        this.salt = resultSet.getString("salt");
        this.hash = resultSet.getString("hash");
        this.isAdmin = resultSet.getString("admin").equals("Y");
    }

    public static Optional<User> get(String name) {
        try (
                Connection connection = State.dataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select * From users where name = ?")
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not get user by name " + name, e);
        }
        return Optional.empty();
    }
}
