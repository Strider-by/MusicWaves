package by.musicwaves.dao.factory;

import by.musicwaves.dao.impl.CrossEntityDaoImpl;
import by.musicwaves.dao.CrossEntityDao;

public class CrossEntityDaoFactory {

    public static CrossEntityDao getInstance() {
        return CrossEntityDaoImpl.getInstance();
    }
}
