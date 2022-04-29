package autosubextract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AutoSubExtractTests {
	
	File sub = new File("src/test/resources/test_muxed.srt");
	Path p = Paths.get("src/test/resources/test_muxed.mkv");

	@BeforeEach
	void setUp() throws Exception {
		// remove if subtitle .srt file exists.

		if(sub.exists()) {
			sub.delete();
		}
		
	}

	@Test
	void TestExtraction() throws Exception {

		Subtitle s = new Subtitle();
		s.setPosition(1);
		s.extract(p, sub.toPath());
		assertTrue(sub.exists());
		
	}

}
