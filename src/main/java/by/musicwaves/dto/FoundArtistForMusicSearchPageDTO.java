package by.musicwaves.dto;

public class FoundArtistForMusicSearchPageDTO {

    private int artistId;
    private String artistName;
    private String artistImageName;
    private boolean isFavourite;
    private int albumsCountArtistHas;

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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public int getAlbumsCountArtistHas() {
        return albumsCountArtistHas;
    }

    public void setAlbumsCountArtistHas(int albumsCountArtistHas) {
        this.albumsCountArtistHas = albumsCountArtistHas;
    }
}
