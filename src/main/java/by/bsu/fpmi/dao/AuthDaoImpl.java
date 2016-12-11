package by.bsu.fpmi.dao;

import by.bsu.fpmi.util.Constants;
import by.bsu.fpmi.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDaoImpl implements AuthDao {
    @Override
    public int authenticate(String login, String password) {
        try (
                Connection connection = DbUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id FROM \"Users\" WHERE login LIKE ? AND password LIKE ? LIMIT 1")
                ) {
            statement.setString(1, login);
            statement.setString(2, password);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Constants.NO_USER_ID;
    }

    @Override
    public void addToken(int userID, String token, int stage) {
        try (
                Connection connection = DbUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO \"tokens\" (user_id, token, stage) VALUES(?, ?, ?)")
                ) {
            statement.setInt(1, userID);
            statement.setString(2, token);
            statement.setInt(3, stage);

            statement.executeUpdate();
            System.out.println("INSETED");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteToken(String token) {
        try (
                Connection connection = DbUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM \"tokens\" WHERE token = ?")
                ) {
            statement.setString(1, token);

            statement.executeUpdate();
            System.out.println("DELETED");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
