package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.xhr.*;

import java.util.Arrays;

public enum XHRCommandEnum {

    UNKNOWN_COMMAND(null, new UnknownCommand()),
    CHANGE_USER_ROLE_COMMAND("change_user_role", new ChangeUserRoleCommand()),
    FIND_USERS("find_users", new FindUsersCommand()),
    DELETE_USER_BY_ADMINISTRATION("delete_user_by_admin", new DeleteUserByAdministrationCommand()),
    FIND_ARTISTS("find_artists", new FindArtistsCommand()),
    CREATE_ARTIST("create_artist", new CreateArtistCommand()),
    UPDATE_ARTIST("update_artists_name_and_visibility", new UpdateArtistsNameAndVisibilityCommand()),
    DELETE_ARTIST("delete_artist", new DeleteArtistCommand()),
    FIND_ALBUMS("find_albums", new FindAlbumsCommand()),
    CREATE_ALBUM("create_album", new CreateAlbumCommand()),
    UPDATE_ALBUM("update_album", new UpdateAlbumCommand()),
    DELETE_ALBUM("delete_album", new DeleteAlbumCommand()),
    CREATE_AUDIO_TRACK("create_track", new CreateAudioTrackCommand()),
    FIND_ALBUM_RELATED_AUDIO_TRACKS("find_album_tracks", new FindAlbumRelatedAudioTracksCommand()),
    UPDATE_AUDIO_TRACK("update_track_name_and_visibility", new UpdateAudioTrackCommand()),
    SHIFT_TRACK_NUMBER_UP("shift_track_number_up", new ShiftAudioTrackAlbumPositionUpCommand()),
    SHIFT_TRACK_NUMBER_DOWN("shift_track_number_down", new ShiftAudioTrackAlbumPositionDownCommand()),
    DELETE_AUDIO_TRACK("delete_track", new DeleteAudioTrackCommand()),
    UPLOAD_AUDIO_TRACK_FILE("upload_track", new UploadAudioTrackCommand()),
    UPLOAD_ARTIST_IMAGE_FILE("upload_artist_image", new UploadArtistImageCommand()),
    UPLOAD_ALBUM_IMAGE_FILE("upload_album_image", new UploadAlbumImageCommand()),
    GET_SEARCH_RESULTS_COUNT_FOR_MUSIC_SEARCH_PAGE("get_search_results_count_for_music_search_page", new GetSearchResultsCountForMusicSearchPageCommand()),
    FIND_ARTISTS_FOR_MUSIC_SEARCH_PAGE("find_artists_for_music_search_page", new FindArtistsForMusicSearchPageCommand()),
    FIND_ALBUMS_FOR_MUSIC_SEARCH_PAGE("find_albums_for_music_search_page", new FindAlbumsForMusicSearchPageCommand()),
    FIND_AUDIO_TRACKS_FOR_MUSIC_SEARCH_PAGE("find_tracks_for_music_search_page", new FindAudioTracksForMusicSearchPageCommand()),
    GET_CHOSEN_ARTIST_DATA_FOR_MUSIC_SEARCH_PAGE("get_chosen_artist_data_for_music_search_page", new GetChosenArtistDataForMusicSearchPageCommand()),
    GET_CHOSEN_ALBUM_DATA_FOR_MUSIC_SEARCH_PAGE("get_chosen_album_data_for_music_search_page", new GetChosenAlbumDataForMusicSearchPageCommand()),
    SET_ARTIST_AS_FAVOURITE("set_artist_as_favourite", new SetArtistAsFavouriteCommand()),
    UNSET_ARTIST_AS_FAVOURITE("unset_artist_as_favourite", new UnsetArtistAsFavouriteCommand()),
    SET_ALBUM_AS_FAVOURITE("set_album_as_favourite", new SetAlbumAsFavouriteCommand()),
    UNSET_ALBUM_AS_FAVOURITE("unset_album_as_favourite", new UnsetAlbumAsFavouriteCommand()),
    SET_AUDIO_TRACK_AS_FAVOURITE("set_track_as_favourite", new SetAudioTrackAsFavouriteCommand()),
    UNSET_AUDIO_TRACK_AS_FAVOURITE("unset_track_as_favourite", new UnsetAudioTrackAsFavouriteCommand()),
    CREATE_PLAYLIST("create_playlist", new CreatePlaylistCommand()),
    DELETE_MULTIPLE_PLAYLISTS("delete_multiple_playlists", new DeleteMultiplePlaylistsCommand()),
    GET_PLAYLIST_ITEMS("get_playlist_items", new GetPlaylistTracksCommand()),
    RECORD_PLAYLIST_ITEMS("record_playlist", new RecordPlaylistItemsCommand()),
    GET_USER_PLAYLISTS("get_user_playlists", new GetUserPlaylistsCommand()),
    GET_VISIBLE_AUDIO_TRACK_DATA("get_visible_track_data", new GetVisibleAudioTrackData()),
    GET_TRACKS_DATA("get_tracks_data_by_tracks_id", new GetAudioTracksDataCommand()),
    CHECK_IF_LOGIN_IS_AVAILABLE("check_if_login_is_available", new CheckIfLoginIsAvailableCommand());

    private final String alias;
    private final XHRCommand command;

    XHRCommandEnum(String alias, XHRCommand command) {
        this.alias = alias;
        this.command = command;
    }

    public static XHRCommand getCommandByAlias(String alias) {
        return Arrays.stream(values())
                .filter(command -> command.alias != null)
                .filter(command -> command.alias.equalsIgnoreCase(alias))
                .findAny()
                .map(XHRCommandEnum::getCommand)
                .orElse(UNKNOWN_COMMAND.command);
    }

    public String getAlias() {
        return alias;
    }

    public XHRCommand getCommand() {
        return command;
    }
}
