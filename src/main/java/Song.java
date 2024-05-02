public class Song {
    private String name;
    private String fileType;
    private String downloadLink;
    private boolean isDownloaded;

    public Song(String name, String fileType, String downloadLink) {
        this.name = name;
        this.fileType = fileType;
        this.downloadLink = downloadLink;
        this.isDownloaded = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", fileType='" + fileType + '\'' +
                ", downloadLink='" + downloadLink + '\'' +
                ", isDownloaded=" + isDownloaded +
                '}';
    }
}
