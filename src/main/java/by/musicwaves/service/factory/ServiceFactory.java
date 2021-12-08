package by.musicwaves.service.factory;

import by.musicwaves.service.*;
import by.musicwaves.service.impl.*;

public class ServiceFactory {

    private static ServiceFactory instance = new ServiceFactory();

    private AlbumService albumService = AlbumServiceImpl.getInstance();
    private ArtistService artistService = ArtistServiceImpl.getInstance();
    private AudioTrackService audioTrackService = AudioTrackServiceImpl.getInstance();
    private CrossEntityService crossEntityService = CrossEntityServiceImpl.getInstance();
    private PlaylistService playlistService = PlaylistServiceImpl.getInstance();
    private UserService userService = UserServiceImpl.getInstance();

    public static ServiceFactory getInstance() {
        return instance;
    }

    public AlbumService getAlbumService() {
        return albumService;
    }

    public ArtistService getArtistService() {
        return artistService;
    }

    public AudioTrackService getAudioTrackService() {
        return audioTrackService;
    }

    public CrossEntityService getCrossEntityService() {
        return crossEntityService;
    }

    public PlaylistService getPlaylistService() {
        return playlistService;
    }

    public UserService getUserService() {
        return userService;
    }
}
