package by.musicwaves.controller.resources;

import java.util.Arrays;

public enum ApplicationPage
{
    UNKNOWN_PAGE(null, null),
    INDEX("index", "/jsp/index.jsp"),
    ENTRANCE("entrance", "/jsp/entrance.jsp"),
    PROFILE("profile", "/jsp/profile.jsp"),
    MUSIC_COMPOUND("music-compound", "/jsp/music_compound.jsp"),
    MUSIC_COMPOUND_OLD("music-compound-old", "/jsp/music_compound_old.jsp"),
    MUSIC_SEARCH("music-search", "/jsp/music_search.jsp"),
    ARTISTS("artists", "/jsp/artists.jsp"),
    USERS("users", "/jsp/users.jsp"),
    ERROR403(null, "/jsp/error/403.jsp"),
    ERROR404(null, "/jsp/error/404.jsp");
    

    private final String alias;
    private final String pathToPage;

    ApplicationPage(String alias, String pathToPage) {
        this.alias = alias;
        this.pathToPage = pathToPage;
    }

    public String getAlias() {
        return alias;
    }

    public String getPathToPage() {
        return pathToPage;
    }

    public static ApplicationPage getPageByUri(String uri) {
        return Arrays.stream(ApplicationPage.values())
                .filter(applicationPage -> uri.equals(applicationPage.pathToPage))
                .findAny()
                .orElse(UNKNOWN_PAGE);
    }

    public static ApplicationPage getPageByAlias(String alias) {
        return Arrays.stream(ApplicationPage.values())
                .filter(applicationPage -> alias.equals(applicationPage.getAlias()))
                .findAny()
                .orElse(UNKNOWN_PAGE);
    }
    
    

}
