package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import RegexParser.RegexNode;
import RegexParser.RegexParserProvider;
import automata.sfa.SFA;
import regexconverter.RegexConverter;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class AutomataTutorConverter {
	public static void main(String[] args) {
		String csvFile = "/Users/lorisdanto/automatatutor-data/regular-expression/regular-expression.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\"";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			int c = 0;
			while ((line = br.readLine()) != null) {
				c++;

				if (c > 7104) {
					System.out.println(c);
					// use comma as separator
					String[] lineElements = line.split(cvsSplitBy);

					String[] corA = new String[1];
					corA[0] = lineElements[lineElements.length-3];
					String[] wronA = new String[1];
					wronA[0] = lineElements[lineElements.length-1];
					List<RegexNode> correct = RegexParserProvider.parse(corA);
					List<RegexNode> wrong = RegexParserProvider.parse(wronA);

					RegexNode correctReg = correct.get(0);
					RegexNode wrongReg = wrong.get(0);

					UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
					SFA<CharPred, Character> sfaCorrect = RegexConverter.toSFA(correctReg, solver);
					SFA<CharPred, Character> sfaWrong = RegexConverter.toSFA(wrongReg, solver);

					SFA<CharPred, Character> sfaTruePositive = sfaWrong.intersectionWith(sfaCorrect, solver).determinize(solver).minimize(solver);
					SFA<CharPred, Character> sfaFalsePositive = sfaWrong.minus(sfaCorrect, solver).determinize(solver).minimize(solver);
					SFA<CharPred, Character> sfaFalseNegative = sfaCorrect.minus(sfaWrong, solver).determinize(solver).minimize(solver);

					List<String> positive = new LinkedList<>();
					List<String> negative = new LinkedList<>();

					int howMany = 6;
					if (!sfaFalsePositive.isEmpty()) {
						try (Writer writer = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream("/Users/lorisdanto/regex-data/test" + c + ".txt"), "utf-8"))) {

							writer.write(corA[0] + "\n");
							writer.write(wronA[0] + "\n");
							if (!sfaTruePositive.isEmpty())
								for (List<Character> s : sfaTruePositive.getWitnesses(solver, howMany)){
									writer.write(solver.stringOfList(s) + "\n");
								}
							if (!sfaFalseNegative.isEmpty())
								for (List<Character> s : sfaFalseNegative.getWitnesses(solver, howMany))
									writer.write(solver.stringOfList(s) + "\n");

							for (List<Character> s : sfaFalsePositive.getWitnesses(solver, howMany*2))
								writer.write(solver.stringOfList(s) + "\n");
						}
					}
				}
			}

		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
