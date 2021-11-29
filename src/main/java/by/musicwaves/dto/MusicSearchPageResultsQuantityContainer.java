package by.musicwaves.dto;

public class MusicSearchPageResultsQuantityContainer<T> {

    private int artistsFound;
    private int albumsFound;
    private int audioTracksFound;
    private T storedValue;

    public int getArtistsFound() {
        return artistsFound;
    }

    public void setArtistsFound(int artistsFound) {
        this.artistsFound = artistsFound;
    }

    public int getAlbumsFound() {
        return albumsFound;
    }

    public void setAlbumsFound(int albumsFound) {
        this.albumsFound = albumsFound;
    }

    public int getAudioTracksFound() {
        return audioTracksFound;
    }

    public void setAudioTracksFound(int audioTracksFound) {
        this.audioTracksFound = audioTracksFound;
    }

    public T getStoredValue() {
        return storedValue;
    }

    public void setStoredValue(T storedValue) {
        this.storedValue = storedValue;
    }
}
