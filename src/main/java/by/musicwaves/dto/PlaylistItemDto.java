package by.musicwaves.dto;

public class PlaylistItemDto {

    private int id;
    private int audioTrackId;
    private String trackName;
    private boolean active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAudioTrackId() {
        return audioTrackId;
    }

    public void setAudioTrackId(int audioTrackId) {
        this.audioTrackId = audioTrackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
