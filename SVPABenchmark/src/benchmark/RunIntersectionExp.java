package benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;
import java.math.*;

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

	private static PrintWriter resultOfEmptiness2;
	private static PrintWriter resultOfEmptiness3;
	private static PrintWriter equivalenceFile;
	private static PrintWriter pairResult;
	private static PrintWriter tripleResult;
	private static PrintWriter quadraResult;
	private static PrintWriter pentaResult;
	private static ArrayList<PairAut> pairList = new ArrayList<PairAut>();
	private static ArrayList<TripleAut> tripleList = new ArrayList<TripleAut>();
	private static ArrayList<QuadraAut> quadraList = new ArrayList<QuadraAut>();
	private static ArrayList<PentaAut> pentaList = new ArrayList<PentaAut>();
	private static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	// private static long timeout = 5000;
	private static ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
	private static ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
	// to store input file of patterns
	private static ArrayList<String> list = new ArrayList<String>();

	public static void main(String[] args) throws TimeoutException {

		try {
			inFile = new FileReader("src/benchmark/regexconverter/pattern@.txt");
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
			SFA<CharPred, Character> sfa= (new SFAprovider(regex, solver)).getSFA();
			if(sfa == null){
				System.err.println("Cannot build sfa"+regex);
			}
			sfaList.add(sfa);
		}

		for (SFA<CharPred, Character> sfa : sfaList) {
			safaList.add(sfa.getSAFA(solver));
		}

		// runEmptinessOf2(5000);
		buildPair();
		runEmptinessOf3(10000);

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
	 * loop through every combination of the file and generate pairs of SFA that
	 * intersect
	 */
	// private static void generatePairSFA() throws TimeoutException {
	// for (int m = 0; m < sfaList.size() - 1; m++) {
	// for (int n = m + 1; n < sfaList.size(); n++) {
	// ArrayList<SFA<CharPred, Character>> temp = new ArrayList<SFA<CharPred,
	// Character>>();
	// SFA<CharPred, Character> sfa1 = sfaList.get(m);
	// SFA<CharPred, Character> sfa2 = sfaList.get(n);
	// temp.add(sfa1);
	// temp.add(sfa2);
	// SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, temp);
	// boolean hasIntersection = false;
	// if(intersectedSFA !=null){
	// hasIntersection = true;
	// pairList.add(new PairAut(m, n, sfa1, sfa2, intersectedSFA));
	// resultOfEmptiness2.print(m+"\t"+n+"\n");
	// }
	// System.out.print(hasIntersection + " ");
	// System.out.println(m + " " + n);
	//
	// }
	// }
	// }

	/*
	 * generate a bundle of three SFAs that have intersection, see
	 * TripleAut.java also write to the file that has the result of experiment
	 * while generating triple bundle
	 */
	// private static void generateTripleSFA() throws TimeoutException{
	//
	// for(PairAut pair: pairList){
	// int i = pair.getFirstIndex();
	// int j =pair.getSecondIndex();
	// for(int k=0;(k !=i) && (k!=j) && k<sfaList.size();k++){
	// ArrayList<SFA<CharPred, Character>> temp= new ArrayList<SFA<CharPred,
	// Character>>();
	// temp.add(pair.getIntersectedSFA());
	// temp.add(sfaList.get(k));
	// SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, temp);
	// if(intersectedSFA != null){
	// resultOfEmptiness2.print(i+"\t"+j+"\t"+k+"\n");
	// temp = pair.getSFAlist();
	// //build new TripleAut
	// TripleAut newTriple = new TripleAut(i,j,k,
	// temp.get(0),temp.get(1),sfaList.get(k), intersectedSFA);
	// tripleList.add(newTriple);
	//
	// outFile.print(i+","+j+"="+i+j+k+"\t\t"+
	// pair.getIntersectedSFA().stateCount()+"\t\t"+
	// intersectedSFA.stateCount()+"\t\t");
	// //now build SAFA from SFA in PairAutomata
	// pair.buildSAFA();
	// outFile.print(pair.getIntersectedSAFA().stateCount()+"\t\t"+
	// newTriple.getIntersectedSAFA().stateCount()+"\t\t");
	//
	// //Run SAFA Equivalence test, also count full time solver time and
	// subsumption time
	// Timers.setTimeout(Long.MAX_VALUE);
	// try {
	// SAFA.isEquivalent(pair.getIntersectedSAFA(),
	// newTriple.getIntersectedSAFA(), solver,
	// SAFA.getBooleanExpressionFactory(), timeout);
	// outFile.print(Timers.getFull() + "\t\t" + Timers.getSolver() + "\t\t" +
	// Timers.getSubsumption()+"\t\t");
	// } catch (TimeoutException e) {
	// outFile.print(timeout + "\t\t" + timeout + "\t\t" + timeout +"\t\t");
	// }
	//
	// // Now calculate the cost of SFA Equivalence test
	// long startDate = System.currentTimeMillis();
	// SFA.areEquivalent(pair.getIntersectedSFA(), intersectedSFA, solver);
	// long endDate = System.currentTimeMillis();
	// long sfaTestTime = endDate- startDate;
	// outFile.print(sfaTestTime);
	// outFile.print(System.getProperty("line.separator"));
	// }
	//
	// }
	// }
	// }

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
		for (int m = 0; m < safaList.size() - 1; m++) {
			for (int n = m + 1; n < safaList.size(); n++) {
				ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
				SAFA<CharPred, Character> safa1 = safaList.get(m);
				SAFA<CharPred, Character> safa2 = safaList.get(n);
				temp.add(safa1);
				temp.add(safa2);
				Timers.setTimeout(Long.MAX_VALUE);
				SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(solver, temp, timeOut);
				long fullTimeSAFA = Timers.getFull();
				long solverTimeSAFA = Timers.getSolver();
				long subTimeSAFA = Timers.getSubsumption();
				boolean hasIntersection = false;
				if (intersectedSAFA != null) {
					hasIntersection = true;
					pairResult.println(m + ";" + n);
					pairList.add(new PairAut(m, n, safa1, safa2, intersectedSAFA));
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
					if (totalTimeSFA > 5000) {
						totalTimeSFA = 5000;
					}
					resultOfEmptiness2.print(totalTimeSFA + "   ");
					long totalTimeReverseSAFA;
					try {
						long startDate2 = System.currentTimeMillis();
						SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
						long endDate2 = System.currentTimeMillis();
						totalTimeReverseSAFA = endDate2 - startDate2;
					} catch (TimeoutException e) {
						totalTimeReverseSAFA = timeOut;
					}
					if (totalTimeReverseSAFA > 5000) {
						totalTimeReverseSAFA = 5000;
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

		for (PairAut pair : pairList) {
			int i = pair.getFirstIndex();
			int j = pair.getSecondIndex();
			
			//avoid recomputing the intersection of first two every time
			ArrayList<SAFA<CharPred, Character>> IntersectFirstTwoSAFA = pair.getSAFAlist();
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
			
			
			
			for (int k = j+1; k < safaList.size(); k++) {
				// intersect intersectedSAFA of first two and the new one 
				ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
				temp.add(intersectedSAFAtemp);
				temp.add(safaList.get(k));
				Timers.setTimeout(Long.MAX_VALUE);
				SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(solver, temp, timeOut);
				long fullTimeSAFA = Timers.getFull()+fullTimeSAFAtemp;
				long solverTimeSAFA = Timers.getSolver()+solverTimeSAFAtemp;
				long subTimeSAFA = Timers.getSubsumption()+subTimeSAFAtemp;
				boolean hasIntersection = false;
				if (intersectedSAFA != null) {
					hasIntersection = true;
					tripleResult.println(i + ";" + j + ";" + k);
					resultOfEmptiness3.print(i + ";" + j + ";" + k + "   ");
					SAFA<CharPred, Character> safa1= safaList.get(i);
					SAFA<CharPred, Character> safa2= safaList.get(j);
					SAFA<CharPred, Character> safa3= safaList.get(k);
					// build new TripleAut
//					TripleAut newTriple = new TripleAut(i, j, k, safa1, safa2, safa3,
//							intersectedSAFA);
//					tripleList.add(newTriple);
					
					int size1 = safa1.stateCount();
					int size2 = safa2.stateCount();
					int size3 = safa3.stateCount();
					int sizeSum = size1 + size2 + size3;
					
//					BigInteger bi1,bi2,bi3,bi4;
//					bi1 = new BigInteger(Integer.toString(size1));
//					bi2 = new BigInteger(Integer.toString(size2));
//					bi3 = new BigInteger(Integer.toString(size3));
//					bi4 = bi3.multiply(bi1.multiply(bi2));
//					int sizeMult;
//					try{
//						sizeMult =Integer.toString(bi4.intValueExact()).length() ;
//					}catch(ArithmeticException e){
//						sizeMult = Integer.toString(size1).length()+Integer.toString(size2).length()+Integer.toString(size3).length()-1;
//					}
					int sizeMult = Integer.toString(size1).length()+Integer.toString(size2).length()+Integer.toString(size3).length()-1;
					resultOfEmptiness3.print(sizeSum + "   " + sizeMult + "   ");

					ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa3 = sfaList.get(k);
					tempSFA.add(intersectedSFAtemp);
					tempSFA.add(sfa3);
					long startDate = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, tempSFA, timeOut);
					long endDate = System.currentTimeMillis();
					long totalTimeSFA = endDate - startDate + totalTimeSFAtemp;
					if (intersectedSFA == null) {
						totalTimeSFA = timeOut;
					}
					if(totalTimeSFA>10000){
						totalTimeSFA = 10000;
					}
					resultOfEmptiness3.print(totalTimeSFA + "   ");
					long totalTimeReverseSAFA;
					try {
						long startDate2 = System.currentTimeMillis();
						SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
						long endDate2 = System.currentTimeMillis();
						totalTimeReverseSAFA = endDate2 - startDate2;
					} catch (TimeoutException e) {
						totalTimeReverseSAFA = timeOut;
					}
					if(totalTimeReverseSAFA>10000){
						totalTimeReverseSAFA = 10000;
					}
					resultOfEmptiness3.print(totalTimeReverseSAFA + "   ");
					resultOfEmptiness3.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "        ");
					long SFAMinusSAFA = totalTimeSFA - fullTimeSAFA;
					long ReverseMinusSAFA = totalTimeReverseSAFA - fullTimeSAFA;
					resultOfEmptiness3.print(SFAMinusSAFA + "   " + ReverseMinusSAFA + "\n");

				}
				System.out.print(hasIntersection + "  ");
				System.out.println(i + " " + j+" "+k);

			}
		}
	}

	private static void buildPair() throws TimeoutException {
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

		for (String line : pairlist) {
			String[] splited = line.split(";");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			SAFA<CharPred, Character> safa1 = safaList.get(first);
			SAFA<CharPred, Character> safa2 = safaList.get(second);
			PairAut newPair = new PairAut(first, second, safa1, safa2, safa1.intersectionWith(safa2, solver));
			pairList.add(newPair);
			System.out.println("building "+line);
		}
	}

}
