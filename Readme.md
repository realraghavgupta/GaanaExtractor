[![Join the chat at https://gitter.im/PathriK/GaanaExtractor](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PathriK/GaanaExtractor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Build Status](https://travis-ci.org/PathriK/GaanaExtractor.svg?branch=master)](https://travis-ci.org/PathriK/GaanaExtractor) [![Build status](https://ci.appveyor.com/api/projects/status/f14f957cxmy8d6l1?svg=true)](https://ci.appveyor.com/project/PathriK/gaanaextractor)

<pre>
<b>IMPORTANT NOTE:</b>
The latest veersion of Gaana Mobile App has updated the way they handle downloads. 
The files that are generated having the extension '.e' <b>DOES NOT WORK</b> with this utility.

The only workaround is to use an APK (Google should help) to install an older version of Gaana.
</pre>

<pre>
<b>Update:</b>
    Have released a new tool named <a href="https://github.com/PathriK/GaanaDownloader">GaanaDownloader</a>.
    Using that we would be able to Search and Download songs from Gaana. :)
    <i><b>Note:</b> Gaana Plus subscription is required </i>
</pre>

# Gaana Extractor

A Java tool to convert songs downloaded using Gaana App to MP3 files.

## Gaana:

[Gaana](http://gaana.com/) is a music streaming free service. There are also mobile Apps that allow users to listen to music for free in their mobiles. 

The mobile App features unlimited download of songs for a small amount of fee. The catch here is that the downloaded songs can be listed through the Gaana App only. 

Using this utility, these downloaded songs can be converted to audio files with proper tags
Also there is an option to organise the songs into folders named under their Album names

## Command:

    java -jar GaanaExtractor-<version>.jar [srcDir] [shldOrganise]

| Parameter    | Required | Type    | Description                                                                              				| Default Value                        |
|--------------|----------|---------|-------------------------------------------------------------------------------------------------------|--------------------------------------|
| srcDir       | Optional | String  | The path of the folder having the downloaded songs                                       				| The folder where jar file is present |
| shldOrganise | Optional | Boolean | **true** : Organise songs into folders named under their Album names <br> **false** : No Organisation | True                                 |

## Usage:

0. Download the latest JAR.
1. Retrieve the downloaded songs from your mobile and copy to a specific folder. Lets assume the folder structure to be **C:\\Gaana\\Songs\\** [Ways to do this coming soon..]
2. Place the **GaanaExtractor-<version>.jar** in the parent folder **C:\\Gaana\\**
3. Open a command prompt and navigate to **C:\\Gaana\\**
4. Run the following command (Direction of slash can be anything)
    
    `java -jar GaanaExtractor-<version>.jar "C:/Gaana/Songs/" true`
    
5. You can find the converted files in the path **C:\Gaana\Songs\converted**
6. Enjoy the songs!

**Note1** : Internet connected is required for this utility to get the song details from the Gaana Server.

**Note2** : If there is any error during conversion, please share the log file with me. The log file can be found in the same directory as that of the jar file. It will be under the name **GaanaExtractor.log**. If there was no error during conversion, the log file can be deleted.

## Downloads:

Releases:

https://github.com/PathriK/GaanaExtractor/releases

Latest Snapshot (v2.6): 

https://ci.appveyor.com/api/projects/PathriK/GaanaExtractor/artifacts/binaries/GaanaExtractor-2.6-SNAPSHOT.jar

## Contact:
Gmail: pathrikumark@gmail.com

Gitter: https://gitter.im/PathriK/GaanaExtractor

## CHANGELOG:

v2.5: HTTPHelper modification to remove logging (password) for 'user.php' (login) requests

v2.4: GET Header modification for fixing download request giving encrypted data

v2.3: GET Header modification for inclusion of more search results

v2.2: Minor changes

v2.1: Minor changes

v2: Multiple Fixes and More Tag Support

v2.0-BETA: Nearly every song should get converted now 
- Fixed issue which was causing conversion to fail for lot of songs 
- Tidy up the console log. It's now more crisp and clear 
- Now the tool creates a detailed log file in the location from which the jar is run.
- Made Parameters optional with default values 
- Now the source directory parameter accepts both windows and unix file path string 

v1.2:
- Now mp3 and mp4(m4a/aac) formats are supported.
- Will skip the songs for which there are no details in DB and prompt the skipped the files in the end.
- Will skip the files for which errors happened and prompt the skipped the files in the end.
- Added an option for selecting whether files need to be organised based on Album names

v1.1:
- Fixed the inability to handle more than 10 songs in a single run.

v1.0:
- Initial Release
