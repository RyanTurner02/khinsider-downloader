public class Song {
    private String name;
    private String fileType;
    private String downloadLink;

    public Song(String name, String fileType, String downloadLink) {
        this.name = name;
        this.fileType = fileType;
        this.downloadLink = downloadLink;
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

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", fileType='" + fileType + '\'' +
                ", downloadLink='" + downloadLink + '\'' +
                '}';
    }
}
