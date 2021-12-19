package by.musicwaves.controller.util;

import java.util.Arrays;

/**
 * Represents list of pages that are used in the application.
 * Pages are set with the aliases by which user can get access to them, paths to actual jsp, and {@link AccessLevelEnum}
 * access restriction that can be used to prevent users from getting to pages where they should not get to.
 */
public enum ApplicationPageEnum {

    UNKNOWN_PAGE(null, null, null),
    ENTRANCE("entrance", "/WEB-INF/jsp/entrance.jsp", AccessLevelEnum.ALL),
    PROFILE("profile", "/WEB-INF/jsp/profile.jsp", AccessLevelEnum.USER_PLUS),
    MUSIC_COMPOUND("music-compound", "/WEB-INF/jsp/music_compound.jsp", AccessLevelEnum.MUSIC_CURATOR_PLUS),
    MUSIC_SEARCH("music-search", "/WEB-INF/jsp/music_search.jsp", AccessLevelEnum.USER_PLUS),
    PLAYLISTS("playlists", "/WEB-INF/jsp/playlists.jsp", AccessLevelEnum.USER_PLUS),
    LISTEN_MUSIC("listen", "/WEB-INF/jsp/listen.jsp", AccessLevelEnum.USER_PLUS),
    USERS("users", "/WEB-INF/jsp/users.jsp", AccessLevelEnum.ADMINISTRATOR_ONLY);


    private final String alias;
    private final String pathToPage;
    private final AccessLevelEnum accessLevelEnum;

    ApplicationPageEnum(String alias, String pathToPage, AccessLevelEnum accessLevelEnum) {
        this.alias = alias;
        this.pathToPage = pathToPage;
        this.accessLevelEnum = accessLevelEnum;
    }

    /**
     * Gets Application page by provided page alias. If there is no page that suits that alias, UNKNOWN_PAGE shall be returned.
     *
     * @param alias - the alias to be converted to the requested page if it is possible
     * @return - Application page that matches given alias
     */
    public static ApplicationPageEnum getPageByAlias(String alias) {
        return Arrays.stream(ApplicationPageEnum.values())
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

    public AccessLevelEnum getAccessLevel() {
        return accessLevelEnum;
    }
}
