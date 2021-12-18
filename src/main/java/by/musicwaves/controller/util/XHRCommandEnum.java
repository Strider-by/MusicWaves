package by.musicwaves.controller.util;

import by.musicwaves.controller.command.xhr.*;

import java.util.Arrays;

public enum XHRCommandEnum {

    UNKNOWN_COMMAND(null, new UnknownCommand(AccessLevelEnum.ALL)),
    CHANGE_USER_ROLE_COMMAND("change_user_role", new ChangeUserRoleCommand(AccessLevelEnum.ADMINISTRATOR_ONLY)),
    FIND_USERS("find_users", new FindUsersCommand(AccessLevelEnum.ADMINISTRATOR_ONLY)),
    DELETE_USER_BY_ADMINISTRATION("delete_user_by_admin", new DeleteUserByAdministrationCommand(AccessLevelEnum.USER_PLUS)),
    FIND_ARTISTS("find_artists", new FindArtistsCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    CREATE_ARTIST("create_artist", new CreateArtistCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    UPDATE_ARTIST("update_artists_name_and_visibility", new UpdateArtistsNameAndVisibilityCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    DELETE_ARTIST("delete_artist", new DeleteArtistCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    FIND_ALBUMS("find_albums", new FindAlbumsCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    CREATE_ALBUM("create_album", new CreateAlbumCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    UPDATE_ALBUM("update_album", new UpdateAlbumCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    DELETE_ALBUM("delete_album", new DeleteAlbumCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    CREATE_AUDIO_TRACK("create_track", new CreateAudioTrackCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    FIND_ALBUM_RELATED_AUDIO_TRACKS("find_album_tracks", new FindAlbumRelatedAudioTracksCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    UPDATE_AUDIO_TRACK("update_track_name_and_visibility", new UpdateAudioTrackCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    SHIFT_TRACK_NUMBER_UP("shift_track_number_up", new ShiftAudioTrackAlbumPositionUpCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    SHIFT_TRACK_NUMBER_DOWN("shift_track_number_down", new ShiftAudioTrackAlbumPositionDownCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    DELETE_AUDIO_TRACK("delete_track", new DeleteAudioTrackCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    UPLOAD_AUDIO_TRACK_FILE("upload_track", new UploadAudioTrackCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    UPLOAD_ARTIST_IMAGE_FILE("upload_artist_image", new UploadArtistImageCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    UPLOAD_ALBUM_IMAGE_FILE("upload_album_image", new UploadAlbumImageCommand(AccessLevelEnum.MUSIC_CURATOR_PLUS)),
    GET_SEARCH_RESULTS_COUNT_FOR_MUSIC_SEARCH_PAGE("get_search_results_count_for_music_search_page", new GetSearchResultsCountForMusicSearchPageCommand(AccessLevelEnum.USER_PLUS)),
    FIND_ARTISTS_FOR_MUSIC_SEARCH_PAGE("find_artists_for_music_search_page", new FindArtistsForMusicSearchPageCommand(AccessLevelEnum.USER_PLUS)),
    FIND_ALBUMS_FOR_MUSIC_SEARCH_PAGE("find_albums_for_music_search_page", new FindAlbumsForMusicSearchPageCommand(AccessLevelEnum.USER_PLUS)),
    FIND_AUDIO_TRACKS_FOR_MUSIC_SEARCH_PAGE("find_tracks_for_music_search_page", new FindAudioTracksForMusicSearchPageCommand(AccessLevelEnum.USER_PLUS)),
    GET_CHOSEN_ARTIST_DATA_FOR_MUSIC_SEARCH_PAGE("get_chosen_artist_data_for_music_search_page", new GetChosenArtistDataForMusicSearchPageCommand(AccessLevelEnum.USER_PLUS)),
    GET_CHOSEN_ALBUM_DATA_FOR_MUSIC_SEARCH_PAGE("get_chosen_album_data_for_music_search_page", new GetChosenAlbumDataForMusicSearchPageCommand(AccessLevelEnum.USER_PLUS)),
    SET_ARTIST_AS_FAVOURITE("set_artist_as_favourite", new SetArtistAsFavouriteCommand(AccessLevelEnum.USER_PLUS)),
    UNSET_ARTIST_AS_FAVOURITE("unset_artist_as_favourite", new UnsetArtistAsFavouriteCommand(AccessLevelEnum.USER_PLUS)),
    SET_ALBUM_AS_FAVOURITE("set_album_as_favourite", new SetAlbumAsFavouriteCommand(AccessLevelEnum.USER_PLUS)),
    UNSET_ALBUM_AS_FAVOURITE("unset_album_as_favourite", new UnsetAlbumAsFavouriteCommand(AccessLevelEnum.USER_PLUS)),
    SET_AUDIO_TRACK_AS_FAVOURITE("set_track_as_favourite", new SetAudioTrackAsFavouriteCommand(AccessLevelEnum.USER_PLUS)),
    UNSET_AUDIO_TRACK_AS_FAVOURITE("unset_track_as_favourite", new UnsetAudioTrackAsFavouriteCommand(AccessLevelEnum.USER_PLUS)),
    CREATE_PLAYLIST("create_playlist", new CreatePlaylistCommand(AccessLevelEnum.USER_PLUS)),
    DELETE_MULTIPLE_PLAYLISTS("delete_multiple_playlists", new DeleteMultiplePlaylistsCommand(AccessLevelEnum.USER_PLUS)),
    GET_PLAYLIST_ITEMS("get_playlist_items", new GetPlaylistTracksCommand(AccessLevelEnum.USER_PLUS)),
    RECORD_PLAYLIST_ITEMS("record_playlist", new RecordPlaylistItemsCommand(AccessLevelEnum.USER_PLUS)),
    GET_USER_PLAYLISTS("get_user_playlists", new GetUserPlaylistsCommand(AccessLevelEnum.USER_PLUS)),
    GET_VISIBLE_AUDIO_TRACK_DATA("get_visible_track_data", new GetVisibleAudioTrackDataCommand(AccessLevelEnum.USER_PLUS)),
    GET_TRACKS_DATA("get_tracks_data_by_tracks_id", new GetAudioTracksDataCommand(AccessLevelEnum.USER_PLUS)),
    CHECK_IF_LOGIN_IS_AVAILABLE("check_if_login_is_available", new CheckIfLoginIsAvailableCommand(AccessLevelEnum.ALL));

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
