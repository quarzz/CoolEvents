package by.bsu.fpmi.dao;

import by.bsu.fpmi.entitty.User;

import java.util.List;

public interface UserDao {

    public User getUserById(int id);

    public boolean addUser(User user);

    public List<User> getUsers();
}