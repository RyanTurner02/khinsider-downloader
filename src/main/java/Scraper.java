import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

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
        String albumName = getAlbumName(albumDoc);
        System.out.println("Downloading the album: " + albumName);

        String selectedFileType = getSelectedFileType();

        Element songTable = albumDoc.getElementById("songlist");
        Elements songLinks = songTable.getElementsByClass("playlistDownloadSong").select("a");

        // iterate through the song table
        for (Element currentSong : songLinks) {
            String currentSongURL = "https://downloads.khinsider.com/" + currentSong.attr("href");

            try {
                // connect to the current song's webpage and get the song's name and url and download the current song
                Document currentSongDoc = Jsoup.connect(currentSongURL).get();
                Element pageInfo = currentSongDoc.getElementById("EchoTopic");

                String songName = pageInfo.getElementsByTag("p").get(2).getElementsByTag("b").get(1).text();
                String songURL = pageInfo.getElementsByAttributeValueEnding("href", selectedFileType).attr("href");

                String filePath = "downloads/" + albumName + "/" + songName + "." + selectedFileType;
                System.out.println(filePath);
                downloadSong(songURL, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAlbumName(Document doc) {
        Element pageInfo = doc.getElementById("EchoTopic").getElementsByTag("h2").get(0);
        String albumName = pageInfo.text();
        return albumName;
    }

    private Map<Integer, String> getFileTypesList(Document doc) {
        Element tableRow = doc.getElementById("songlist_header");
        Elements headerCells = tableRow.getElementsByTag("th");

        // get the complement of the header cells with file types
        int numFileTypes = headerCells.size() - 4;

        // insert the filetypes into a hashmap
        Map<Integer, String> fileTypes = new HashMap<>(numFileTypes);

        int index = 0;
        for (int i = 3; i < numFileTypes + 3; i++) {
            fileTypes.put(index++, headerCells.get(i).text());
        }
        return fileTypes;
    }

    private String getSelectedFileType() {
        Map<Integer, String> fileTypes = getFileTypesList(albumDoc);
        String selectedFileType;

        if (fileTypes.size() > 1) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Multiple filetypes found. Please select a filetype.");

            for (int i = 0; i < fileTypes.size(); i++) {
                System.out.println(i + 1 + ": " + fileTypes.get(i));
            }
            int fileTypeIndex = reader.nextInt() - 1;
            selectedFileType = fileTypes.get(fileTypeIndex).toLowerCase(Locale.ROOT);
        } else {
            selectedFileType = fileTypes.get(0).toLowerCase(Locale.ROOT);
        }
        return selectedFileType;
    }

    private void downloadSong(String url, String filePath) {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
