package by.musicwaves.dto;

public class AlbumDto {

    private int artistId;
    private String artistName;
    private String artistImageName;
    private int albumId;
    private String albumName;
    private String albumImageName;
    private int albumYear;
    private int tracksCountAlbumHas;
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

    public int getTracksCountAlbumHas() {
        return tracksCountAlbumHas;
    }

    public void setTracksCountAlbumHas(int tracksCountAlbumHas) {
        this.tracksCountAlbumHas = tracksCountAlbumHas;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
