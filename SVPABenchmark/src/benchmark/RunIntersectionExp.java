package benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;

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
	
	private static PrintWriter resultOfEmptiness;
	private static PrintWriter equivalenceFile;
	private static PrintWriter tripleResult;
	private static PrintWriter quadraResult;
	private static PrintWriter pentaResult;
	private static ArrayList<PairAut> pairList = new ArrayList<PairAut>();
	private static ArrayList<TripleAut> tripleList = new ArrayList<TripleAut>();
	private static ArrayList<QuadraAut> quadraList = new ArrayList<QuadraAut>();
	private static ArrayList<PentaAut> pentaList = new ArrayList<PentaAut>();
	private static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	//private static long timeout = 5000;
	private static ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
	private static ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
	// to store input file of patterns
	private static ArrayList<String> list = new ArrayList<String>();
	
	public static void main(String[] args) throws TimeoutException {

		

		try {
			inFile = new FileReader("src/benchmark/regexconverter/pattern@test.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		
		try {
			pairFile = new FileReader("src/benchmark/regexconverter/PairResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		
		try {
			equivalenceFile = new PrintWriter("src/benchmark/regexconverter/resultOfEquivalence2to3.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			resultOfEmptiness = new PrintWriter("src/benchmark/regexconverter/resultOfEmptiness.txt");
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

		try (BufferedReader br = new BufferedReader(inFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String regex : list) {
			sfaList.add((new SFAprovider(regex, solver)).getSFA());
		}

		for (SFA<CharPred, Character> sfa : sfaList) {
			safaList.add(sfa.getSAFA(solver));
		}

		equivalenceFile.print("name |SAFA1| |SAFA2| |SFA1| |SFA2| FullTime SolverTime subsTime SFAtime"+"\n");
		

		// generate pairs that has intersection
		// generatePairSFA();
		
		
		//resultOfEmptiness.print("Pair  1+2  1*2  SFAtime  ReverseSAFA SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"+"\n");
		resultOfEmptiness.print("Triple  1+2+3  1*2*3  SFAtime  ReverseSAFA SAFAfull SAFASolver SAFAsub SFA-SAFAfull Reverse-SAFAfull"+"\n");
		//generatePairSAFA(5000);
		
		buildPair();
		generateTripleSAFA(10000);
		
		// TODO: generate TripleSFA, QuadraSFA and PentsSFA
		
		resultOfEmptiness.close();
		equivalenceFile.close();
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
				if (!SFA.areEquivalent(result, SFA.getEmptySFA(solver), solver, timeOut)) {
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
				if (!SFA.areEquivalent(result, SFA.getEmptySFA(solver), solver, timeOut)) {
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
	// resultOfEmptiness.print(m+"\t"+n+"\n");
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
	// resultOfEmptiness.print(i+"\t"+j+"\t"+k+"\n");
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
					pairList.add(new PairAut(m, n, safa1, safa2, intersectedSAFA));
					resultOfEmptiness.print(m + ";" + n + "   ");
					int size1 = safa1.stateCount();
					int size2 = safa2.stateCount();
					int sizeSum = size1 + size2;
					int sizeMult = size1 * size2;
					resultOfEmptiness.print(sizeSum + "   " + sizeMult + "   ");

					ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa1 = sfaList.get(m);
					SFA<CharPred, Character> sfa2 = sfaList.get(n);
					tempSFA.add(sfa1);
					tempSFA.add(sfa2);
					long startDate = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, tempSFA, timeOut);
					long endDate = System.currentTimeMillis();
					long totalTimeSFA = endDate - startDate;
					if(intersectedSFA ==null){
						totalTimeSFA = timeOut;
					}
					resultOfEmptiness.print(totalTimeSFA+"   ");
					long totalTimeReverseSAFA;
					try{
						long startDate2 = System.currentTimeMillis();
						SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
						long endDate2 = System.currentTimeMillis();
						totalTimeReverseSAFA = endDate2 - startDate2;
					}catch(TimeoutException e){
						totalTimeReverseSAFA = timeOut;
					}
					resultOfEmptiness.print(totalTimeReverseSAFA+"   ");
					resultOfEmptiness.print(fullTimeSAFA+"   "+solverTimeSAFA+"   "+ subTimeSAFA+"        ");
					long SFAMinusSAFA = totalTimeSFA -fullTimeSAFA;
					long ReverseMinusSAFA = totalTimeReverseSAFA-fullTimeSAFA;
					resultOfEmptiness.print(SFAMinusSAFA+"   "+ReverseMinusSAFA+"\n");
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
			for (int k = 0; (k != i) && (k != j) && k < safaList.size(); k++) {
				ArrayList<SAFA<CharPred, Character>> temp = new ArrayList<SAFA<CharPred, Character>>();
				temp.add(pair.getIntersectedSAFA());
				temp.add(safaList.get(k));
				
				//here is intersectin of 2
				Timers.setTimeout(Long.MAX_VALUE);
				SAFA<CharPred, Character> intersectedSAFA = IntersectedSAFA(solver, temp, timeOut);
				long fullTimeSAFA = Timers.getFull();
				long solverTimeSAFA = Timers.getSolver();
				long subTimeSAFA = Timers.getSubsumption();
				
				if (intersectedSAFA != null) {
					tripleResult.print(i+"  "+j+"  "+k+"\n");
					resultOfEmptiness.print(i + "  " + j + "  " + k + "\n");
					temp = pair.getSAFAlist();
					// build new TripleAut
					TripleAut newTriple = new TripleAut(i, j, k, temp.get(0), temp.get(1), safaList.get(k),
							intersectedSAFA);
					tripleList.add(newTriple);

					newTriple.setSFA(sfaList.get(i), sfaList.get(j), sfaList.get(k));
					
					equivalenceFile.print(i + "," + j + "=" + i +","+ j +","+ k + "    " + pair.getIntersectedSFA().stateCount()
							+ "    " + newTriple.getIntersectedSFA().stateCount() + "    ");

					equivalenceFile.print(pair.getIntersectedSAFA().stateCount() + "    "
							+ newTriple.getIntersectedSAFA().stateCount() + "    ");

					// Run SAFA Equivalence test, also count full time solver
					// time and subsumption time
					Timers.setTimeout(Long.MAX_VALUE);
					try {
						SAFA.isEquivalent(pair.getIntersectedSAFA(), newTriple.getIntersectedSAFA(), solver,
								SAFA.getBooleanExpressionFactory(), timeOut);
						equivalenceFile.print(Timers.getFull() + "    " + Timers.getSolver() + "    " + Timers.getSubsumption()
								+ "    ");
					} catch (TimeoutException e) {
						equivalenceFile.print(timeOut + "    " + timeOut + "    " + timeOut + "    ");
					}

					// Now calculate the cost of SFA Equivalence test
					long startDate = System.currentTimeMillis();
					SFA.areEquivalent(pair.getIntersectedSFA(), newTriple.getIntersectedSFA(), solver);
					long endDate = System.currentTimeMillis();
					long sfaTestTime = endDate - startDate;
					equivalenceFile.print(sfaTestTime+"\n");
					
					
					int size1 = safaList.get(i).stateCount();
					int size2 = safaList.get(j).stateCount();
					int size3 = safaList.get(k).stateCount();
					int sizeSum = size1 + size2+ size3;
					int sizeMult = size1 * size2*  size3;
					resultOfEmptiness.print(sizeSum + "   " + sizeMult + "   ");

					ArrayList<SFA<CharPred, Character>> tempSFA = new ArrayList<SFA<CharPred, Character>>();
					SFA<CharPred, Character> sfa1 = pair.getIntersectedSFA();
					SFA<CharPred, Character> sfa2 = sfaList.get(k);
					tempSFA.add(sfa1);
					tempSFA.add(sfa2);
					long startDate2 = System.currentTimeMillis();
					SFA<CharPred, Character> intersectedSFA = IntersectedSFA(solver, tempSFA, timeOut);
					long endDate2 = System.currentTimeMillis();
					long totalTimeSFA = endDate2 - startDate2;
					if(intersectedSFA ==null){
						totalTimeSFA = timeOut;
					}
					resultOfEmptiness.print(totalTimeSFA+"   ");
					long totalTimeReverseSAFA;
					try{
						long startDate3 = System.currentTimeMillis();
						SAFA.areReverseEquivalent(intersectedSAFA, SAFA.getEmptySAFA(solver), solver, timeOut);
						long endDate3 = System.currentTimeMillis();
						totalTimeReverseSAFA = endDate3 - startDate3;
					}catch(TimeoutException e){
						totalTimeReverseSAFA = timeOut;
					}
					resultOfEmptiness.print(totalTimeReverseSAFA+"   ");
					resultOfEmptiness.print(fullTimeSAFA+"   "+solverTimeSAFA+"   "+ subTimeSAFA+"        ");
					long SFAMinusSAFA = totalTimeSFA -fullTimeSAFA;
					long ReverseMinusSAFA = totalTimeReverseSAFA-fullTimeSAFA;
					resultOfEmptiness.print(SFAMinusSAFA+"   "+ReverseMinusSAFA+"\n");
					
					
				}

			}
		}
	}
	
	private static void buildPair() throws TimeoutException{
		ArrayList<String> pairlist = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(pairFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				pairlist.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String line: pairlist){
			String[] splited = line.split("\\t+");
			int first = Integer.parseInt(splited[0]);
			int second = Integer.parseInt(splited[1]);
			SAFA<CharPred, Character> safa1 = safaList.get(first);
			SAFA<CharPred, Character> safa2 = safaList.get(second);
			PairAut newPair = new PairAut(first, second, safa1, safa2, safa1.intersectionWith( safa2, solver));
			newPair.setSFA(sfaList.get(first),sfaList.get(second));
			pairList.add(newPair);
			
		}
	}

}
