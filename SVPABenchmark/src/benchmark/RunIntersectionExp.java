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
import benchmark.regexconverter.PairAut;
import benchmark.regexconverter.PentaAut;
import benchmark.regexconverter.QuadraAut;
import benchmark.regexconverter.TripleAut;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Timers;

public class RunIntersectionExp {
	static FileReader inFile;
	static FileReader pairFile;
	static FileReader tripleFile;
	static FileReader quadraFile;

	private static PrintWriter resultOfEmptiness2;
	private static PrintWriter resultOfEmptiness3;
	private static PrintWriter resultOfEmptiness4;
	private static PrintWriter equivalenceFile;
	private static PrintWriter pairResult;
	private static PrintWriter tripleResult;
	private static PrintWriter quadraResult;
	private static PrintWriter pentaResult;
//	private static ArrayList<PairAut> pairList = new ArrayList<PairAut>();
//	private static ArrayList<TripleAut> tripleList = new ArrayList<TripleAut>();
//	private static ArrayList<QuadraAut> quadraList = new ArrayList<QuadraAut>();
//	private static ArrayList<PentaAut> pentaList = new ArrayList<PentaAut>();
	
	private static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	private static ArrayList<Combination> pairCombination = new ArrayList<Combination>();
	private static ArrayList<MultiCombination> tripleCombination = new ArrayList<MultiCombination>();
	// private static long timeout = 5000;
	private static ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
	private static ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
	// to store input file of patterns
	private static ArrayList<String> list = new ArrayList<String>();

	public static void main(String[] args) throws TimeoutException {

		try {
			inFile = new FileReader("src/benchmark/regexconverter/pattern@75.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}

		// try {
		// equivalenceFile = new
		// PrintWriter("src/benchmark/regexconverter/resultOfEquivalence2to3.txt");
		// } catch (FileNotFoundException ex) {
		// System.err.println("File could not be opened for writing.");
		// System.exit(-1);
		// }

		try (BufferedReader br = new BufferedReader(inFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			System.out.println(list.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String regex : list) {
			SFA<CharPred, Character> sfa = (new SFAprovider(regex, solver)).getSFA();
			if (sfa == null) {
				System.err.println("Cannot build sfa" + regex);
			}
			sfaList.add(sfa);
		}

		for (SFA<CharPred, Character> sfa : sfaList) {
			safaList.add(sfa.getSAFA(solver));
		}

		runEmptinessOf2(5000);
		buildPair();
		runEmptinessOf3(5000);
		buildTriple();
		runEmptinessOf4(5000);

		// equivalenceFile.print("name |SAFA1| |SAFA2| |SFA1| |SFA2| FullTime
		// SolverTime subsTime SFAtime"+"\n");
		// generate pairs that has intersection
		// resultOfEmptiness2.print("Triple 1+2+3 1*2*3 SFAtime ReverseSAFA
		// SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"+"\n");
		// buildPair();
		// generateTripleSAFA(10000);
		// TODO: generate TripleSFA, QuadraSFA and PentsSFA
		// equivalenceFile.close();
		// tripleResult.close();

	}

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
		generatePairSAFA(timeOut);
		resultOfEmptiness2.close();
		pairResult.close();
	}

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
		generateTripleSAFA(timeOut);
		resultOfEmptiness3.close();
		tripleResult.close();
	}

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
		generateQuadraSAFA(timeOut);
		resultOfEmptiness4.close();
		quadraResult.close();
	}
	
	/*
	 * see if a list of SFAs have intersection, if there is, return the
	 * intersectedeSFA otherwise return null
	 */
	private static SFA<CharPred, Character> IntersectedSFA(UnaryCharIntervalSolver solver,
			ArrayList<SFA<CharPred, Character>> sfaList, long timeOut) {
		SFA<CharPred, Character> result;
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

	

	/*
	 * see if a list of SAFAs have intersection
	 */
	private static SAFA<CharPred, Character> IntersectedSAFA(UnaryCharIntervalSolver solver,
			ArrayList<SAFA<CharPred, Character>> safaList, long timeOut) {
		SAFA<CharPred, Character> result;
		if (safaList.size() < 2) {
			return null;
		}
		if (safaList.size() == 2) {

			try {
				result = safaList.get(0).intersectionWith(safaList.get(1), solver);
				// return !SAFA.isEmpty(result, solver);
				if (!SAFA.isEmpty(result, solver, timeOut)) {
					return result;
				}
				return null;
			} catch (Exception e) {
				return null;
			}

		} else {
			try {
				result = safaList.get(0).intersectionWith(safaList.get(1), solver);
				for (int i = 2; i < safaList.size(); i++) {
					result = result.intersectionWith(safaList.get(i), solver);
				}
				if (!SAFA.isEmpty(result, solver, timeOut)) {
					return result;
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}

	}

	/*
	 * loop through every combination of the file and generate pairs of SAFA
	 * that intersect
	 */
	private static void generatePairSAFA(long timeOut) throws TimeoutException {
		int counter = 0;
		for (int m = 0; m < safaList.size() - 1 && counter < 2000; m++) {
			for (int n = m + 1; n < safaList.size() && counter < 2000; n++) {
				ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
				SAFA<CharPred, Character> safa1 = safaList.get(m);
				SAFA<CharPred, Character> safa2 = safaList.get(n);
				temp.add(safa1);
				temp.add(safa2);
				Timers.setTimeout(Long.MAX_VALUE);
				// see if we have intersection in SAFA
				SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(solver, temp, timeOut);
				long fullTimeSAFA = Timers.getFull();
				long solverTimeSAFA = Timers.getSolver();
				long subTimeSAFA = Timers.getSubsumption();
				
				// see if we have intersection in SFA
				ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
				SFA<CharPred, Character> sfa1 = sfaList.get(m);
				SFA<CharPred, Character> sfa2 = sfaList.get(n);
				tempSFA.add(sfa1);
				tempSFA.add(sfa2);
				long startDate = System.currentTimeMillis();
				SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, tempSFA, timeOut);
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
					// pairList.add(new PairAut(m, n, safa1, safa2));
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
						sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length() - 1;
					}
					resultOfEmptiness2.print(sizeSum + "   " + sizeMult + "   ");

					resultOfEmptiness2.print(totalTimeSFA + "   ");
					
					long totalTimeReverseSAFA;
					if(intersectedSAFA != null){
						try {
							long startDate2 = System.currentTimeMillis();
							SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
							long endDate2 = System.currentTimeMillis();
							totalTimeReverseSAFA = endDate2 - startDate2;
						} catch (TimeoutException e) {
							totalTimeReverseSAFA = timeOut;
						} 
					}else{
						totalTimeReverseSAFA = timeOut;
						
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

	/*
	 * generate a bundle of three SFAs that have intersection, see
	 * TripleAut.java also write to the file that has the result of experiment
	 * while generating triple bundle
	 */
	private static void generateTripleSAFA(long timeOut) throws TimeoutException {
		int counter = 0;
		for (Combination combi : pairCombination) {
			if(counter>=2000){
				break;
			}
			int i = combi.getCommonIndex();
			int j, k;
			ArrayList<Integer> tempIndexArray = combi.getIndexArray();
			while (!tempIndexArray.isEmpty() && counter<2000) {
				j = tempIndexArray.get(0);

				// avoid recomputing the intersection of first two every time
				ArrayList<SAFA<CharPred, Character>> IntersectFirstTwoSAFA = new ArrayList<SAFA<CharPred, Character>>();
				IntersectFirstTwoSAFA.add(safaList.get(i));
				IntersectFirstTwoSAFA.add(safaList.get(j));
				Timers.setTimeout(Long.MAX_VALUE);
				SAFA<CharPred, Character> intersectedSAFAtemp = IntersectedSAFA(solver, IntersectFirstTwoSAFA, timeOut);
				long fullTimeSAFAtemp = Timers.getFull();
				long solverTimeSAFAtemp = Timers.getSolver();
				long subTimeSAFAtemp = Timers.getSubsumption();

				// also pre-computing the intersection of the first two SFAs
				ArrayList<SFA<CharPred, Character>> IntersectFirstTwoSFA = new ArrayList<SFA<CharPred, Character>>();
				SFA<CharPred, Character> sfa1 = sfaList.get(i);
				SFA<CharPred, Character> sfa2 = sfaList.get(j);
				IntersectFirstTwoSFA.add(sfa1);
				IntersectFirstTwoSFA.add(sfa2);

				long startDatetemp = System.currentTimeMillis();
				SFA<CharPred, Character> intersectedSFAtemp = IntersectedSFA(solver, IntersectFirstTwoSFA, timeOut);
				long endDatetemp = System.currentTimeMillis();
				long totalTimeSFAtemp = endDatetemp - startDatetemp;

				for (int tempIndex = 1; tempIndex < tempIndexArray.size() && counter<2000; tempIndex++) {
					k = tempIndexArray.get(tempIndex);

					// intersect intersectedSAFA of first two and the new one
					ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
					temp.add(intersectedSAFAtemp);
					temp.add(safaList.get(k));
					Timers.setTimeout(Long.MAX_VALUE);
					SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(solver, temp,
							timeOut - fullTimeSAFAtemp);
					long fullTimeSAFA = Timers.getFull() + fullTimeSAFAtemp;
					long solverTimeSAFA = Timers.getSolver() + solverTimeSAFAtemp;
					long subTimeSAFA = Timers.getSubsumption() + subTimeSAFAtemp;
					
					ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa3 = sfaList.get(k);
					tempSFA.add(intersectedSFAtemp);
					tempSFA.add(sfa3);
					long startDate = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, tempSFA,
							timeOut - totalTimeSFAtemp);
					long endDate = System.currentTimeMillis();
					long totalTimeSFA = endDate - startDate + totalTimeSFAtemp;
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
						tripleResult.println(i + ";" + j + ";" + k);
						resultOfEmptiness3.print(i + ";" + j + ";" + k + "   ");
						SAFA<CharPred, Character> safa1 = safaList.get(i);
						SAFA<CharPred, Character> safa2 = safaList.get(j);
						SAFA<CharPred, Character> safa3 = safaList.get(k);
						// build new TripleAut
						// TripleAut newTriple = new TripleAut(i, j, k, safa1,
						// safa2, safa3,
						// intersectedSAFA);
						// tripleList.add(newTriple);

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
							sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length()
									+ Integer.toString(size3).length() - 1;
						}
						// sizeMult =
						// Integer.toString(size1).length()+Integer.toString(size2).length()+Integer.toString(size3).length()-1;
						resultOfEmptiness3.print(sizeSum + "   " + sizeMult + "   ");

						resultOfEmptiness3.print(totalTimeSFA + "   ");
						
						long totalTimeReverseSAFA;
						if(intersectedSAFA != null){
							try {
								long startDate2 = System.currentTimeMillis();
								SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
								long endDate2 = System.currentTimeMillis();
								totalTimeReverseSAFA = endDate2 - startDate2;
							} catch (TimeoutException e) {
								totalTimeReverseSAFA = timeOut;
							}
						}else{
							totalTimeReverseSAFA = timeOut;
							
							fullTimeSAFA = timeOut;
							solverTimeSAFA = timeOut;
							subTimeSAFA = timeOut;
						}
						
						if (totalTimeReverseSAFA > timeOut) {
							totalTimeReverseSAFA = timeOut;
						}
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
	
	private static void generateQuadraSAFA(long timeOut) throws TimeoutException {
		int counter = 0;
		for (MultiCombination combi : tripleCombination) {
			if(counter>=2000){
				break;
			}
			ArrayList<Integer> indexAL = combi.getCommonIndex();
			int i = indexAL.get(0);
			// i2 means the second index, i3 means the third index etc.
			int i2 = indexAL.get(1);
			// j means the second to the end index, k means the end index
			int j, k;
			ArrayList<Integer> tempIndexArray = combi.getIndexArray();
			while (!tempIndexArray.isEmpty() && counter<2000) {
				j = tempIndexArray.get(0);

				// avoid recomputing the intersection of first three every time
				ArrayList<SAFA<CharPred, Character>> IntersectTempSAFAList = new ArrayList<SAFA<CharPred, Character>>();
				IntersectTempSAFAList.add(safaList.get(i));
				IntersectTempSAFAList.add(safaList.get(i2));
				IntersectTempSAFAList.add(safaList.get(j));
				Timers.setTimeout(Long.MAX_VALUE);
				SAFA<CharPred, Character> intersectedSAFAtemp = IntersectedSAFA(solver, IntersectTempSAFAList, timeOut);
				long fullTimeSAFAtemp = Timers.getFull();
				long solverTimeSAFAtemp = Timers.getSolver();
				long subTimeSAFAtemp = Timers.getSubsumption();

				// also pre-computing the intersection of the first three SFAs
				ArrayList<SFA<CharPred, Character>> IntersectTempSFAList = new ArrayList<SFA<CharPred, Character>>();
				SFA<CharPred, Character> sfa1 = sfaList.get(i);
				SFA<CharPred, Character> sfa2 = sfaList.get(i2);
				SFA<CharPred, Character> sfa3 = sfaList.get(j);
				IntersectTempSFAList.add(sfa1);
				IntersectTempSFAList.add(sfa2);
				IntersectTempSFAList.add(sfa3);

				long startDatetemp = System.currentTimeMillis();
				SFA<CharPred, Character> intersectedSFAtemp = IntersectedSFA(solver, IntersectTempSFAList, timeOut);
				long endDatetemp = System.currentTimeMillis();
				long totalTimeSFAtemp = endDatetemp - startDatetemp;

				for (int tempIndex = 1; tempIndex < tempIndexArray.size() && counter<2000; tempIndex++) {
					k = tempIndexArray.get(tempIndex);

					// intersect intersectedSAFA of first two and the new one
					ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
					temp.add(intersectedSAFAtemp);
					temp.add(safaList.get(k));
					Timers.setTimeout(Long.MAX_VALUE);
					SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(solver, temp,
							timeOut - fullTimeSAFAtemp);
					long fullTimeSAFA = Timers.getFull() + fullTimeSAFAtemp;
					long solverTimeSAFA = Timers.getSolver() + solverTimeSAFAtemp;
					long subTimeSAFA = Timers.getSubsumption() + subTimeSAFAtemp;
					
					ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> lastSFA = sfaList.get(k);
					tempSFA.add(intersectedSFAtemp);
					tempSFA.add(lastSFA);
					long startDate = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, tempSFA,
							timeOut - totalTimeSFAtemp);
					long endDate = System.currentTimeMillis();
					long totalTimeSFA = endDate - startDate + totalTimeSFAtemp;
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
						quadraResult.println(i + ";" +i2 +";"+ j + ";" + k);
						resultOfEmptiness4.print(i + ";"+i2 +";" + j + ";" + k + "   ");
						SAFA<CharPred, Character> safa1 = safaList.get(i);
						SAFA<CharPred, Character> safa2 = safaList.get(i2);
						SAFA<CharPred, Character> safa3 = safaList.get(j);
						SAFA<CharPred, Character> safa4 = safaList.get(k);
						

						int size1 = safa1.stateCount();
						int size2 = safa2.stateCount();
						int size3 = safa3.stateCount();
						int size4 = safa4.stateCount();
						int sizeSum = size1 + size2 + size3+ size4;
						int sizeMult;
						BigInteger bi1, bi2, bi3, bi4, bi5;
						bi1 = new BigInteger(Integer.toString(size1));
						bi2 = new BigInteger(Integer.toString(size2));
						bi3 = new BigInteger(Integer.toString(size3));
						bi4 = new BigInteger(Integer.toString(size4));
						bi5 = bi4.multiply(bi3.multiply(bi1.multiply(bi2))); ;

						try {
							sizeMult = Integer.toString(bi5.intValueExact()).length();
						} catch (ArithmeticException e) {
							sizeMult = Integer.toString(size1).length() + Integer.toString(size2).length()
									+ Integer.toString(size3).length()+ + Integer.toString(size4).length() - 1;
						}
						// sizeMult =
						// Integer.toString(size1).length()+Integer.toString(size2).length()+Integer.toString(size3).length()-1;
						resultOfEmptiness4.print(sizeSum + "   " + sizeMult + "   ");

						resultOfEmptiness4.print(totalTimeSFA + "   ");
						
						long totalTimeReverseSAFA;
						if(intersectedSAFA != null){
							try {
								long startDate2 = System.currentTimeMillis();
								SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
								long endDate2 = System.currentTimeMillis();
								totalTimeReverseSAFA = endDate2 - startDate2;
							} catch (TimeoutException e) {
								totalTimeReverseSAFA = timeOut;
							}
						}else{
							totalTimeReverseSAFA = timeOut;
							
							fullTimeSAFA = timeOut;
							solverTimeSAFA = timeOut;
							subTimeSAFA = timeOut;
						}
						
						if (totalTimeReverseSAFA > timeOut) {
							totalTimeReverseSAFA = timeOut;
						}
						resultOfEmptiness4.print(totalTimeReverseSAFA + "   ");
						
						resultOfEmptiness4
								.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "        ");
						long SFAMinusSAFA = totalTimeSFA - fullTimeSAFA;
						long ReverseMinusSAFA = totalTimeReverseSAFA - fullTimeSAFA;
						resultOfEmptiness4.print(SFAMinusSAFA + "   " + ReverseMinusSAFA + "\n");

					}
					System.out.print(hasIntersection + "  ");
					System.out.println(i + " " + j + " " + k);

				}

				tempIndexArray.remove(0);
			}

		}
	}

	private static void buildPair(){
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
		for (String line : pairlist) {
			String[] splited = line.split(";");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			if (pairComb.getCommonIndex() == first) {
				pairComb.addToIndexArray(second);
			} else {
				pairComb = new Combination(first, second);

			}

			pairCombination.add(pairComb);
			System.out.println("building " + line);
		}
	}
	
	private static void buildTriple(){
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

		MultiCombination combi = new MultiCombination(0,1);
		for (String line : templist) {
			String[] splited = line.split(";");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			int third = Integer.parseInt(splited[2]);
			ArrayList<Integer> indexs = combi.getCommonIndex();
			
			if ( indexs.get(0)== first && indexs.get(1)== second) {
				combi.addToIndexArray(third);
			} else {
				combi = new MultiCombination(first, second);
				combi.addToIndexArray(third);
			}

			tripleCombination.add(combi);
			System.out.println("building " + line);
		}
	}

}
