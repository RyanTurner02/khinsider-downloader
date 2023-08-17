# khinsider-downloader
A web scraper for downloading albums from [KHInsider](https://downloads.khinsider.com/).

## Prerequisites
Java 17 or above.

## Installation
Download the latest version from the [Releases](https://github.com/RyanTurner02/khinsider-downloader/releases) page.

## Usage
Once the latest version is downloaded, run the `.jar` file through the command line and pass an album's URL as an argument if no additional arguments are being passed.

`java -jar khi-dl.jar [albumURL]`

Additional arguments can be passed when running the `.jar` file through the command line. The album's URL will need to be the last argument when including any additional arguments.

```
usage: java -jar khinsider-downloader.jar [-c] [-h] [-i] [-p] [albumURL]
 -c,--counter    Display a counter next to each song and image when
                 downloading
 -h,--help       Show this help message and exit
 -i,--indices    Add an index number to the file name of each song when
                 downloading
 -p,--pictures   Download the images alongside the album if available
```
