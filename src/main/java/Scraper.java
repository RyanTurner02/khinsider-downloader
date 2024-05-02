import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class Scraper {
    private String url;
    private String albumDirectoryPath;
    private Album album;
    private boolean countOption;
    private Document albumDoc;

    public Scraper(String url) {
        this.url = url;
        this.albumDirectoryPath = "";
        this.album = new Album();

        try {
            this.albumDoc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scrapeAlbum() {
        scrapeAlbumName();
        scrapeFileType();
        String fileType = getFileType();
        downloadSongs(fileType);
    }

    private void scrapeAlbumName() {
        String albumName = url.substring(url.lastIndexOf("/") + 1);
        album.setName(albumName);
        this.albumDirectoryPath = String.format("downloads/%s/", album.getName());
    }

    private void scrapeFileType() {
        Element tableRow = albumDoc.getElementById("songlist_header");
        Elements fileTypeCells = tableRow.getElementsByAttributeValue("width", "60px");

        for (Element fileTypeCell : fileTypeCells) {
            String fileType = String.format(".%s", fileTypeCell.text().toLowerCase());
            album.addFileType(fileType);
        }
    }

    private String getFileType() {
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

    private void downloadSongs(String fileType) {
        System.out.println("Scraping the album.");

        Element songTable = albumDoc.getElementById("songlist");
        Elements songLinks = songTable.getElementsByClass("playlistDownloadSong").select("a");

        for (Element songLink : songLinks) {
            String songURL = String.format("https://downloads.khinsider.com/%s", songLink.attr("href"));

            try {
                Document songDoc = Jsoup.connect(songURL).get();
                Element pageContent = songDoc.getElementById("pageContent");
                Elements songDownloadElements = pageContent.getElementsByTag("p").select("a[href$=" + fileType + "]");

                try {
                    Element songDownloadElement = songDownloadElements.first();
                    String songDownloadLink = songDownloadElement.attr("href");
                    String songName = songDownloadLink.substring(songDownloadLink.lastIndexOf("/") + 1);
                    String songNameDecoded = URLDecoder.decode(songName, "UTF-8");
                    String songFileType = songNameDecoded.substring(songNameDecoded.lastIndexOf("."));

                    Song song = new Song(songNameDecoded, songFileType, songDownloadLink);
                    album.addSong(song, songFileType);

                    String songFilePath = String.format("%s%s", albumDirectoryPath, songNameDecoded);
                    downloadSong(songDownloadLink, songFilePath);
                } catch (Exception e) {
                    System.out.println("Could not find song.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadSong(String url, String filePath) {
        if(FileUtils.getFile(filePath).exists()) {
            System.out.println(filePath + " already exists.");
            return;
        }

        try {
            System.out.println(filePath);
            FileUtils.copyURLToFile(new URL(url), new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrapeAlbumImages() {

    }

    public void downloadImages(Album album) {
        String filePath = "downloads/" + album.getName() + "/img/";
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
