package by.musicwaves.service;

import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Album;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.util.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public interface AlbumService {
    ServiceResponse<Pair<Integer, List<Album>>> findAlbums(int artistId, String name, Integer year, Boolean visible, int pageNumber, int recordsPerPage) throws ServiceException;

    ServiceResponse<Album> createAlbum(int artistId, String name, int year, boolean visible, Locale locale) throws ServiceException;

    ServiceResponse<?> updateAlbum(int id, String name, Integer year, boolean visible, Locale locale) throws ServiceException;

    ServiceResponse<?> deleteAlbum(int id) throws ServiceException;

    ServiceResponse<String> uploadAlbumImage(int albumId, HttpServletRequest request) throws ServiceException;
}
