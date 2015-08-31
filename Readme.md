Gaana Extractor

A Java tool to convert songs downloaded using Gaana App to MP3 files.

Gaana:

Gaana is a music streaming free service (http://gaana.com/). There are also mobile Apps that allow users to listen to music for free in their mobiles. 

The mobile App features unlimited download of songs for a small amount of fee. The catch here is that the downloaded songs can be listed through the Gaana App only. 

Using this utility, these downloaded songs can be converted to audio files with proper tags
Also there is an option to organise the songs into folders named under their Album names

Command:

java -jar GaanaExtractor-<version>.jar param1 param2

param1 = The path of the folder having the downloaded songs
param2 = true : Organise songs into folders named under their Album names
		 false : No Organisation

Usage:

0. Download the JAR file from the binaries folder.
1. Retrieve the downloaded songs from your mobile and copy to a specific folder. Lets assume the folder structure to be 'C:\Gaana\Songs\' [Ways to do this coming soon..]
2. Place the GaanaExtractor-<version>.jar in the parent folder 'C:\Gaana\'
3. Open a command prompt and navigate to 'C:\Gaana\'
4. Run the following command (Note the change in direction of slashes)
    
    java -jar GaanaExtractor-<version>.jar "C:/Gaana/Songs/" true
    
5. You can find the converted files in the path 'C:\Gaana\Songs\converted'
6. Enjoy the songs!

Note : Internet connected is required for this utility to get the song details from the Gaana Server.

CHANGELOG:

v1.2:
- Now mp3 and mp4(m4a/aac) formats are supported.
- Will skip the songs for which there are no details in DB and prompt the skipped the files in the end.
- Will skip the files for which errors happened and prompt the skipped the files in the end.
- Added an option for selecting whether files need to be organised based on Album names

v1.1:
- Fixed the inability to handle more than 10 songs in a single run.

v1.0:
- Initial Release

[![Join the chat at https://gitter.im/PathriK/GaanaExtractor](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PathriK/GaanaExtractor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)