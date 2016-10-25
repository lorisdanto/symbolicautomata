package benchmark.regexconverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import RegexParser.RegexParserProvider;

/**
 * 
 * @author Fang Wang
 *
 *
 * This program read the input file line by line and 
 * direct the lines that can be parsed into a new file.
 * 
 * How to use: 
 * Two command line args, first for input, second for output
 * e.g. 
 * test/RegexParser/regexlib-clean.txt test/RegexParser/regexlib-filtered.txt
 * 
 * regexlib.txt is the regexlib-clean.txt in the regex folder 
 */
public class ExtractPattern {
	static FileReader inFile;
	private static PrintWriter outFile;
	public static RegexParserProvider test;

	public static void main(String args[]) {

		// check for command-line args
		if (args.length == 2) {
			System.out.println("the input file is " + args[0] + "\nthe parsable output file is " + args[1]);
		} else {
			System.err.println("For file input/output, please supply path of file to be filtered in first arg "
					+ "and path of the output in the second arg.");
			System.exit(-1);
		}
		// open input file
		if (args.length == 2) {
			try {
				inFile = new FileReader(args[0]);
			} catch (FileNotFoundException ex) {
				System.err.println("File " + args[0] + " not found.");
				System.exit(-1);
			}
			// open output file
			outFile = null;
			try {
				outFile = new PrintWriter(args[1]);
			} catch (FileNotFoundException ex) {
				System.err.println("File " + args[1] + " could not be opened for writing.");
				System.exit(-1);
			}
		}

		try (BufferedReader br = new BufferedReader(inFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				// process the line.
				if(line.contains("0-9")){
					outFile.println(line);
				}
			}
			System.out.println("Filter finished.");
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
