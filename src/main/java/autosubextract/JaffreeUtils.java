package autosubextract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;

class VideoFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if (name.endsWith(".mkv") || name.endsWith(".mp4")) {
			return true;
		}
		return false;
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

	public static void extractSubF(Path input, String language, Boolean all) throws Exception {

		System.out.println("[INFO] Processing: " + input.toString());

		ArrayList<Subtitle> candidates = new ArrayList<>();

		FFprobeResult results = FFprobe.atPath().setLogLevel(LogLevel.QUIET).setShowStreams(true)
				.setInput(input.toString()).execute();

		for (Stream stream : results.getStreams()) {

			if (stream.getCodecType() == StreamType.SUBTITLE) {

				String sub_lang = stream.getTag("language");
				if (sub_lang == null) {
					continue;
				}

				if (sub_lang.equals(language)) {
					Subtitle sub = new Subtitle();
					sub.setPosition(stream.getIndex());
					if (stream.getTag("NUMBER_OF_BYTES") != null) {
						sub.setSize(Long.parseLong(stream.getTag("NUMBER_OF_BYTES")));
					}

					sub.setTitle(stream.getTag("title"));
					sub.setLanguage(stream.getTag("language"));
					candidates.add(sub);
				}

			}

		}

		System.out.println("[INFO] Matched Subtiles: " + candidates.size());

		if (candidates.size() == 1) {
			candidates.get(0).extract(input, replaceExt(input));
		} else {

			while (true) {

				candidates.forEach((s) -> {
					System.out.println(s.toString());
				});

				System.out.print("\n=>Enter Position: ");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					int choice = Integer.parseInt(reader.readLine());
					for (Subtitle s : candidates) {
						if (s.getPosition() == choice) {
							s.extract(input, replaceExt(input));
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				if (all) {
					break;
				}

			}

		}

	}

}
