package by.musicwaves.service;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.factory.AlbumDaoFactory;
import by.musicwaves.entity.Album;
import by.musicwaves.util.Pair;
import by.musicwaves.util.UploadableResource;
import by.musicwaves.util.UploadableResourcesWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlbumService {

    private static final AlbumService service = new AlbumService();
    private static final AlbumDao albumDao = AlbumDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(AlbumService.class);

    private AlbumService() {
    }

    public static AlbumService getInstance() {
        return service;
    }


    public ServiceResponse<Pair<Integer, List<Album>>> findAlbums(int artistId, String name, Integer year, Boolean visible, int pageNumber, int recordsPerPage) throws ServiceException {
        ServiceResponse<Pair<Integer, List<Album>>> serviceResponse = new ServiceResponse<>();

        Pair<Integer, List<Album>> daoResponse;

        try {
            daoResponse = albumDao.findAlbums(
                    artistId, name, year, visible,
                     pageNumber, recordsPerPage);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        serviceResponse.setStoredValue(daoResponse);
        return serviceResponse;
    }

    public ServiceResponse<Album> createAlbum(int artistId, String name, int year, boolean visible, Locale locale) throws ServiceException {
        ServiceResponse<Album> serviceResponse = new ServiceResponse<>();
        if(name == null || name.equals("")) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_ALBUM_NAME, locale);
            return serviceResponse;
        }


        Album album = new Album();
        album.setArtistId(artistId);
        album.setName(name);
        album.setYear(year);
        album.setVisible(visible);

        int id;
        try {
            id = albumDao.create(album);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        album.setId(id);
        serviceResponse.setStoredValue(album);
        return serviceResponse;
    }

    public ServiceResponse<?> updateAlbum(int id, String name, Integer year, boolean visible, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        if(name == null || name.equals("")) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_ALBUM_NAME, locale);
            return serviceResponse;
        }

        try {
            albumDao.updateNameYearAndVisibility(id, name, year, visible);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    /*public ServiceResponse<?> deleteAlbum(int id, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        try {
            albumDao.deleteById(id);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
        // todo: delete image
        // todo: delete all track files
        return serviceResponse;
    }*/

    public ServiceResponse<?> deleteAlbum(int id, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        Pair<String, List<String>> filesToDelete;
        try {
            filesToDelete = albumDao.deleteAlbumAndGetRelatedAlbumImageNameAndTracksFilesNames(id);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        String imageFileName = filesToDelete.getFirstValue();
        List<String> audioTracksFilesNames = filesToDelete.getSecondValue();
        LOGGER.debug("album image to delete is: " + imageFileName);
        LOGGER.debug("tracks files names to delete are: " + audioTracksFilesNames);

        // deleting files
        if (imageFileName != null) {
            UploadableResourcesWorker.deleteFile(UploadableResource.ALBUM_IMAGE, imageFileName);
        }
        audioTracksFilesNames.stream()
                .filter(Objects::nonNull)
                .forEach(fileName -> UploadableResourcesWorker.deleteFile(UploadableResource.AUDIO_TRACK, fileName));

        return serviceResponse;
    }

    public ServiceResponse<String> uploadAlbumImage(int albumId, HttpServletRequest request, Locale locale) throws ServiceException {
        // saving new file
        String newFileName;
        try {
            newFileName = UploadableResourcesWorker.uploadFile(UploadableResource.ALBUM_IMAGE, request, albumId);
        } catch (ServletException | IOException ex) {
            throw new ServiceException(ex);
        }

        try {
            // updating database record
            String oldFileName = albumDao.updateAlbumImageFileName(albumId, newFileName);
            // deleting old file
            UploadableResourcesWorker.deleteFile(UploadableResource.ALBUM_IMAGE, oldFileName);
        } catch (DaoException ex) {
            // deleting new file (we can't use it since database record is not updated
            UploadableResourcesWorker.deleteFile(UploadableResource.ALBUM_IMAGE, newFileName);
            throw new ServiceException(ex);
        }
        ServiceResponse<String> serviceResponse = new ServiceResponse<>();
        serviceResponse.setStoredValue(newFileName);
        return serviceResponse;
    }
}
