package by.musicwaves.dao;


import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;

import java.util.List;

public interface ArtistDao extends Dao<Artist> {
    Pair<Integer, List<Artist>> findArtists(String name, Boolean visibility, int nameSearchTypeId, int pageNumber, int recordsPerPage) throws DaoException;
    void updateNameAndVisibility(int id, String name, boolean visible) throws DaoException;
    String updateArtistImageFileName(int artistId, String newFileName) throws DaoException;

    Triplet<String, List<String>, List<String>> deleteArtistAndGetFilesToDelete(int id) throws DaoException;
}
