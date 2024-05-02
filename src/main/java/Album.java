import java.util.*;

public class Album {
    private String name;
    private Map<String, List<Song>> songs;

    public Album() {
        songs = new TreeMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<Song>> getSongs() {
        return songs;
    }

    public void setSongs(Map<String, List<Song>> songs) {
        this.songs = songs;
    }

    public void addFileType(String fileType) {
        songs.put(fileType, new ArrayList<>());
    }

    public void addSong(Song currentSong, String fileType) {
        songs.get(fileType).add(currentSong);
        songs.put(fileType, songs.get(fileType));
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", songs=" + songs +
                '}';
    }
}
