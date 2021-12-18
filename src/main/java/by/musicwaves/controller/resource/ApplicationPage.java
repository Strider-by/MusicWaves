package by.musicwaves.controller.resource;

import java.util.Arrays;

/**
 * Represents list of pages that are used in the application.
 * Pages are set with the aliases by which user can get access to them, paths to actual jsp, and {@link AccessLevel}
 * access restriction that can be used to prevent users from getting to pages where they should not get to.
 */
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

    /**
     * Gets Application page by provided page alias. If there is no page that suits that alias, UNKNOWN_PAGE shall be returned.
     *
     * @param alias - the alias to be converted to the requested page if it is possible
     * @return - Application page that matches given alias
     */
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
