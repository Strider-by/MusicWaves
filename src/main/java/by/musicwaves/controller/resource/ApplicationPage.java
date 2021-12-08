package by.musicwaves.controller.resource;

import java.util.Arrays;

public enum ApplicationPage {

    UNKNOWN_PAGE(null, null),
    ENTRANCE("entrance", "/WEB-INF/jsp/entrance.jsp"),
    PROFILE("profile", "/WEB-INF/jsp/profile.jsp"),
    MUSIC_COMPOUND("music-compound", "/WEB-INF/jsp/music_compound.jsp"),
    MUSIC_SEARCH("music-search", "/WEB-INF/jsp/music_search.jsp"),
    PLAYLISTS("playlists", "/WEB-INF/jsp/playlists.jsp"),
    LISTEN_MUSIC("listen", "/WEB-INF/jsp/listen.jsp"),
    USERS("users", "/WEB-INF/jsp/users.jsp");


    private final String alias;
    private final String pathToPage;

    ApplicationPage(String alias, String pathToPage) {
        this.alias = alias;
        this.pathToPage = pathToPage;
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

}
