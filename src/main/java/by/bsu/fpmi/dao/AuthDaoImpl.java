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
    public int authenticate2(String token, int pin) {
        try (
            Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT t.user_id as user_id FROM tokens t JOIN pins p ON t.token = p.token WHERE t.token = ? AND p.pin = ? LIMIT 1")
        ) {
            statement.setString(1, token);
            statement.setInt(2, pin);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
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

    @Override
    public boolean hasToken(String token, int stage) {
        try (
                Connection connection = DbUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM tokens WHERE token like ? AND stage = ?")
                ) {
            statement.setString(1, token);
            statement.setInt(2, stage);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0)
                        return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setPin(String token, int pin) {
        try (
            Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO pins (token, pin) VALUES(?, ?)")
        ) {
            statement.setString(1, token);
            statement.setInt(2, pin);

            int count = statement.executeUpdate();
            if (count > 0)
                System.out.println("PIN INSERTED");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPin(String token) {
        try (
            Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT pin FROM pins WHERE token = ? LIMIT 1")
        ) {
            statement.setString(1, token);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("pin");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getUserIdByToken(String token, int stage) {
        try (
                Connection conn = DbUtil.getConnection();
                PreparedStatement stat =
                        conn.prepareStatement("SELECT user_id FROM \"tokens\" WHERE token LIKE ? AND stage = ? LIMIT 1")
        ) {
            stat.setString(1, token);
            stat.setInt(2, stage);

            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Constants.NO_USER_ID;
    }
}
