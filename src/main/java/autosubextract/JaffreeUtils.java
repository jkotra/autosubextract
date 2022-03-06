package autosubextract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;

class VideoFilter implements FilenameFilter{
	
	@Override
	public boolean accept(File dir, String name) {
		if( name.endsWith(".mkv") || name.endsWith(".mp4") ) {
			return true;
		}
		return false;
	}
	
}

class Subtitle {

	public Integer position;
	public String title;
	public long size;
	public String language;

	@Override
	public String toString() {
		return "Subtitle [position=" + position + ", title=" + title + ", size=" + size + ", language=" + language
				+ "]";
	}

}

public class JaffreeUtils {

	private static Path replaceExt(Path input) {

		String[] original = input.toString().split("\\.");
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < original.length - 1; i++) {
			output.append(original[i] + ".");
		}

		output.append("srt");

		return Paths.get(output.toString());
	}

	public static void extractSubF(Path input, String language) {
		
		System.out.println("[INFO] Processing: " + input.toString());

		int sub_counter = 0;
		ArrayList<Subtitle> candidates = new ArrayList<>();

		FFprobeResult results = FFprobe.atPath().setShowStreams(true).setInput(input.toString()).execute();

		for (Stream stream : results.getStreams()) {
			
			if (stream.getCodecType() == StreamType.SUBTITLE) {
				
				/*
				 * System.out.println("ln" + stream.getCodecLongName());
				 * System.out.println("tag: " + stream.getTag("language"));
				*/

				String sub_lang = stream.getTag("language");
				if (sub_lang == null) {
					continue;
				}

				if (sub_lang.equals(language)) {
					Subtitle sub = new Subtitle();
					sub.position = sub_counter;
					if (stream.getTag("NUMBER_OF_BYTES") != null) {
						sub.size = Long.parseLong(stream.getTag("NUMBER_OF_BYTES"));
					}
					sub.title = stream.getTag("title");
					sub.language = stream.getTag("language");
					candidates.add(sub);
				}

				sub_counter++;

			}

		}
		
		System.out.println("[INFO] Matched Subtiles: " + candidates.size());

		if (candidates.size() > 0 && candidates.size() == 1) {
			extractSub(input, candidates.get(0).position);
		} else {
			
			candidates.forEach((s) -> {
				System.out.println(s.toString());
			});
			

			System.out.print("\n=>Enter Position: ");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(System.in));
			Integer choice = 0;
			try {
				choice = Integer.parseInt(reader.readLine());
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < candidates.size(); i++) {
				
				if (choice == candidates.get(i).position) {
					extractSub(input, choice);
				}
				
			}

		}

	}

	public static void extractSub(Path input, Integer id) {

		System.out.println("Extracting Stream: " + id);
		Path output = replaceExt(input);
		
		if (output.toFile().exists()) {
			System.err.println("[Error]: File Already Exists!");
			return;
		}

		try {
			FFmpeg.atPath().setOverwriteOutput(true).addInput(UrlInput.fromPath(input))
			.addArguments("-c:s", "text")
			.addArguments("-map", String.format("0:s:%d", id))
			.addOutput(UrlOutput.toPath(output)).execute();
			
			System.out.println("[OK] " + output.toString());
			}
		catch (Exception e) {
			System.err.println("[Error]: " + e.getMessage());
		}

	}

}
