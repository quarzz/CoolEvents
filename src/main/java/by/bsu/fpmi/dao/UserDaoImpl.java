package by.bsu.fpmi.dao;

import by.bsu.fpmi.entitty.User;
import by.bsu.fpmi.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    protected User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setLogin(rs.getString("login"));
        return user;
    }

    public User getUserById(int id) {
        User user = null;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM \"Users\" WHERE id=?")) {
                stat.setInt(1, id);

                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        user = createUserFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User getUserByLogin(String login) {
        User user = null;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM \"Users\" WHERE login=?")) {
                stat.setString(1, login);

                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        user = createUserFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean addUser(User user) {
        boolean res = false;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement(
                    "INSERT INTO \"Users\" (login, password, \"firstName\", \"lastName\") VALUES (?,?,?,?) RETURNING id")) {
                stat.setString(1, user.getLogin());
                stat.setString(2, user.getPassword());
                stat.setString(3, user.getFirstName());
                stat.setString(4, user.getLastName());

                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        user.setId(rs.getInt("id"));
                        res = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM \"Users\"")) {
                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        users.add(createUserFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
