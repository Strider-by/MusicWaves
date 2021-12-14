package by.musicwaves.controller.resource;

import java.util.Arrays;

public enum ApplicationPage {

    UNKNOWN_PAGE(null, null, null),
    ENTRANCE("entrance", "/WEB-INF/jsp/entrance.jsp", AccessLevel.ALL),
    PROFILE("profile", "/WEB-INF/jsp/profile.jsp", AccessLevel.USER_PLUS),
    MUSIC_COMPOUND("music-compound", "/WEB-INF/jsp/music_compound.jsp", AccessLevel.MUSIC_CURATOR_PLUS),
    MUSIC_SEARCH("music-search", "/WEB-INF/jsp/music_search.jsp", AccessLevel.USER_PLUS),
    PLAYLISTS("playlists", "/WEB-INF/jsp/playlists.jsp", AccessLevel.USER_PLUS),
    LISTEN_MUSIC("listen", "/WEB-INF/jsp/listen.jsp", AccessLevel.USER_PLUS),
    USERS("users", "/WEB-INF/jsp/users.jsp", AccessLevel.ADMINISTRATOR_ONLY);


    private final String alias;
    private final String pathToPage;
    private final AccessLevel accessLevel;

    ApplicationPage(String alias, String pathToPage, AccessLevel accessLevel) {
        this.alias = alias;
        this.pathToPage = pathToPage;
        this.accessLevel = accessLevel;
    }

    public static ApplicationPage getPageByAlias(String alias) {
        return Arrays.stream(ApplicationPage.values())
                .filter(applicationPage -> alias.equals(applicationPage.getAlias()))
                .findAny()
                .orElse(UNKNOWN_PAGE);
    }

    public String getAlias() {
        return alias;
    }

    public String getPathToPage() {
        return pathToPage;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}
