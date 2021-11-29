package by.musicwaves.service;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.factory.ArtistDaoFactory;
import by.musicwaves.dao.ArtistDao;
import by.musicwaves.entity.Artist;
import by.musicwaves.util.Pair;
import by.musicwaves.util.Triplet;
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

public class ArtistService {

    private static final ArtistService service = new ArtistService();
    private static final ArtistDao artistDao = ArtistDaoFactory.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(ArtistService.class);

    private ArtistService() {
    }

    public static ArtistService getInstance() {
        return service;
    }


    public ServiceResponse<Pair<Integer, List<Artist>>> findArtists(String name, int nameSearchTypeId, Boolean visible, int pageNumber, int recordsPerPage) throws ServiceException {
        ServiceResponse<Pair<Integer, List<Artist>>> serviceResponse = new ServiceResponse<>();

        Pair<Integer, List<Artist>> daoResponse;

        try {
            daoResponse = artistDao.findArtists(
                    name, visible,
                    nameSearchTypeId, pageNumber, recordsPerPage);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        serviceResponse.setStoredValue(daoResponse);
        return serviceResponse;
    }

    public ServiceResponse<Artist> createArtist(String name, boolean visible, Locale locale) throws ServiceException {
        ServiceResponse<Artist> serviceResponse = new ServiceResponse<>();
        if(name == null || name.equals("")) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_ARTIST_NAME, locale);
            return serviceResponse;
        }


        Artist artist = new Artist();
        artist.setName(name);
        artist.setVisible(visible);

        int id;
        try {
            id = artistDao.create(artist);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        artist.setId(id);
        serviceResponse.setStoredValue(artist);
        return serviceResponse;
    }

    public ServiceResponse<?> updateArtist(int id, String name, boolean visible, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        if(name == null || name.equals("")) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_ARTIST_NAME, locale);
            return serviceResponse;
        }

        try {
            artistDao.updateNameAndVisibility(id, name, visible);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    /*public ServiceResponse<?> deleteArtist(int id, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        try {
            artistDao.deleteById(id);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }
        // todo: delete image
        return serviceResponse;
    }*/

    public ServiceResponse<?> deleteArtist(int id, Locale locale) throws ServiceException {

        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        Triplet<String, List<String>, List<String>> filesToDelete;

        try {
            filesToDelete = artistDao.deleteArtistAndGetFilesToDelete(id);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        // files to delete
        String artistImage = filesToDelete.getFirstValue();
        List<String> albumsImages = filesToDelete.getSecondValue();
        List<String> audioTracksFiles = filesToDelete.getThirdValue();
        LOGGER.debug("artist image to delete is: " + artistImage);
        LOGGER.debug("albums images to delete are: " + albumsImages);
        LOGGER.debug("audio tracks files to delete are: " + audioTracksFiles);

        if (artistImage != null) {
            UploadableResourcesWorker.deleteFile(UploadableResource.ARTIST_IMAGE, artistImage);
        }

        albumsImages.stream()
                .filter(Objects::nonNull)
                .forEach(file -> UploadableResourcesWorker.deleteFile(UploadableResource.ALBUM_IMAGE, file));

        audioTracksFiles.stream()
                .filter(Objects::nonNull)
                .forEach(file -> UploadableResourcesWorker.deleteFile(UploadableResource.AUDIO_TRACK, file));

        return serviceResponse;
    }

    public ServiceResponse<String> uploadArtistImage(int artistId, HttpServletRequest request, Locale locale) throws ServiceException {
        // saving new file
        String newFileName;
        try {
            newFileName = UploadableResourcesWorker.uploadFile(UploadableResource.ARTIST_IMAGE, request, artistId);
        } catch (ServletException | IOException ex) {
            throw new ServiceException(ex);
        }

        try {
            // updating database record
            String oldFileName = artistDao.updateArtistImageFileName(artistId, newFileName);
            // deleting old file
            UploadableResourcesWorker.deleteFile(UploadableResource.ARTIST_IMAGE, oldFileName);
        } catch (DaoException ex) {
            // deleting new file (we can't use it since database record is not updated
            UploadableResourcesWorker.deleteFile(UploadableResource.ARTIST_IMAGE, newFileName);
            throw new ServiceException(ex);
        }
        ServiceResponse<String> serviceResponse = new ServiceResponse<>();
        serviceResponse.setStoredValue(newFileName);
        return serviceResponse;
    }
}
