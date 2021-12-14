package by.musicwaves.service;

import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.service.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public interface AudioTrackService {
    ServiceResponse<List<AudioTrack>> findAudioTracks(int albumId, Locale locale) throws ServiceException;

    ServiceResponse<AudioTrack> createAudioTrack(int albumId, String name, boolean visible, Locale locale) throws ServiceException;

    ServiceResponse<?> updateAudioTrack(int id, String name, boolean visible, Locale locale) throws ServiceException;

    ServiceResponse<?> shiftTrackNumberUp(int trackId, Locale locale) throws ServiceException;

    ServiceResponse<?> shiftTrackNumberDown(int trackId, Locale locale) throws ServiceException;

    ServiceResponse<?> deleteAudioTrack(int id, Locale locale) throws ServiceException;

    ServiceResponse<String> uploadAudioTrack(int trackId, HttpServletRequest request, Locale locale) throws ServiceException;
}
