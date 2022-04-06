import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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
        Map<Integer, String> fileTypes = getFileTypesList(albumDoc);
        int fileTypeIndex = 0;
        String selectedFileType = "";

        if (fileTypes.size() > 1) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Multiple filetypes found. Please select a filetype.");

            for(int i = 0; i < fileTypes.size(); i++) {
                System.out.println(i + 1 + ": " + fileTypes.get(i));
            }
            fileTypeIndex = reader.nextInt() - 1;
            selectedFileType = fileTypes.get(fileTypeIndex);
        } else {
            selectedFileType = fileTypes.get(0);
        }

        Element songTable = albumDoc.getElementById("songlist");
        Elements songLinks = songTable.getElementsByClass("playlistDownloadSong").select("a");

        // iterate through the song table
        for (Element currentSong : songLinks) {
            String currentSongURL = "https://downloads.khinsider.com/" + currentSong.attr("href");

            try {
                // connect to the current song's webpage and
                Document currentSongDoc = Jsoup.connect(currentSongURL).get();
                Element pageInfo = currentSongDoc.getElementById("EchoTopic");
                Elements paragraphs = pageInfo.getElementsByTag("p");

                // get the complement of the paragraph tags with download links
                int numFileTypes = paragraphs.size() - 4;

//                // get filetype urls
//                for (int i = 3; i < numFileTypes + 3; i++) {
//                    String mp3URL = paragraphs.get(3).getElementsByTag("a").attr("href");
//                    String flacURL = paragraphs.get(4).getElementsByTag("a").attr("href");
//                    System.out.println(i + " " + paragraphs.get(i).getElementsByTag("a").attr("href"));
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void downloadSong(String url) {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
