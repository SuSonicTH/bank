package net.weichware.bank.database.entities;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.weichware.bank.base.Bootstrap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Getter
@Accessors(makeFinal = true, fluent = true)
public class User {
    private final String name;
    private final String salt;
    private final String hash;

    public User(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("name");
        this.salt = resultSet.getString("salt");
        this.hash = resultSet.getString("hash");
    }

    public static Optional<User> get(String name) throws SQLException {
        try (
                Connection connection = Bootstrap.getDataSource().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select * From users where name = ?")
        ) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(resultSet));
                }
            }
        }
        return Optional.empty();
    }
}
