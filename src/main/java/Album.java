import java.util.List;
import java.util.ArrayList;

public class Album {
    private List<Song> songs;

    public Album() {
        songs = new ArrayList<>();
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
