package by.bsu.fpmi.dao;

import by.bsu.fpmi.entity.User;

import java.util.List;

public interface UserDao {

    public User getUserById(int id);

    public User getUserByLogin(String login);

    public boolean addUser(User user);

    public List<User> getUsers();
}
