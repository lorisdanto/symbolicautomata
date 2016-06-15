package benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;

import RegexParser.RegexListNode;
import RegexParser.RegexParserProvider;
import automata.safa.SAFA;
import automata.sfa.SFA;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class RunIntersectionExp {
	static FileReader inFile;

	public static void main(String[] args) throws TimeoutException {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
		UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
		try {
			inFile = new FileReader(args[0]);
		} catch (FileNotFoundException ex) {
			System.err.println("File " + args[0] + " not found.");
			System.exit(-1);
		}

		try (BufferedReader br = new BufferedReader(inFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String regex : list) {
			safaList.add((new SFAprovider(regex, solver)).getSFA().getSAFA(solver));
		}
		// ArrayList<SAFA<CharPred, Character>> temp = new
		// ArrayList<SAFA<CharPred, Character>>();
		// temp.add(safaList.get(0));
		// temp.add(safaList.get(1));
		// temp.add(safaList.get(2));
		// temp.add(safaList.get(3));
		// temp.add(safaList.get(5));
		//
		// System.out.println(hasIntersection(solver, temp));

		//System.out.println(hasIntersection(solver, safaList));
		for (int q = 0; q < safaList.size() - 5; q++) {
			for (int i = q + 1; i < safaList.size() - 4; i++) {
				for (int j = i + 1; j < safaList.size() - 3; j++) {
					for (int k = j + 1; k < safaList.size() - 2; k++) {
						for (int m = k + 1; m < safaList.size() - 1; m++) {
							for (int n = m + 1; n < safaList.size(); n++) {
								ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
								temp.add(safaList.get(q));
								temp.add(safaList.get(i));
								temp.add(safaList.get(j));
								temp.add(safaList.get(k));
								temp.add(safaList.get(m));
								temp.add(safaList.get(n));

								boolean hasintersect = hasIntersection(solver, temp);
							    System.out.print(hasintersect+"  ");
							    System.out.println(q + " " + i + " " + j + " " + k + " " + m + " " + n);
//								if (hasintersect) {
//									System.out.println(hasintersect);
//									System.out.println(q + "\n" + i + "\n" + j + "\n" + k + "\n" + m + "\n" + n);
//									// System.exit(-1);
//								}
							}
						}
					}
				}
			}
		}

	}

	private static boolean hasIntersection(UnaryCharIntervalSolver solver,
			ArrayList<SAFA<CharPred, Character>> safaList) throws TimeoutException {
		SAFA<CharPred, Character> result;
		if (safaList.size() < 2) {
			return false;
		}
		if (safaList.size() == 2) {
			result = safaList.get(0).intersectionWith(safaList.get(1), solver);
			return !SAFA.isEmpty(result, solver);
		} else {
			result = safaList.get(0).intersectionWith(safaList.get(1), solver);
			for (int i = 2; i < safaList.size(); i++) {
				result = result.intersectionWith(safaList.get(i), solver);
			}
			return !SAFA.isEmpty(result, solver);
		}

	}

	// private static ArrayList<SAFA<CharPred, Character>>
	// findExample(UnaryCharIntervalSolver solver,ArrayList<SAFA<CharPred,
	// Character>> safaList, int numOfExample){
	//
	// return safaList;
	// }

}
