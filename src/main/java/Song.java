public class Song {
    private String name;
    private int cdNumber;
    private int trackNumber;

    public Song(String name, int cdNumber, int trackNumber) {
        this.name = name;
        this.cdNumber = cdNumber;
        this.trackNumber = trackNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCDNumber() {
        return cdNumber;
    }

    public void setCDNumber(int cdNumber) {
        this.cdNumber = cdNumber;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }
}
