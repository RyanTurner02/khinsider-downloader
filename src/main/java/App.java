
public class App {
    public static void main(String[] args) {
        String url = "";
        Scraper scraper = new Scraper(url);
        scraper.getSongs();
    }
}
