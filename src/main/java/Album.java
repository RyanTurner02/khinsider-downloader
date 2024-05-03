import java.util.*;

public class Album {
    private String name;
    private Set<String> fileTypes;

    public Album() {
        fileTypes = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(Set<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public void addFileType(String fileType) {
        fileTypes.add(fileType);
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", fileTypes=" + fileTypes +
                '}';
    }
}
