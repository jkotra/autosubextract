package autosubextract;

import java.nio.file.Path;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import lombok.Data;

@Data
class Subtitle {

	private Integer position;
	private String title;
	private long size;
	private String language;

	public void extract(Path file, Path output) throws Exception {

		FFmpeg.atPath().setLogLevel(LogLevel.FATAL).setOverwriteOutput(true).addInput(UrlInput.fromPath(file)).addArguments("-c:s", "text")
				.addArguments("-map", String.format("0:%d", this.position)).addOutput(UrlOutput.toPath(output))
				.execute();

	}

}
