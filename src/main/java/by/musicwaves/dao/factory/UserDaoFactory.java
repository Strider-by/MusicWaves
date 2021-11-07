package by.musicwaves.dao.factory;

import by.musicwaves.dao.UserDao;
import by.musicwaves.dao.UserDaoImpl;

public class UserDaoFactory {

    public static UserDao getInstance() {
        return UserDaoImpl.getInstance();
    }
}
