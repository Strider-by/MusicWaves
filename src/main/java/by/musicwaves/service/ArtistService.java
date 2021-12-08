package by.musicwaves.service;

import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.Artist;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.util.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public interface ArtistService {
    ServiceResponse<Pair<Integer, List<Artist>>> findArtists(String name, int nameSearchTypeId, Boolean visible, int pageNumber, int recordsPerPage) throws ServiceException;

    ServiceResponse<Artist> createArtist(String name, boolean visible, Locale locale) throws ServiceException;

    ServiceResponse<?> updateArtist(int id, String name, boolean visible, Locale locale) throws ServiceException;

    ServiceResponse<?> deleteArtist(int id, Locale locale) throws ServiceException;

    ServiceResponse<String> uploadArtistImage(int artistId, HttpServletRequest request, Locale locale) throws ServiceException;
}
