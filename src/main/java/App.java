
public class App {
    public static void main(String[] args) {
        String albumURL = args[0];
        Scraper scraper = new Scraper(albumURL);
        scraper.getSongs();
    }
}
