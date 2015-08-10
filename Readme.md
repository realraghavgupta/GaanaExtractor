Gaana Extractor

A Java tool to convert songs downloaded using Gaana App to MP3 files.

Gaana:

Gaana is a music streaming free service (http://gaana.com/). There are also mobile Apps that allow users to listen to music for free in their mobiles. 

The mobile App features unlimited download of songs for a small amount of fee. The catch here is that the downloaded songs can be listed through the Gaana App only. 

Using this utility, these downloaded songs can be converted to universal music format - MP3.

Usage:

0. Download the JAR file from the binaries folder.
1. Retrieve the downloaded songs from your mobile and copy to a specific folder. Lets assume the folder structure to be 'C:\Gaana\Songs\' [Ways to do this coming soon..]
2. Place the GaanaExtractor-<version>.jar in the parent folder 'C:\Gaana\'
3. Open a command prompt and navigate to 'C:\Gaana\'
4. Run the following command (Note the change in direction of slashes)
    
    java -jar GaanaExtractor-<version>.jar "C:/Gaana/Songs/"
    
5. You can find the converted files in the path 'C:\Gaana\Songs\converted'
6. Enjoy the songs!

Note : Internet connected is required for this utility to get the song details from the Gaana Server.


[![Join the chat at https://gitter.im/PathriK/GaanaExtractor](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PathriK/GaanaExtractor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)