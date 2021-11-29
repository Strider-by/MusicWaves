package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.xhr.*;

import java.util.Arrays;

public enum XHRCommandEnum
{
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
    FIND_ARTISTS_FOR_MUSIC_SEARCH_PAGE("find_artists_for_music_search_page", new FindArtistsForMusicSearchPageCommand());
        
    private final String alias;
    private final XHRCommand command;

    XHRCommandEnum(String alias, XHRCommand command) {
        this.alias = alias;
        this.command = command;
    }

    public String getAlias() {
        return alias;
    }

    public XHRCommand getCommand() {
        return command;
    }

    public static XHRCommand getCommandByAlias(String alias) {
        return Arrays.stream(values())
                .filter(command -> command.alias != null)
                .filter(command -> command.alias.equalsIgnoreCase(alias))
                .findAny()
                .map(XHRCommandEnum::getCommand)
                .orElse(UNKNOWN_COMMAND.command);
    }
}
