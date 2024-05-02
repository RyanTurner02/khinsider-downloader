import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class Scraper {
    private String url;
    private Album album;
    private boolean countOption;
    private boolean indicesOption;
    private Document albumDoc;

    public Scraper(String url) {
        this.url = url;
        this.album = new Album();

        try {
            this.albumDoc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scrapeAlbum() {
        scrapeFileType();
        String userFileType = getUserFileType();
        scrapeSongs(userFileType);
    }

    private void scrapeFileType() {
        Element tableRow = albumDoc.getElementById("songlist_header");
        Elements fileTypeCells = tableRow.getElementsByAttributeValue("width", "60px");

        for (Element fileTypeCell : fileTypeCells) {
            String fileType = String.format(".%s", fileTypeCell.text().toLowerCase());
            album.addFileType(fileType);
        }
    }

    private String getUserFileType() {
        List<String> fileTypes = new ArrayList<>(album.getSongs().keySet());

        if (fileTypes.isEmpty()) {
            System.out.println("Could not retrieve any file types.");
            System.exit(1);
        }

        if (fileTypes.size() == 1) {
            return fileTypes.get(0);
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Multiple filetypes found. Please select a filetype:");
        printFileTypes(fileTypes);

        int userOption = scanner.nextInt() - 1;
        boolean isOutOfBounds = userOption < 0 || userOption >= fileTypes.size();

        if (isOutOfBounds) {
            System.out.println("Invalid option: out of bounds.");
            System.exit(1);
        }

        scanner.close();
        return fileTypes.get(userOption);
    }

    private void printFileTypes(List<String> fileTypes) {
        int counter = 1;

        for (String fileType : fileTypes) {
            System.out.printf("[%d] %s\n", counter, fileType);
            counter++;
        }
    }

    private void scrapeSongs(String userFileType) {
        Element songTable = albumDoc.getElementById("songlist");
        Elements songLinks = songTable.getElementsByClass("playlistDownloadSong").select("a");

        for (Element songLink : songLinks) {
            String songURL = String.format("https://downloads.khinsider.com/%s", songLink.attr("href"));

            try {
                Document songDoc = Jsoup.connect(songURL).get();
                Element pageContent = songDoc.getElementById("pageContent");
                Elements songDownloadElements = pageContent.getElementsByTag("p").select("a[href$=" + userFileType + "]");
                Element songDownloadElement = songDownloadElements.first();

                String songDownloadLink = songDownloadElement.attr("href");
                String songName = songDownloadLink.substring(songDownloadLink.lastIndexOf("/") + 1);
                String songNameDecoded = URLDecoder.decode(songName, "UTF-8");
                String songFileType = songNameDecoded.substring(songNameDecoded.lastIndexOf("."));

                Song song = new Song(songNameDecoded, songFileType, songDownloadLink);
                album.addSong(song, songFileType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(album);
    }

    private void scrapeAlbumImages() {

    }

    public void downloadAlbum() {
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

                String songURL = pageContent.getElementsByAttributeValueEnding("href", selectedFileType).attr("href");
                String filePath = String.format("downloads/%s/", albumName);
                String counterString = "";

                // add the current song's index to the file path
                if (indicesOption) {
                    filePath += getFormattedIndex(numSongsLength, index + 1);
                }

                filePath += String.format("%s.%s", songNames.get(index).text(), selectedFileType);

                // display the counter
                if (countOption) {
                    counterString = String.format("[%d/%d] ", index + 1, numSongs);
                }

                index++;

                // check if the song already exists
                if (new File(filePath).exists()) {
                    continue;
                }

                System.out.printf("%s%s\n", counterString, filePath);
                downloadSong(songURL, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAlbumName(Document doc) {
        String[] urlArr = url.split("/");
        String albumName = urlArr[urlArr.length - 1];

        return albumName;
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
            System.out.println("Multiple filetypes found. Please select a filetype:");

            for (int i = 0; i < fileTypes.size(); i++) {
                System.out.printf("[%d]. %s\n", i + 1, fileTypes.get(i));
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
        return String.format("%0" + numDigits + "d. ", index);
    }

    private void downloadSong(String url, String filePath) {
        try {
            FileUtils.copyURLToFile(new URL(url), new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadImages() {
        String filePath = "downloads/" + getAlbumName(albumDoc) + "/img/";
        Elements pictures = albumDoc.getElementsByClass("albumImage").select("a");
        int index = 1;

        // iterate through each picture
        for (Element currentPicture : pictures) {
            // get file information
            String currentPictureURL = currentPicture.attr("href");

            String[] urlArr = currentPictureURL.split("/");
            String currentPictureName = urlArr[urlArr.length - 1];

            String currentFilePath = filePath + currentPictureName;

            // check if the picture already exists
            if (new File(currentFilePath).exists()) {
                index++;
                continue;
            }

            // download the current picture
            if (countOption) {
                System.out.printf("[%d/%d] ", index++, pictures.size());
            }

            System.out.printf("%s\n", currentFilePath);

            try {
                FileUtils.copyURLToFile(new URL(currentPictureURL), new File(currentFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
