package benchmark;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import RegexParser.RegexNode;
import RegexParser.RegexParserProvider;
import automata.sfa.SFA;
import benchmark.regexconverter.RegexConverter;
import theory.BooleanAlgebra;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;;

public class TestRegexConverter {
	public static <A, B> void Run() {
		try {
			Files.walk(Paths.get("../automatark/regex/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath) && (filePath.toString().endsWith(".re") || filePath.toString().endsWith(".txt"))) {
					try {
						List<RegexNode> nodes = RegexParserProvider.parse(new FileReader(filePath.toFile()));
						System.out.println(filePath);
						for (RegexNode node : nodes) {
							//Counter++;
							UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
							SFA<CharPred, Character> sfa =RegexConverter.toSFA(node, solver);
						}
						//Counter=0;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

