
public class App {
    public static void main(String[] args) {
        String url = args[0];
        Scraper scraper = new Scraper(url);
        scraper.getSongs();
    }
}
