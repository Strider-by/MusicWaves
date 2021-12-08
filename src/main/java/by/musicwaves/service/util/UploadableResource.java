package by.musicwaves.service.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public enum UploadableResource {

    UNKNOWN_RESOURCE("", "unknown"),
    AUDIO_TRACK("music", "music"),
    ALBUM_IMAGE("album-images", "album_images"),
    ARTIST_IMAGE("artist-images", "artist_images");

    private final static String BUNDLE_NAME = "uploadable";
    private String pathToResourceDirectory;
    private String alias;

    UploadableResource(String alias, String propertyKey) {
        this.alias = alias;
        this.pathToResourceDirectory = ResourceBundle.getBundle(BUNDLE_NAME).getString(propertyKey);
    }

    public static UploadableResource getByAlias(String alias) {
        return Arrays.stream(values())
                .filter(Objects::nonNull)
                .filter(resource -> resource.alias.equalsIgnoreCase(alias))
                .findAny()
                .orElse(UNKNOWN_RESOURCE);
    }

    public String getPathToResourceDirectory() {
        return pathToResourceDirectory;
    }
}
