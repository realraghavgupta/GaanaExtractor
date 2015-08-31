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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class MainExtractor {
	static final Logger logger = LogManager.getLogger(MainExtractor.class.getName());
	static List<String> data404 = new ArrayList<String>();
	static List<String> fileError = new ArrayList<String>();

	public static void main(String[] args) {
		logger.entry();
		boolean toAlbumFolder = Boolean.valueOf(args[1]);
		extract(args[0], toAlbumFolder);
		System.out.println("File Data Not Found : " + data404.stream().collect(Collectors.joining(";")));
		System.out.println("File Read Error : " + fileError.stream().collect(Collectors.joining(";")));
		logger.exit();
	}

	private static void extract(String srcPath, boolean toAlbumFolder) {
		// srcPath = "workspace";
		String trgtFolder = "converted";
		String trgtPath = srcPath + ((srcPath.endsWith("/") | srcPath.endsWith("\\")) ? "" : "/") + trgtFolder;
		logger.trace("Into Extract");
		Map<Integer, Path> fileIds = new HashMap<Integer, Path>();
		fileIds = getFileIds(srcPath);
		JSONObject songMeta = getSongsDetail(fileIds);
		if (songMeta != null) {
			// System.out.println(JsonPath.read(songMeta,
			// "$.tracks[0].track_id"));
			copyConvert(fileIds, songMeta, trgtPath, toAlbumFolder);
		}

		logger.trace("Exit Extract");
	}

	private static void copyConvert(Map<Integer, Path> fileIds, JSONObject songMeta, String trgtPath,
			boolean toAlbumFolder) {
		logger.trace("Into copyConvert");
		for (Entry<Integer, Path> fileEntry : fileIds.entrySet()) {
			String fileName = fileEntry.getKey().toString();
//			System.out.println(fileEntry.getValue().toAbsolutePath());
			File srcFile = fileEntry.getValue().toFile();
			try {
				AudioFile f = AudioFileIO.readMagic(srcFile);
				Tag tag = f.getTag();

				String strBasePath = "$.tracks[?(@.track_id==" + fileName + ")].";
//				System.out.println(strBasePath);
//				System.out.println(songMeta.toJSONString());
//				System.out.println(strBasePath + "album_title");

				JSONArray arrFileName = (JSONArray) JsonPath.read(songMeta, strBasePath + "track_title");
				if (!arrFileName.isEmpty()) {
					String strFileName = (String) (arrFileName).get(0);
					strFileName = FileNameCleaner.cleanFileName(strFileName);

					String trgFolderPath = trgtPath;

					if (toAlbumFolder) {
						String strFolderName = (String) ((JSONArray) JsonPath.read(songMeta,
								strBasePath + "album_title")).get(0);
						strFolderName = FileNameCleaner.cleanFileName(strFolderName);

						trgFolderPath = trgFolderPath + "/" + strFolderName;
					}
					File trgtFolder = new File(trgFolderPath);
					if (!trgtFolder.exists()) {
						trgtFolder.mkdirs();
					}

					String trgtFilePath = trgtFolder.getAbsolutePath() + "/" + strFileName;
					String strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "album_title")).get(0);
					tag.setField(FieldKey.ALBUM, strData);

					strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "track_title")).get(0);
					tag.setField(FieldKey.TITLE, strData);

					strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "lyrics_url")).get(0);
					tag.setField(FieldKey.URL_LYRICS_SITE, strData);

					strData = (String) ((JSONArray) JsonPath.read(songMeta, strBasePath + "artwork")).get(0);
					byte[] strImgData = getByteArray(strData);
					Artwork artwork = ArtworkFactory.getNew();
					artwork.setBinaryData(strImgData);
					tag.setField(artwork);

					AudioFileIO.writeAs(f, trgtFilePath);
				} else {
//					System.out.println("File Data Not Found : " + fileName);
					data404.add(fileName);
				}

			} catch (IOException | IllegalArgumentException | SecurityException | CannotWriteException
					| CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
				// TODO Auto-generated catch block
				fileError.add(fileName);
				e.printStackTrace();
			}

		}
		logger.trace("Exit copyConvert");
	}

	private static JSONObject getSongsDetail(Map<Integer, Path> fileIds) {
		logger.trace("Into getSongsDetail");
		String endPoint = "http://api.gaana.com/";
		JSONObject songsDetail = new JSONObject();
		JSONObject songsDetailFull = new JSONObject();
		JSONArray tracks = new JSONArray();
		JSONArray tracksFull = new JSONArray();
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "song");
		params.put("subtype", "song_detail");
		if (!fileIds.isEmpty()) {
			int i = 0;
			String strFileIds = "";
			for (Integer fileId : fileIds.keySet()) {
				i = i + 1;
				if (strFileIds == "") {
					strFileIds = fileId.toString();
				} else {
					strFileIds = strFileIds + "," + fileId.toString();
				}
				if (i == 10) {
					params.put("track_id", strFileIds);
					songsDetail = getSongsDetail(endPoint, params);
					tracks = (JSONArray) songsDetail.get("tracks");
					if(tracks != null && !tracks.isEmpty()){
					tracksFull.addAll(tracks);
					}
					i = 0;
					strFileIds = "";
				}
			}
			if (!strFileIds.isEmpty()) {
				params.put("track_id", strFileIds);
				songsDetail = getSongsDetail(endPoint, params);
				tracks = (JSONArray) songsDetail.get("tracks");
				if(tracks != null && !tracks.isEmpty()){
				tracksFull.addAll(tracks);
				}
				i = 0;
				strFileIds = "";
			}

			songsDetailFull.put("tracks", tracksFull);
			return songsDetailFull;
		}
		logger.trace("Exit getSongsDetail");
		return null;
	}

	public static JSONObject getSongsDetail(String endPoint, Map<String, String> params) {
		try {
			String songMeta = HTTPHelper.sendGet(endPoint, params);
			System.out.println(songMeta);
			logger.trace("Exit getSongsDetail");
			return (JSONObject) JSONValue.parse(songMeta);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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

	private static byte[] getByteArray(String strURL) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			URL url = new URL(strURL);
			is = url.openStream();
			byte[] byteChunk = new byte[4096]; // Or whatever size you want to
			// read in at a time.
			int n;

			while ((n = is.read(byteChunk)) > 0) {
				baos.write(byteChunk, 0, n);
			}
			return (baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}