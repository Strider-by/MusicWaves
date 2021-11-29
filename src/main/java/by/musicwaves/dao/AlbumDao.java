package by.musicwaves.dao;


import by.musicwaves.entity.Album;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;

import java.util.List;

public interface AlbumDao extends Dao<Album> {
    Pair<Integer, List<Album>> findAlbums(int artistId, String name, Integer year, Boolean visibility, int pageNumber, int recordsPerPage) throws DaoException;
    void updateNameYearAndVisibility(int id, String name, Integer year, boolean visible) throws DaoException;
    String updateAlbumImageFileName(int albumId, String newFileName) throws DaoException;

    Pair<String, List<String>> deleteAlbumAndGetRelatedAlbumImageNameAndTracksFilesNames(int id) throws DaoException;
}
