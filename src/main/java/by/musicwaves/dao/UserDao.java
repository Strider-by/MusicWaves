package by.musicwaves.dao;

import by.musicwaves.entity.User;
import by.musicwaves.util.Pair;

import java.time.LocalDate;
import java.util.List;

public interface UserDao extends Dao<User> {

    User findByLogin(String login) throws DaoException;

    User findByLoginAndPassword(String login, String password) throws DaoException;

    boolean updateUserLogin(String login, int userId) throws DaoException;

    void updateUserRole(int userId, int roleId) throws DaoException;

    Pair<Integer, List<User>> findUsers(Integer id, String login, int loginSearchTypeId, Integer roleId, LocalDate registerDate, int registerDateCompareTypeId, int fieldIdToBeSortedBy, int sortOrderId, int pageNumber, int recordsPerPage) throws DaoException;

    boolean deleteById(int userId) throws DaoException;

    boolean delete(User user) throws DaoException;
}
