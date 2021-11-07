package by.musicwaves.dao;

import by.musicwaves.entity.User;

import java.util.List;

public interface UserDao extends Dao<User> {

    User findByLogin(String login) throws DaoException;

    User findByLoginAndPassword(String login, String password) throws DaoException;

    boolean updateUserLogin(String login, int userId) throws DaoException;
}
