package by.musicwaves.dto;

public class AudioTrackDto {

    private int artistId;
    private String artistName;
    private String artistImageName;

    private int albumId;
    private String albumName;
    private String albumImageName;
    private int albumYear;

    private int trackId;
    private String trackName;
    private String trackFileName;
    private int trackNumber;
    private boolean favourite;


    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistImageName() {
        return artistImageName;
    }

    public void setArtistImageName(String artistImageName) {
        this.artistImageName = artistImageName;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImageName() {
        return albumImageName;
    }

    public void setAlbumImageName(String albumImageName) {
        this.albumImageName = albumImageName;
    }

    public int getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(int albumYear) {
        this.albumYear = albumYear;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackFileName() {
        return trackFileName;
    }

    public void setTrackFileName(String trackFileName) {
        this.trackFileName = trackFileName;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
