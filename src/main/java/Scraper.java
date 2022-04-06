import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Scraper {
    private String url;
    private Document albumDoc;

    public Scraper(String url) {
        this.url = url;

        try {
            this.albumDoc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSongs() {
        Element songTable = albumDoc.getElementById("songlist");
        Elements songLinks = songTable.getElementsByClass("playlistDownloadSong").select("a");

        // iterate through the song table
        for (Element currentSong : songLinks) {
            String currentSongURL = "https://downloads.khinsider.com/" + currentSong.attr("href");

            // connect to the current song's webpage
            try {
                Document currentSongDoc = Jsoup.connect(currentSongURL).get();
                Element element = currentSongDoc.getElementById("EchoTopic");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadSong(String url) {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
