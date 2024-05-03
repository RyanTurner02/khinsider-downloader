# khinsider-downloader
A web scraper for downloading albums from [KHInsider](https://downloads.khinsider.com/).

## Prerequisites
Java 17 or above.

## Installation
Download the latest version from the [Releases](https://github.com/RyanTurner02/khinsider-downloader/releases) page.

## Usage
Run the `.jar` file through the command line and pass an album's URL as an argument if no additional arguments are being passed.

`java -jar khinsider-downloader.jar [albumURL]`

Additional arguments can be passed when running the `.jar` file through the command line.

```
usage: java -jar khinsider-downloader.jar [-h] [-i] [albumURL]
 -h,--help     Show this help message and exit
 -i,--images   Download the images after the album if available
```

**Note:** While running the program, you might encounter a prompt asking for a file type. Simply input the corresponding number and press `Enter` to proceed.
