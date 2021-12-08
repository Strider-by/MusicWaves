package by.musicwaves.dao;


import by.musicwaves.dao.exception.DaoException;
import by.musicwaves.entity.AudioTrack;

import java.util.List;

public interface AudioTrackDao extends Dao<AudioTrack> {
    List<AudioTrack> findAlbumRelatedTracks(int albumId) throws DaoException;
    void updateNameAndVisibility(int id, String name, boolean visible) throws DaoException;
    void rearrangeAlbumTracksNumbers(int albumId) throws DaoException;
    void shiftTrackNumberUp(int trackId) throws DaoException;
    void shiftTrackNumberDown(int trackId) throws DaoException;
    String deleteAndGetCorrespondingFileName(int trackId) throws DaoException;

    String updateAudioTrackFileName(int trackId, String newFileName) throws DaoException;
}
