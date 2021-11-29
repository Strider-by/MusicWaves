package by.musicwaves.dao.factory;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.ArtistDao;
import by.musicwaves.dao.impl.AlbumDaoImpl;
import by.musicwaves.dao.impl.ArtistDaoImpl;

public class AlbumDaoFactory {

    public static AlbumDao getInstance() {
        return AlbumDaoImpl.getInstance();
    }
}
