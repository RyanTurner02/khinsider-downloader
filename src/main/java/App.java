import org.apache.commons.cli.*;

public class App {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "Show this help message and exit");
        options.addOption("c", "counter", false, "Display a counter next to each song when downloading");
        options.addOption("i", "indices", false, "Add an index number to the file name of each song when downloading");

        CommandLineParser defaultParser = new DefaultParser();
        String albumURL = "";
        boolean countOption = false;
        boolean indicesOption = false;

        try {
            CommandLine cmd = defaultParser.parse(options, args);

            if (cmd.hasOption("h") || args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar khi-dl.jar [-h | --help] [-c | --counter] [-i | --indices] [albumURL]", options);
                System.exit(0);
            }

            if (cmd.hasOption("c")) {
                countOption = true;
            }

            if (cmd.hasOption("i")) {
                indicesOption = true;
            }

            // check if the user entered the album url last
            albumURL = args[args.length - 1];

            if (!albumURL.startsWith("https://downloads.khinsider.com/")) {
                System.out.println("Invalid Album URL");
                System.exit(1);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Scraper scraper = new Scraper(albumURL, countOption, indicesOption);
        scraper.downloadAlbum();
    }
}
