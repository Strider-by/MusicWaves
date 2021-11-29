package by.musicwaves.dao.factory;

import by.musicwaves.dao.impl.ArtistDaoImpl;
import by.musicwaves.dao.ArtistDao;

public class ArtistDaoFactory {

    public static ArtistDao getInstance() {
        return ArtistDaoImpl.getInstance();
    }
}
