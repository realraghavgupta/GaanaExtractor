package in.pathri.gaanaextractor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jayway.jsonpath.JsonPath;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class MainExtractor {
	static final Logger logger = LogManager.getLogger(MainExtractor.class.getName());

	public static void main(String[] args) {
		logger.entry();
		extract(args[0]);
		logger.exit();
	}

	private static void extract(String srcPath) {
		// srcPath = "workspace";
		String trgtFolder = "converted";
		String trgtPath = srcPath + ((srcPath.endsWith("/") | srcPath.endsWith("\\")) ? "" : "/") + trgtFolder;
		logger.trace("Into Extract");
		Map<Integer, Path> fileIds = new HashMap<Integer, Path>();
		fileIds = getFileIds(srcPath);
		JSONObject songMeta = getSongsDetail(fileIds);
		if (songMeta != null) {
			System.out.println(JsonPath.read(songMeta, "$.tracks[0].track_id"));
			copyConvert(fileIds, songMeta, trgtPath);
		}

		logger.trace("Exit Extract");
	}

	private static void copyConvert(Map<Integer, Path> fileIds, JSONObject songMeta, String trgtPath) {
		logger.trace("Into copyConvert");
		for (Entry<Integer, Path> fileEntry : fileIds.entrySet()) {
			String fileName = fileEntry.getKey().toString();
			System.out.println(fileEntry.getValue().toAbsolutePath());
			File srcFile = fileEntry.getValue().toFile();
			try {
				Mp3File mp3file = new Mp3File(srcFile);
				ID3v2 id3v2Tag;
				if (mp3file.hasId3v2Tag()) {
					id3v2Tag = mp3file.getId3v2Tag();
				} else {
					// mp3 does not have an ID3v2 tag, let's create one..
					id3v2Tag = new ID3v24Tag();
					mp3file.setId3v2Tag(id3v2Tag);
				}
				// id3v2Tag.set
				String strBasePath = "$.tracks[?(@.track_id==" + fileName + ")].";
				System.out.println(strBasePath);
				System.out.println(songMeta.toJSONString());
				System.out.println(strBasePath + "album_title");
				String strAlbum = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "album_title"))
						.get(0);
				String strFileName = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "track_title"))
						.get(0);
				String trgFolderPath = trgtPath + "/" + strAlbum;				
				File trgtFolder = new File(trgFolderPath);
				if (!trgtFolder.exists()) {
					trgtFolder.mkdirs();
				}
				String trgtFilePath = trgtFolder.getAbsolutePath() + "/" + strFileName + ".mp3";
				String strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "album_title")).get(0);
				id3v2Tag.setAlbum(strData);
				
				strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "track_title")).get(0);
				id3v2Tag.setTitle(strData);

				strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "lyrics_url")).get(0);
				id3v2Tag.setUrl(strData);
				
//				JSONArray arrArtist = (JSONArray) ((JSONArray) JsonPath.read(songMeta, strBasePath + "artist")).get(0);
//				String artists = "";
//				for (Object objArtist : arrArtist) {
//					JSONObject jsonArtist = (JSONObject)objArtist;
//					String artist = jsonArtist.getAsString("name");
//					if(artists.isEmpty()){
//						artists = artist;
//					}else{
//						artists = artists + "/" + artist;
//					}
//				}
//				id3v2Tag.setAlbumArtist(artists);

				strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "artwork")).get(0);
				byte[] strImgData = getByteArray(strData);
				id3v2Tag.setAlbumImage(strImgData, ".mp3");;
				mp3file.save(trgtFilePath);
			} catch (UnsupportedTagException | InvalidDataException | IOException | NotSupportedException
					| IllegalArgumentException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		logger.trace("Exit copyConvert");
	}


	private static JSONObject getSongsDetail(Map<Integer, Path> fileIds) {
		logger.trace("Into getSongsDetail");
		String endPoint = "http://api.gaana.com/";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "song");
		params.put("subtype", "song_detail");
		String songIds = "";
		if (!fileIds.isEmpty()) {
			songIds = fileIds.keySet().stream().map(Object::toString).collect(Collectors.joining(","));
			params.put("track_id", songIds);
			try {
				String songMeta = HTTPHelper.sendGet(endPoint, params);
				System.out.println(songMeta);
				logger.trace("Exit getSongsDetail");
				return (JSONObject) JSONValue.parse(songMeta);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		logger.trace("Exit getSongsDetail");
		return null;
	}

	public static Map<Integer, Path> getFileIds(String path) {
		logger.trace("Enter getFileIds");
		final Map<Integer, Path> fileIds = new HashMap<Integer, Path>();
		Path p = Paths.get(path);
		FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.getFileName().toString().matches("\\d+")) {
					// System.out.println(file.toString());
					Integer id = Integer.parseInt(file.getFileName().toString());
					Path path = file;
					fileIds.put(id, path);
				} else {
					// logger.info("Non Numberic File", file.toString());
				}
				logger.trace("Exit getFileIds");
				return FileVisitResult.CONTINUE;
			}
		};

		try {
			Files.walkFileTree(p, fv);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.trace("Exit getFileIds");
		return fileIds;

	}
	
	private static byte[] getByteArray(String strURL){

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			URL url = new URL(strURL);
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }
		  return(baos.toByteArray());
		}
		catch (IOException e) {
		  e.printStackTrace ();
		}
		finally {
		  if (is != null) { try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
		}
		return null;
	}
}