import org.apache.commons.cli.*;

public class App {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "Show this help message and exit");
        options.addOption("i", "images", false, "Download the images after the album if available");

        CommandLineParser defaultParser = new DefaultParser();
        String albumURL = "";
        boolean imagesFlag = false;

        try {
            CommandLine cmd = defaultParser.parse(options, args);

            if (cmd.hasOption("h") || args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar khinsider-downloader.jar [-h] [-i] [albumURL]", options);
                System.exit(0);
            }

            if (cmd.hasOption("i")) {
                imagesFlag = true;
            }

            albumURL = cmd.getArgs()[0];

            if (!albumURL.startsWith("https://downloads.khinsider.com/")) {
                System.out.println("Invalid Album URL");
                System.exit(1);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Scraper scraper = new Scraper(albumURL);
        scraper.downloadAlbum();

        if (imagesFlag) {
            scraper.downloadImages();
        }
    }
}
