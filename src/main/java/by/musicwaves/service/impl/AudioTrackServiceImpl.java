package by.musicwaves.service.impl;

import by.musicwaves.dao.AudioTrackDao;
import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.dao.impl.AudioTrackDaoImpl;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.service.AudioTrackService;
import by.musicwaves.service.message.ServiceErrorEnum;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.service.exception.ServiceException;
import by.musicwaves.service.util.UploadableResource;
import by.musicwaves.service.util.UploadableResourcesWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AudioTrackServiceImpl implements AudioTrackService {

    private static final AudioTrackServiceImpl service = new AudioTrackServiceImpl();
    private static final AudioTrackDao audioTrackDao = AudioTrackDaoImpl.getInstance();
    private final static Logger LOGGER = LogManager.getLogger(AudioTrackServiceImpl.class);

    private AudioTrackServiceImpl() {
    }

    public static AudioTrackServiceImpl getInstance() {
        return service;
    }


    @Override
    public ServiceResponse<List<AudioTrack>> findAudioTracks(int albumId) throws ServiceException {
        ServiceResponse<List<AudioTrack>> serviceResponse = new ServiceResponse<>();

        List<AudioTrack> daoResponse;

        try {
            daoResponse = audioTrackDao.findAlbumRelatedTracks(albumId);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        serviceResponse.setStoredValue(daoResponse);
        return serviceResponse;
    }

    @Override
    public ServiceResponse<AudioTrack> createAudioTrack(int albumId, String name, boolean visible, Locale locale) throws ServiceException {
        ServiceResponse<AudioTrack> serviceResponse = new ServiceResponse<>();
        if(name == null || name.equals("")) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_TRACK_NAME, locale);
            return serviceResponse;
        }


        AudioTrack track = new AudioTrack();
        track.setAlbumId(albumId);
        track.setName(name);
        track.setVisible(visible);

        int id;
        try {
            id = audioTrackDao.create(track);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        track.setId(id);
        serviceResponse.setStoredValue(track);
        return serviceResponse;
    }

    @Override
    public ServiceResponse<?> updateAudioTrack(int id, String name, boolean visible, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        if(name == null || name.equals("")) {
            serviceResponse.addErrorOccurrence(ServiceErrorEnum.INVALID_TRACK_NAME, locale);
            return serviceResponse;
        }

        try {
            audioTrackDao.updateNameAndVisibility(id, name, visible);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }

        return new ServiceResponse<>();
    }

    @Override
    public ServiceResponse<?> shiftTrackNumberUp(int trackId, Locale locale) throws ServiceException{
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        try {
            audioTrackDao.shiftTrackNumberUp(trackId);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse<?> shiftTrackNumberDown(int trackId, Locale locale) throws ServiceException{
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        try {
            audioTrackDao.shiftTrackNumberDown(trackId);
        } catch(DaoException ex) {
            throw new ServiceException(ex);
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse<?> deleteAudioTrack(int id, Locale locale) throws ServiceException {
        ServiceResponse<?> serviceResponse = new ServiceResponse<>();
        String fileName;
        try {
            fileName = audioTrackDao.deleteAndGetCorrespondingFileName(id);
        } catch (DaoException ex) {
            throw new ServiceException(ex);
        }

        UploadableResourcesWorker.deleteFile(UploadableResource.AUDIO_TRACK, fileName);
        return serviceResponse;
    }

    @Override
    public ServiceResponse<String> uploadAudioTrack(int trackId, HttpServletRequest request, Locale locale) throws ServiceException {
        // saving new file
        String newFileName;
        try {
            newFileName = UploadableResourcesWorker.uploadFile(UploadableResource.AUDIO_TRACK, request, trackId);
        } catch (ServletException | IOException ex) {
            throw new ServiceException(ex);
        }

        try {
            // updating database record
            String oldFileName = audioTrackDao.updateAudioTrackFileName(trackId, newFileName);
            // deleting old file
            UploadableResourcesWorker.deleteFile(UploadableResource.AUDIO_TRACK, oldFileName);
        } catch (DaoException ex) {
            // deleting new file (we can't use it since database record is not updated
            UploadableResourcesWorker.deleteFile(UploadableResource.AUDIO_TRACK, newFileName);
            throw new ServiceException(ex);
        }
        ServiceResponse<String> serviceResponse = new ServiceResponse<>();
        serviceResponse.setStoredValue(newFileName);
        return serviceResponse;
    }

}
