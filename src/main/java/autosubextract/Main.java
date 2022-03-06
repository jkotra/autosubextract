package autosubextract;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	
	private static final Option ARG_INPUT = new Option("i", "input", true, "Video Input.");
	private static final Option ARG_LANGUAGE = new Option("l", "language", true, "language of the subtitle to extract.");
	private static final Option ARG_ALL = new Option(null, "all", false, "Process all video files on current directory.");
	private static final Option ARG_PRINT_HELP = new Option("h", "help", false, "Print Help and Exit.");
	
	public static String getVersion() {
		return "0.1";
	}
	
	public static String getEmail() {
		return "jagadeesh@stdin.top";
	}
	
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.out);
		pw.printf("AutoSubExtract v.%s\n", getVersion());
		pw.printf("Jagadeesh Kotra <%s>\n", getEmail());
		formatter.printUsage(pw, 100, String.format("java -jar autosubextract-%s-shaded.jar -i movie.mkv [-l eng] [--all]", getVersion()));
		formatter.printOptions(pw, 100, options, 4, 4);
		pw.close();
		System.exit(0);
	}

	public static void main(String[] args) throws IOException, ParseException {
		
		CommandLineParser clp = new DefaultParser();
		Options opts = new Options();
		opts.addOption(ARG_INPUT);
		opts.addOption(ARG_LANGUAGE);
		opts.addOption(ARG_ALL);
		opts.addOption(ARG_PRINT_HELP);
		CommandLine cl = clp.parse(opts, args);
		
		if (cl.hasOption(ARG_PRINT_HELP)) { printHelp(opts); };

		if (!cl.hasOption(ARG_INPUT) && !cl.hasOption(ARG_ALL)) {
			System.err.println("[Error] Input / --all is not provided.");
			printHelp(opts);
			System.exit(1);
		}
		
		
		if (cl.hasOption(ARG_INPUT) && !cl.hasOption(ARG_ALL)) {
			Path input = Paths.get(cl.getOptionValue(ARG_INPUT));
			JaffreeUtils.extractSubF(input, cl.getOptionValue(ARG_LANGUAGE));
		}
		else if (cl.hasOption(ARG_ALL)) {

			File[] input = Paths.get(System.getProperty("user.dir")).toFile().listFiles(new VideoFilter());
			for(File f : input) {
				Path p = Paths.get(f.getAbsolutePath());
				JaffreeUtils.extractSubF(p, cl.getOptionValue(ARG_LANGUAGE, "eng"));
			}
			
		}

        
	}

}
