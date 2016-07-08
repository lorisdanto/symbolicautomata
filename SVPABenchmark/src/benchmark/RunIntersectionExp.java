package benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;
import java.math.*;

import benchmark.regexconverter.Combination;
import benchmark.regexconverter.MultiCombination;
import RegexParser.RegexListNode;
import RegexParser.RegexParserProvider;
import automata.safa.SAFA;
import automata.sfa.SFA;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Timers;

/**
 * This class runs the intersection and run emptiness check on SFAs and SAFAs
 * which are built from Regular Expressions
 * 
 * It has the output limit of roughly 2000 SFAs/SAFAs on each iteration since
 * the procedure takes huge amount of time if runs on a large sample size
 * 
 * @author Fang Wang 06/19/2016
 *
 */
public class RunIntersectionExp {
	static FileReader inFile;
	static FileReader pairFile;
	static FileReader tripleFile;
	static FileReader quadraFile;
	static FileReader pentaFile;

	private static PrintWriter resultOfEmptiness2;
	private static PrintWriter resultOfEmptiness3;
	private static PrintWriter resultOfEmptiness4;
	private static PrintWriter resultOfEmptiness5;
	private static PrintWriter pairResult;
	private static PrintWriter tripleResult;
	private static PrintWriter quadraResult;
	private static PrintWriter pentaResult;

	private static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	private static ArrayList<Combination> pairCombination = new ArrayList<Combination>();
	private static ArrayList<MultiCombination> tripleCombination = new ArrayList<MultiCombination>();
	private static ArrayList<MultiCombination> quadraCombination = new ArrayList<MultiCombination>();

	private static ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
	private static ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();

	// to store input file of patterns
	private static ArrayList<String> list = new ArrayList<String>();

	// use static for SAFA times so it is convenient to modify them across
	// functions
	private static long totalTimeReverseSAFA;
	private static long fullTimeSAFA;
	private static long solverTimeSAFA;
	private static long subTimeSAFA;

	public static void main(String[] args) throws TimeoutException {

		// read the source file, pattern@ contains regexs that have symbol @ in
		// it
		try {
			inFile = new FileReader("src/benchmark/regexconverter/pattern@75.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}

		// store every line(regex) inside a ArrayList
		try (BufferedReader br = new BufferedReader(inFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			System.out.println(list.size());
			inFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// convert every regex to SFA
		for (String regex : list) {
			SFA<CharPred, Character> sfa = (new SFAprovider(regex, solver)).getSFA();
			if (sfa == null) {
				System.err.println("Cannot build sfa" + regex);
			}
			sfaList.add(sfa);
		}

		// convert every SFA to SAFA
		for (SFA<CharPred, Character> sfa : sfaList) {
			safaList.add(sfa.getSAFA(solver));
		}

		// run the experiment
		// runEmptinessOf2(5000);
		// buildPair();
		// runEmptinessOf3(5000);
		// buildTriple();
		// runEmptinessOf4(5000);
		buildQuadra();
		runEmptinessOf5(5000);

	}

	/**
	 * This method opens the output files and call the functions to build pair
	 * and run emptiness test
	 * 
	 * @param timeOut
	 * @throws TimeoutException
	 */
	private static void runEmptinessOf2(long timeOut) throws TimeoutException {
		try {
			resultOfEmptiness2 = new PrintWriter("src/benchmark/regexconverter/resultOfEmptiness2.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			pairResult = new PrintWriter("src/benchmark/regexconverter/PairResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		resultOfEmptiness2
				.print("Pair  1+2  1*2(#digits)  SFAtime  ReverseSAFA SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"
						+ "\n");
		generatePair(timeOut);
		resultOfEmptiness2.close();
		pairResult.close();
	}

	/**
	 * This method opens the output files and call the functions to build triple
	 * and run emptiness test
	 * 
	 * @param timeOut
	 * @throws TimeoutException
	 */
	private static void runEmptinessOf3(long timeOut) throws TimeoutException {
		try {
			resultOfEmptiness3 = new PrintWriter("src/benchmark/regexconverter/resultOfEmptiness3.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			tripleResult = new PrintWriter("src/benchmark/regexconverter/TripleResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		resultOfEmptiness3
				.print("Triple  1+2+3  1*2*3(#digits)  SFAtime  ReverseSAFA SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"
						+ "\n");
		generateTriple(timeOut);
		resultOfEmptiness3.close();
		tripleResult.close();
	}

	/**
	 * This method opens the output files and call the functions to build quadra
	 * and run emptiness test
	 * 
	 * @param timeOut
	 * @throws TimeoutException
	 */
	private static void runEmptinessOf4(long timeOut) throws TimeoutException {
		try {
			resultOfEmptiness4 = new PrintWriter("src/benchmark/regexconverter/resultOfEmptiness4.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			quadraResult = new PrintWriter("src/benchmark/regexconverter/QuadraResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		resultOfEmptiness4
				.print("Quadra  1+2+3+4  1*2*3*4(#digits)  SFAtime  ReverseSAFA SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"
						+ "\n");
		generateQuadra(timeOut);
		resultOfEmptiness4.close();
		quadraResult.close();
	}

	/**
	 * This method opens the output files and call the functions to build penta
	 * and run emptiness test
	 * 
	 * @param timeOut
	 * @throws TimeoutException
	 */
	private static void runEmptinessOf5(long timeOut) throws TimeoutException {
		try {
			resultOfEmptiness5 = new PrintWriter("src/benchmark/regexconverter/resultOfEmptiness5.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			pentaResult = new PrintWriter("src/benchmark/regexconverter/PentaResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		resultOfEmptiness5
				.print("Penta  1+2+3+4+5  1*2*3*4*5(#digits)  SFAtime  ReverseSAFA SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"
						+ "\n");
		generatePenta(timeOut);
		resultOfEmptiness5.close();
		pentaResult.close();
	}

	/**
	 * 
	 * This method finds the intersected SFA from an ArrayList of SFAs
	 * 
	 * @param sfaList
	 * @param timeOut
	 * @return intersected SFA, or null if timeout on emptiness check or there
	 *         is no intersection
	 */
	private static SFA<CharPred, Character> IntersectedSFA(ArrayList<SFA<CharPred, Character>> sfaList, long timeOut) {
		SFA<CharPred, Character> result = null;
		if (sfaList.size() < 2) {
			return null;
		}
		if (sfaList.size() == 2) {
			try {
				result = sfaList.get(0).intersectionWith(sfaList.get(1), solver, timeOut);
				if (!result.isEmpty()) {
					return result;
				}
				return null;
			} catch (Exception e) {
				return null;
			}

		} else {
			try {
				result = sfaList.get(0).intersectionWith(sfaList.get(1), solver, timeOut);
				for (int i = 2; i < sfaList.size(); i++) {
					result = result.intersectionWith(sfaList.get(i), solver, timeOut);
				}
				if (!result.isEmpty()) {
					return result;
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}

	}

	/**
	 * 
	 * This method finds the intersected SAFA from an ArrayList of SAFAs It also
	 * runs ReverseEquivalent inside
	 * 
	 * @param sfaList
	 * @param timeOut
	 * @return intersected SAFA, or null if timeout on emptiness check or there
	 *         is no intersection
	 */
	private static SAFA<CharPred, Character> IntersectedSAFA(ArrayList<SAFA<CharPred, Character>> safaList,
			long timeOut) {
		SAFA<CharPred, Character> result = null;
		boolean returnNull = true;
		if (safaList.size() < 2) {
			return null;
		}
		if (safaList.size() == 2) {
			try {
				Timers.setTimeout(Long.MAX_VALUE);
				result = safaList.get(0).intersectionWith(safaList.get(1), solver);
				if (!SAFA.isEmpty(result, solver, timeOut)) {
					fullTimeSAFA = Timers.getFull();
					solverTimeSAFA = Timers.getSolver();
					subTimeSAFA = Timers.getSubsumption();
					returnNull = false;
				} else {
					returnNull = true;
				}
			} catch (Exception e) {
				returnNull = true;
			}

			// sometimes the SAFA times out but reverse works, so try reverse
			// every time
			try {
				long startDate = System.currentTimeMillis();
				SAFA.areReverseEquivalent(result, SAFA.getEmptySAFA(solver), solver, timeOut);
				long endDate = System.currentTimeMillis();
				totalTimeReverseSAFA = endDate - startDate;
			} catch (TimeoutException e) {
				totalTimeReverseSAFA = timeOut;
			}
			return returnNull ? null : result;

		} else { // size >2
			try {
				Timers.setTimeout(Long.MAX_VALUE);
				result = safaList.get(0).intersectionWith(safaList.get(1), solver);
				for (int i = 2; i < safaList.size(); i++) {
					result = result.intersectionWith(safaList.get(i), solver);
				}
				if (!SAFA.isEmpty(result, solver, timeOut)) {
					fullTimeSAFA = Timers.getFull();
					solverTimeSAFA = Timers.getSolver();
					subTimeSAFA = Timers.getSubsumption();
					returnNull = false;
				} else {
					returnNull = true;
				}
			} catch (Exception e) {
				returnNull = true;
			}

			try {
				long startDate = System.currentTimeMillis();
				SAFA.areReverseEquivalent(result, SAFA.getEmptySAFA(solver), solver, timeOut);
				long endDate = System.currentTimeMillis();
				totalTimeReverseSAFA = endDate - startDate;
			} catch (TimeoutException e) {
				totalTimeReverseSAFA = timeOut;
			}

			return returnNull ? null : result;
		}
	}

	/**
	 * loop through every combination of the file and generate pairs of SAFA
	 * that intersect
	 */
	private static void generatePair(long timeOut) throws TimeoutException {
		int counter = 0;
		for (int m = 0; m < safaList.size() - 1 && counter < 2000; m++) {
			for (int n = m + 1; n < safaList.size() && counter < 2000; n++) {
				ArrayList<SAFA<CharPred, Character>> tempSAFA = new ArrayList<SAFA<CharPred, Character>>();
				SAFA<CharPred, Character> safa1 = safaList.get(m);
				SAFA<CharPred, Character> safa2 = safaList.get(n);
				tempSAFA.add(safa1);
				tempSAFA.add(safa2);
				// three SAFA times and reverseSAFAtime is calculated inside
				SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(tempSAFA, timeOut);
				// see if we have intersection in SFA
				ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
				SFA<CharPred, Character> sfa1 = sfaList.get(m);
				SFA<CharPred, Character> sfa2 = sfaList.get(n);
				tempSFA.add(sfa1);
				tempSFA.add(sfa2);
				long startDate = System.currentTimeMillis();
				SFA<CharPred, Character> intersectedSFA = IntersectedSFA(tempSFA, timeOut);
				long endDate = System.currentTimeMillis();
				long totalTimeSFA = endDate - startDate;
				if (intersectedSFA == null) {
					totalTimeSFA = timeOut;
				}
				if (totalTimeSFA > timeOut) {
					totalTimeSFA = timeOut;
				}

				boolean hasIntersection = false;
				if (intersectedSAFA != null || intersectedSFA != null) {
					counter++;
					hasIntersection = true;
					pairResult.println(m + ";" + n);
					resultOfEmptiness2.print(m + ";" + n + "   ");
					int size1 = safa1.stateCount();
					int size2 = safa2.stateCount();
					int sizeSum = size1 + size2;
					BigInteger bi1, bi2, bi3;
					bi1 = new BigInteger(Integer.toString(size1));
					bi2 = new BigInteger(Integer.toString(size2));
					bi3 = bi1.multiply(bi2);
					int sizeMult;
					try {
						sizeMult = Integer.toString(bi3.intValueExact()).length();
					} catch (ArithmeticException e) {
						// if the multiplication exceeds max int size, then take
						// approximation of digits
						sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length() - 1;
					}

					resultOfEmptiness2.print(sizeSum + "   " + sizeMult + "   ");
					resultOfEmptiness2.print(totalTimeSFA + "   ");
					// long totalTimeReverseSAFA;
					if (intersectedSAFA == null) {
						fullTimeSAFA = timeOut;
						solverTimeSAFA = timeOut;
						subTimeSAFA = timeOut;
					}
					if (totalTimeReverseSAFA > timeOut) {
						totalTimeReverseSAFA = timeOut;
					}
					resultOfEmptiness2.print(totalTimeReverseSAFA + "   ");
					resultOfEmptiness2.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "        ");
					long SFAMinusSAFA = totalTimeSFA - fullTimeSAFA;
					long ReverseMinusSAFA = totalTimeReverseSAFA - fullTimeSAFA;
					resultOfEmptiness2.print(SFAMinusSAFA + "   " + ReverseMinusSAFA + "\n");
				}

				System.out.print(hasIntersection + "  ");
				System.out.println(m + " " + n);
			}
		}
	}

	/**
	 * find combinations of three SFAs that have intersection, also write to the
	 * file that has the result of experiment while generating triple Output
	 * size is limited 2000 since it takes too long to generate complete output
	 * 
	 * To find possible triple from double: groups doubles into small groups
	 * that has common elements then try the combination between the elements
	 * that are not common e.g. if we have 0;1 0;2 0;3 , 0 is common element,
	 * then we can try 0;1;2, 0;1;3 and 0;2;3
	 */
	private static void generateTriple(long timeOut) throws TimeoutException {
		int counter = 0;

		for (Combination combi : pairCombination) {
			if (counter >= 1999) {
				break;
			}
			int i = combi.getCommonIndex();
			int j, k;
			ArrayList<Integer> tempIndexArray = combi.getIndexArray();
			while (!tempIndexArray.isEmpty() && counter < 2000) {
				j = tempIndexArray.get(0);
				for (int tempIndex = 1; tempIndex < tempIndexArray.size() && counter < 2000; tempIndex++) {
					k = tempIndexArray.get(tempIndex);

					// intersect intersectedSAFA of first two and the new one
					ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
					SAFA<CharPred, Character> safa1 = safaList.get(i);
					SAFA<CharPred, Character> safa2 = safaList.get(j);
					SAFA<CharPred, Character> safa3 = safaList.get(k);
					temp.add(safa1);
					temp.add(safa2);
					temp.add(safa3);
					SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(temp, timeOut);

					ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa1 = sfaList.get(i);
					SFA<CharPred, Character> sfa2 = sfaList.get(j);
					SFA<CharPred, Character> sfa3 = sfaList.get(k);
					tempSFA.add(sfa1);
					tempSFA.add(sfa2);
					tempSFA.add(sfa3);
					long startDate = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(tempSFA, timeOut);
					long endDate = System.currentTimeMillis();
					long totalTimeSFA = endDate - startDate;

					boolean hasIntersection = false;
					if (intersectedSAFA != null || intersectedSFA != null) {
						counter++;
						hasIntersection = true;
						tripleResult.println(i + ";" + j + ";" + k);
						resultOfEmptiness3.print(i + ";" + j + ";" + k + "   ");

						int size1 = safa1.stateCount();
						int size2 = safa2.stateCount();
						int size3 = safa3.stateCount();
						int sizeSum = size1 + size2 + size3;
						int sizeMult;
						BigInteger bi1, bi2, bi3, bi4;
						bi1 = new BigInteger(Integer.toString(size1));
						bi2 = new BigInteger(Integer.toString(size2));
						bi3 = new BigInteger(Integer.toString(size3));
						bi4 = bi3.multiply(bi1.multiply(bi2));

						try {
							sizeMult = Integer.toString(bi4.intValueExact()).length();
						} catch (ArithmeticException e) {
							// if the multiplication exceeds max int size, then
							// take approximation of digits
							sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length()
									+ Integer.toString(size3).length() - 1;
						}

						resultOfEmptiness3.print(sizeSum + "   " + sizeMult + "   ");

						if (intersectedSFA == null) {
							totalTimeSFA = timeOut;
						}
						if (totalTimeSFA > timeOut) {
							totalTimeSFA = timeOut;
						}
						if (intersectedSAFA == null) {
							fullTimeSAFA = timeOut;
							solverTimeSAFA = timeOut;
							subTimeSAFA = timeOut;
						}
						if (totalTimeReverseSAFA > timeOut) {
							totalTimeReverseSAFA = timeOut;
						}
						resultOfEmptiness3.print(totalTimeSFA + "   ");
						resultOfEmptiness3.print(totalTimeReverseSAFA + "   ");
						resultOfEmptiness3
								.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "        ");
						long SFAMinusSAFA = totalTimeSFA - fullTimeSAFA;
						long ReverseMinusSAFA = totalTimeReverseSAFA - fullTimeSAFA;
						resultOfEmptiness3.print(SFAMinusSAFA + "   " + ReverseMinusSAFA + "\n");
					}
					System.out.print(hasIntersection + "  ");
					System.out.println(i + " " + j + " " + k);

				}

				tempIndexArray.remove(0);
			}

		}
	}

	private static void generateQuadra(long timeOut) throws TimeoutException {
		int counter = 0;
		for (MultiCombination combi : tripleCombination) {
			if (counter >= 1999) {
				break;
			}
			ArrayList<Integer> indexAL = combi.getCommonIndex();
			int i = indexAL.get(0);
			// i2 means the second index, i3 means the third index etc.
			int i2 = indexAL.get(1);
			// j means the second to the end index, k means the end index
			int j, k;
			ArrayList<Integer> tempIndexArray = combi.getIndexArray();
			while (!tempIndexArray.isEmpty() && counter < 2000) {
				j = tempIndexArray.get(0);
				for (int tempIndex = 1; tempIndex < tempIndexArray.size() && counter < 2000; tempIndex++) {
					k = tempIndexArray.get(tempIndex);

					
					ArrayList<SAFA<CharPred, Character>> intersectedSAFAlist = new ArrayList<SAFA<CharPred, Character>>();
					intersectedSAFAlist.add(safaList.get(i));
					intersectedSAFAlist.add(safaList.get(i2));
					intersectedSAFAlist.add(safaList.get(j));
					intersectedSAFAlist.add(safaList.get(k));

					SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(intersectedSAFAlist, timeOut);

					
					ArrayList<SFA<CharPred, Character>> intersectedSFAlist = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa1 = sfaList.get(i);
					SFA<CharPred, Character> sfa2 = sfaList.get(i2);
					SFA<CharPred, Character> sfa3 = sfaList.get(j);
					SFA<CharPred, Character> lastSFA = sfaList.get(k);
					intersectedSFAlist.add(sfa1);
					intersectedSFAlist.add(sfa2);
					intersectedSFAlist.add(sfa3);
					intersectedSFAlist.add(lastSFA);

					long startDatetemp = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(intersectedSFAlist, timeOut);
					long endDatetemp = System.currentTimeMillis();
					long totalTimeSFA = endDatetemp - startDatetemp;

					boolean hasIntersection = false;
					if (intersectedSAFA != null || intersectedSFA != null) {
						counter++;
						hasIntersection = true;
						quadraResult.println(i + ";" + i2 + ";" + j + ";" + k);
						resultOfEmptiness4.print(i + ";" + i2 + ";" + j + ";" + k + "   ");
						SAFA<CharPred, Character> safa1 = safaList.get(i);
						SAFA<CharPred, Character> safa2 = safaList.get(i2);
						SAFA<CharPred, Character> safa3 = safaList.get(j);
						SAFA<CharPred, Character> safa4 = safaList.get(k);

						int size1 = safa1.stateCount();
						int size2 = safa2.stateCount();
						int size3 = safa3.stateCount();
						int size4 = safa4.stateCount();
						int sizeSum = size1 + size2 + size3 + size4;
						int sizeMult;
						BigInteger bi1, bi2, bi3, bi4, bi5;
						bi1 = new BigInteger(Integer.toString(size1));
						bi2 = new BigInteger(Integer.toString(size2));
						bi3 = new BigInteger(Integer.toString(size3));
						bi4 = new BigInteger(Integer.toString(size4));
						bi5 = bi4.multiply(bi3.multiply(bi1.multiply(bi2)));

						try {
							sizeMult = Integer.toString(bi5.intValueExact()).length();
						} catch (ArithmeticException e) {
							sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length()
									+ Integer.toString(size3).length() + Integer.toString(size4).length() - 1;
						}
						// sizeMult =
						// Integer.toString(size1).length()+Integer.toString(size2).length()+Integer.toString(size3).length()-1;
						resultOfEmptiness4.print(sizeSum + "   " + sizeMult + "   ");

						if (intersectedSFA == null) {
							totalTimeSFA = timeOut;
						}
						if (totalTimeSFA > timeOut) {
							totalTimeSFA = timeOut;
						}
						if (intersectedSAFA == null) {
							fullTimeSAFA = timeOut;
							solverTimeSAFA = timeOut;
							subTimeSAFA = timeOut;
						}

						if (totalTimeReverseSAFA > timeOut) {
							totalTimeReverseSAFA = timeOut;
						}
						resultOfEmptiness4.print(totalTimeSFA + "   ");
						resultOfEmptiness4.print(totalTimeReverseSAFA + "   ");
						resultOfEmptiness4
								.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "        ");
						long SFAMinusSAFA = totalTimeSFA - fullTimeSAFA;
						long ReverseMinusSAFA = totalTimeReverseSAFA - fullTimeSAFA;
						resultOfEmptiness4.print(SFAMinusSAFA + "   " + ReverseMinusSAFA + "\n");
					}
					System.out.print(hasIntersection + "  ");
					System.out.println(i + " " + i2 + " " + j + " " + k);

				}
				tempIndexArray.remove(0);
			}

		}
	}

	private static void generatePenta(long timeOut) throws TimeoutException {
		int counter = 0;
		for (MultiCombination combi : quadraCombination) {
			if (counter >= 1999) {
				break;
			}
			ArrayList<Integer> indexAL = combi.getCommonIndex();
			int i = indexAL.get(0);
			// i2 means the second index, i3 means the third index etc.
			int i2 = indexAL.get(1);
			int i3 = indexAL.get(2);
			// j means the second to the end index, k means the end index
			int j, k;
			ArrayList<Integer> tempIndexArray = combi.getIndexArray();
			while (!tempIndexArray.isEmpty() && counter < 2000) {
				j = tempIndexArray.get(0);
				for (int tempIndex = 1; tempIndex < tempIndexArray.size() && counter < 2000; tempIndex++) {
					k = tempIndexArray.get(tempIndex);

					
					ArrayList<SAFA<CharPred, Character>> intersectedSAFAlist = new ArrayList<SAFA<CharPred, Character>>();
					SAFA<CharPred, Character> safa1 = safaList.get(i);
					SAFA<CharPred, Character> safa2 = safaList.get(i2);
					SAFA<CharPred, Character> safa3 = safaList.get(i3);
					SAFA<CharPred, Character> safa4 = safaList.get(j);
					SAFA<CharPred, Character> safa5 = safaList.get(k);
					intersectedSAFAlist.add(safa1);
					intersectedSAFAlist.add(safa2);
					intersectedSAFAlist.add(safa3);
					intersectedSAFAlist.add(safa4);
					intersectedSAFAlist.add(safa5);

					SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(intersectedSAFAlist, timeOut);

					
					ArrayList<SFA<CharPred, Character>> intersectedSFAlist = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa1 = sfaList.get(i);
					SFA<CharPred, Character> sfa2 = sfaList.get(i2);
					SFA<CharPred, Character> sfa3 = sfaList.get(i3);
					SFA<CharPred, Character> sfa4 = sfaList.get(j);
					SFA<CharPred, Character> lastSFA = sfaList.get(k);
					intersectedSFAlist.add(sfa1);
					intersectedSFAlist.add(sfa2);
					intersectedSFAlist.add(sfa3);
					intersectedSFAlist.add(sfa4);
					intersectedSFAlist.add(lastSFA);

					long startDatetemp = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(intersectedSFAlist, timeOut);
					long endDatetemp = System.currentTimeMillis();
					long totalTimeSFA = endDatetemp - startDatetemp;

					boolean hasIntersection = false;
					if (intersectedSAFA != null || intersectedSFA != null) {
						counter++;
						hasIntersection = true;
						pentaResult.println(i + ";" + i2 + ";" + i3 + ";" + j + ";" + k);
						resultOfEmptiness5.print(i + ";" + i2 + ";" + i3 + ";" + j + ";" + k + "   ");

						int size1 = safa1.stateCount();
						int size2 = safa2.stateCount();
						int size3 = safa3.stateCount();
						int size4 = safa4.stateCount();
						int size5 = safa5.stateCount();
						int sizeSum = size1 + size2 + size3 + size4 + size5;
						int sizeMult;
						BigInteger bi1, bi2, bi3, bi4, bi5, bi6;
						bi1 = new BigInteger(Integer.toString(size1));
						bi2 = new BigInteger(Integer.toString(size2));
						bi3 = new BigInteger(Integer.toString(size3));
						bi4 = new BigInteger(Integer.toString(size4));
						bi5 = new BigInteger(Integer.toString(size5));
						bi6 = bi5.multiply(bi4.multiply(bi3.multiply(bi1.multiply(bi2))));

						try {
							sizeMult = Integer.toString(bi6.intValueExact()).length();
						} catch (ArithmeticException e) {
							sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length()
									+ Integer.toString(size3).length() + Integer.toString(size4).length()
									+ Integer.toString(size5).length() - 1;
						}
						// sizeMult =
						// Integer.toString(size1).length()+Integer.toString(size2).length()+Integer.toString(size3).length()-1;
						resultOfEmptiness5.print(sizeSum + "   " + sizeMult + "   ");

						if (intersectedSFA == null) {
							totalTimeSFA = timeOut;
						}
						if (totalTimeSFA > timeOut) {
							totalTimeSFA = timeOut;
						}
						if (intersectedSAFA == null) {
							fullTimeSAFA = timeOut;
							solverTimeSAFA = timeOut;
							subTimeSAFA = timeOut;
						}

						if (totalTimeReverseSAFA > timeOut) {
							totalTimeReverseSAFA = timeOut;
						}
						resultOfEmptiness5.print(totalTimeSFA + "   ");
						resultOfEmptiness5.print(totalTimeReverseSAFA + "   ");
						resultOfEmptiness5
								.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "        ");
						long SFAMinusSAFA = totalTimeSFA - fullTimeSAFA;
						long ReverseMinusSAFA = totalTimeReverseSAFA - fullTimeSAFA;
						resultOfEmptiness5.print(SFAMinusSAFA + "   " + ReverseMinusSAFA + "\n");
					}
					System.out.print(hasIntersection + "  ");
					System.out.println(i + " " + i2 + " " + i3 + " " + j + " " + k);

				}
				tempIndexArray.remove(0);
			}

		}
	}

	private static void buildPair() {
		ArrayList<String> pairlist = new ArrayList<String>();
		try {
			pairFile = new FileReader("src/benchmark/regexconverter/PairResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		try (BufferedReader br = new BufferedReader(pairFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				pairlist.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Combination pairComb = new Combination(0);
		pairCombination.add(pairComb);
		for (String line : pairlist) {
			String[] splited = line.split(";");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			if (pairComb.getCommonIndex() == first) {
				pairComb.addToIndexArray(second);
			} else {
				pairComb = new Combination(first, second);
				pairCombination.add(pairComb);

			}

			System.out.println("building " + line);
		}
	}

	private static void buildTriple() {
		ArrayList<String> templist = new ArrayList<String>();
		try {
			tripleFile = new FileReader("src/benchmark/regexconverter/TripleResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		try (BufferedReader br = new BufferedReader(tripleFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				templist.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		MultiCombination combi = new MultiCombination(0, 1);
		tripleCombination.add(combi);
		for (String line : templist) {
			String[] splited = line.split(";");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			int third = Integer.parseInt(splited[2]);
			ArrayList<Integer> indexs = combi.getCommonIndex();

			if (indexs.get(0) == first && indexs.get(1) == second) {
				combi.addToIndexArray(third);
			} else {
				combi = new MultiCombination(first, second);
				combi.addToIndexArray(third);
				tripleCombination.add(combi);
			}
			System.out.println("building " + line);
		}
	}

	private static void buildQuadra() {
		ArrayList<String> templist = new ArrayList<String>();
		try {
			quadraFile = new FileReader("src/benchmark/regexconverter/QuadraResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		try (BufferedReader br = new BufferedReader(quadraFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				templist.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		MultiCombination combi = new MultiCombination(0, 1, 2);
		quadraCombination.add(combi);
		for (String line : templist) {
			String[] splited = line.split(";");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			int third = Integer.parseInt(splited[2]);
			int fourth = Integer.parseInt(splited[3]);
			ArrayList<Integer> indexs = combi.getCommonIndex();

			if (indexs.get(0) == first && indexs.get(1) == second && indexs.get(2) == third) {
				combi.addToIndexArray(fourth);
			} else {
				combi = new MultiCombination(first, second, third);
				combi.addToIndexArray(fourth);
				quadraCombination.add(combi);
			}
			System.out.println("building " + line);
		}
	}

}
