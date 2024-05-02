import java.util.*;

public class Album {
    private Map<String, List<Song>> songs;

    public Album() {
        songs = new TreeMap<>();
    }

    public Map<String, List<Song>> getSongs() {
        return songs;
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
                "songs=" + songs +
                '}';
    }
}
