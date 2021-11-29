package by.musicwaves.dao.factory;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.impl.AlbumDaoImpl;
import by.musicwaves.dao.impl.CrossEntityDaoImpl;
import by.musicwaves.dao.util.CrossEntityDao;

public class CrossEntityDaoFactory {

    public static CrossEntityDao getInstance() {
        return CrossEntityDaoImpl.getInstance();
    }
}
