package by.musicwaves.dao.factory;

import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.dao.impl.PlaylistDaoImpl;

public class PlaylistDaoFactory {

    public static PlaylistDao getInstance() {
        return PlaylistDaoImpl.getInstance();
    }
}
