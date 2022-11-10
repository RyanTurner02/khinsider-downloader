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
    private boolean countOption;
    private boolean indicesOption;
    private Document albumDoc;

    public Scraper(String url, boolean countOption, boolean indicesOption) {
        this.url = url;
        this.countOption = countOption;
        this.indicesOption = indicesOption;

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
        Elements songNames = songTable.getElementsByClass("clickable-row").not("[align=\"right\"]");
        Elements songLinks = songTable.getElementsByClass("playlistDownloadSong").select("a");

        int index = 0;
        int numSongs = songLinks.size();
        int numSongsLength = getNumDigits(numSongs);

        // iterate through the song table
        for (Element currentSong : songLinks) {
            String currentSongURL = "https://downloads.khinsider.com/" + currentSong.attr("href");

            try {
                // connect to the current song's webpage
                Document currentSongDoc = Jsoup.connect(currentSongURL).get();
                Element pageContent = currentSongDoc.getElementById("pageContent");

                // get song information
                String songURL = pageContent.getElementsByAttributeValueEnding("href", selectedFileType).attr("href");

                // download the current song
                String filePath = String.format("downloads/%s/", albumName);

                if (indicesOption) {
                    filePath += String.format("%d. ", index + 1);
                }

                filePath += String.format("%s.%s", songNames.get(index).text(), selectedFileType);

                if (countOption) {
                    System.out.printf("[%d/%d] ", index + 1, numSongs);
                }

                System.out.printf("%s\n", filePath);
                downloadSong(songURL, filePath);
                index++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAlbumName(Document doc) {
        return doc.getElementsByTag("h2").get(0).text();
    }

    private Map<Integer, String> getFileTypesList(Document doc) {
        Element tableRow = doc.getElementById("songlist_header");
        Elements fileTypeCells = tableRow.getElementsByAttributeValue("width", "60px");
        int numFileTypes = fileTypeCells.size();

        // insert the filetypes into a hashmap
        Map<Integer, String> fileTypes = new HashMap<>();
        for (int i = 0; i < numFileTypes; i++) {
            fileTypes.put(i, fileTypeCells.get(i).text());
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

    private int getNumDigits(int num) {
        char[] digits = String.valueOf(num).toCharArray();
        return digits.length;
    }

    private String getFormattedIndex(int numDigits, int index) {
        return String.format("%0" + numDigits + "d", index);
    }

    private void downloadSong(String url, String filePath) {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
