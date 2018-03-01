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
import theory.intervals.UnaryCharIntervalSolver;
import strings.EditDistance;

public class TestRegexCorrection {
	public static void main(String[] args) {
		try {
			Files.walk(Paths.get("../automatark/regex/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)
						&& (filePath.toString().endsWith("edit-distance-benchmark.re"))) {
					try {
						List<RegexNode> nodes = RegexParserProvider.parse(new FileReader(filePath.toFile()));
						int i = 1;
						for (RegexNode node : nodes) {
							// Counter++;
							UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
							SFA<CharPred, Character> sfa = RegexConverter.toSFA(node, solver);
							SFA<CharPred, Character> sfa_comp = sfa.complement(solver);
							List<Character> sampleChrList = sfa.getWitness(solver);
							if (sampleChrList == null || sampleChrList.size() <= 0) {
								i++;
								continue;
							}
							StringBuilder sb = new StringBuilder();
							for (Character c : sampleChrList) {
								sb.append(c);
							}
							String sampleStr = sb.toString();
							long startTime = System.nanoTime();
							System.out.println("Start computing edit distance...");
							String resultStr = EditDistance.getCorrectString(sfa_comp, sampleStr);
							long endTime = System.nanoTime();
							System.out.println(i++ + "[" + sampleStr + "] [" + resultStr + "]" + ((endTime - startTime) / (double)1000000000));
						}
						// Counter=0;
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