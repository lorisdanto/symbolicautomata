package benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.Triple;
import org.sat4j.specs.TimeoutException;
import java.math.*;

import benchmark.regexconverter.Combination;
import benchmark.regexconverter.MultiCombination;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFA;
import automata.safa.booleanexpression.BDDExpression;
import automata.safa.booleanexpression.BDDExpressionFactory;
import automata.safa.booleanexpression.PositiveBooleanExpression;
import automata.safa.booleanexpression.SumOfProductsFactory;
import automata.sfa.SFA;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Timers;

public class RunSelfEquivalenceExp {
	static FileReader inFile;
	static FileReader pairFile;
	static FileReader tripleFile;
	static FileReader quadraFile;
	static FileReader pentaFile;

	private static PrintWriter equivalence2to3;
	private static PrintWriter equivalence3to4;
	private static PrintWriter equivalence4to5;

	private static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();

	private static ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
	private static ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
	// to store input file of patterns
	private static ArrayList<String> list = new ArrayList<String>();
	private static ArrayList<String> tripleList = new ArrayList<String>();
	private static ArrayList<String> quadraList = new ArrayList<String>();
	private static ArrayList<String> pentaList = new ArrayList<String>();
	private static long fullTimeSAFA;
	private static long solverTimeSAFA;
	private static long subTimeSAFA;
	private static long totalTimeSFA;
	private static long exploredStatesSAFA;
	private static long exploredStatesSFA;
	private static long successfulSubsumptionsSAFA;
	private static int safa1Size = 0;
	private static int safa2Size = 0;
	private static int sfa1Size = 0;
	private static int sfa2Size = 0;

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

		runEquivalenceOf2to3(5000);
		runEquivalenceOf3to4(5000);
		runEquivalenceOf4to5(5000);

	}

	private static void runEquivalenceOf2to3(long timeOut) throws TimeoutException {
		try {
			equivalence2to3 = new PrintWriter("src/benchmark/regexconverter/EquivalenceOf2to3Selfnew.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}
		try {
			pairFile = new FileReader("src/benchmark/regexconverter/PairResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		try (BufferedReader br = new BufferedReader(pairFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] splited = line.split(";");

				tripleList.add(line + ";" + splited[0]);
				tripleList.add(line + ";" + splited[1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		equivalence2to3
				.print("name   |SAFA1|   |SAFA2|   |SFA1|   |SFA2|   FullTime   SolverTime   subsTime   SFAtime   SFA-SAFAfull"
						+ "\n");
		generate2to3(timeOut);
		equivalence2to3.close();

	}

	private static void runEquivalenceOf3to4(long timeOut) throws TimeoutException {
		try {
			equivalence3to4 = new PrintWriter("src/benchmark/regexconverter/EquivalenceOf3to4Selfnew.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			tripleFile = new FileReader("src/benchmark/regexconverter/TripleResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}

		try (BufferedReader br = new BufferedReader(tripleFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] splited = line.split(";");

				quadraList.add(line + ";" + splited[0]);
				quadraList.add(line + ";" + splited[1]);
				quadraList.add(line + ";" + splited[2]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		equivalence3to4
				.print("name   |SAFA1|   |SAFA2|   |SFA1|   |SFA2|   FullTime   SolverTime   subsTime   SFAtime   SFA-SAFAfull"
						+ "\n");
		generate3to4(timeOut);
		equivalence3to4.close();

	}

	private static void runEquivalenceOf4to5(long timeOut) throws TimeoutException {
		try {
			equivalence4to5 = new PrintWriter("src/benchmark/regexconverter/EquivalenceOf4to5Selfnew.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File could not be opened for writing.");
			System.exit(-1);
		}

		try {
			quadraFile = new FileReader("src/benchmark/regexconverter/QuadraResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}

		try (BufferedReader br = new BufferedReader(quadraFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] splited = line.split(";");
				pentaList.add(line + ";" + splited[0]);
				pentaList.add(line + ";" + splited[1]);
				pentaList.add(line + ";" + splited[2]);
				pentaList.add(line + ";" + splited[3]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		equivalence4to5
				.print("name   |SAFA1|   |SAFA2|   |SFA1|   |SFA2|   FullTime   SolverTime   subsTime   exploredStatesSAFA   "
						+ "successfulSubs   SFAtime   exploredStatesSFA   SFA-SAFAfull"
						+ "\n");
		generate4to5(timeOut);
		equivalence4to5.close();

	}

	private static void generate2to3(long timeOut) {

		for (String str : tripleList) {
			String[] splited = str.split(";");
			StringBuilder builder = new StringBuilder();
			String first = splited[0];
			String second = splited[1];
			builder.append(first + ";" + second);
			String possiblePair = builder.toString();

			System.out.println("Building " + possiblePair + "=" + str);
			equivalentTest(splited, timeOut);
			if(fullTimeSAFA == timeOut && totalTimeSFA == timeOut){
				System.out.println("Both timeout");
				continue;
			}
			equivalence2to3.print(possiblePair + "=" + str + "   ");
			equivalence2to3.print(safa1Size + "   " + safa2Size + "   " + sfa1Size + "   " + sfa2Size + "   ");
			long sfaMinussafa = totalTimeSFA - fullTimeSAFA;
			equivalence2to3.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "   " 
					+ exploredStatesSAFA + "   " + successfulSubsumptionsSAFA + "   "				
					+ totalTimeSFA+ "   " 
					+ exploredStatesSFA+ "   " 
					+ sfaMinussafa + "\n");
			System.out.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "   " 
					+ exploredStatesSAFA + "   " + successfulSubsumptionsSAFA + "   "				
					+ totalTimeSFA+ "   " 
					+ exploredStatesSFA+ "   " 
					+ sfaMinussafa + "\n");
		}

	}

	private static void generate3to4(long timeOut) {

		for (String str : quadraList) {
			String[] splited = str.split(";");
			StringBuilder builder = new StringBuilder();
			String first = splited[0];
			String second = splited[1];
			String third = splited[2];
			builder.append(first + ";" + second + ";" + third);
			String possiblePair = builder.toString();

			System.out.println("Building " + possiblePair + "=" + str);
			equivalentTest(splited, timeOut);
			if(fullTimeSAFA == timeOut && totalTimeSFA == timeOut){
				System.out.println("Both timeout");
				continue;
			}
			equivalence3to4.print(possiblePair + "=" + str + "   ");
			equivalence3to4.print(safa1Size + "   " + safa2Size + "   " + sfa1Size + "   " + sfa2Size + "   ");
			long sfaMinussafa = totalTimeSFA - fullTimeSAFA;
			equivalence3to4.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "   " 
					+ exploredStatesSAFA + "   " + successfulSubsumptionsSAFA + "   "				
					+ totalTimeSFA+ "   " 
					+ exploredStatesSFA+ "   " 
					+ sfaMinussafa + "\n");
			System.out.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "   " 
					+ exploredStatesSAFA + "   " + successfulSubsumptionsSAFA + "   "				
					+ totalTimeSFA+ "   " 
					+ exploredStatesSFA+ "   " 
					+ sfaMinussafa + "\n");

		}

	}

	private static void generate4to5(long timeOut) {

		for (String str : pentaList) {
			String[] splited = str.split(";");
			StringBuilder builder = new StringBuilder();
			String first = splited[0];
			String second = splited[1];
			String third = splited[2];
			String fourth = splited[3];
			builder.append(first + ";" + second + ";" + third + ";" + fourth);
			String possiblePair = builder.toString();

			System.out.println("Building " + possiblePair + "=" + str);
			equivalentTest(splited, timeOut);
			if(fullTimeSAFA == timeOut && totalTimeSFA == timeOut){
				System.out.println("Both timeout");
				continue;
			}
			equivalence4to5.print(possiblePair + "=" + str + "   ");
			equivalence4to5.print(safa1Size + "   " + safa2Size + "   " + sfa1Size + "   " + sfa2Size + "   ");
			long sfaMinussafa = totalTimeSFA - fullTimeSAFA;
			equivalence4to5.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "   " 
					+ exploredStatesSAFA + "   " + successfulSubsumptionsSAFA + "   "				
					+ totalTimeSFA+ "   " 
					+ exploredStatesSFA+ "   " 
					+ sfaMinussafa + "\n");
			System.out.print(fullTimeSAFA + "   " + solverTimeSAFA + "   " + subTimeSAFA + "   " 
					+ exploredStatesSAFA + "   " + successfulSubsumptionsSAFA + "   "				
					+ totalTimeSFA+ "   " 
					+ exploredStatesSFA+ "   " 
					+ sfaMinussafa + "\n");
		}

	}

	private static void equivalentTest(String[] splited, long timeOut) {
		safa1Size = 0;
		safa2Size = 0;
		sfa1Size = 0;
		sfa2Size = 0;

		ArrayList<SAFA<CharPred, Character>> safaRHS = new ArrayList<SAFA<CharPred, Character>>();
		ArrayList<SFA<CharPred, Character>> sfaLHS = new ArrayList<SFA<CharPred, Character>>();
		ArrayList<SFA<CharPred, Character>> sfaRHS = new ArrayList<SFA<CharPred, Character>>();
		// put elements except the last one to the SFA/SAFA list, only the RHS
		// need the last one element
		for (int i = 0; i < splited.length - 1; i++) {
			SAFA<CharPred, Character> tempSAFA = safaList.get(Integer.parseInt(splited[i]));
			SFA<CharPred, Character> tempSFA = sfaList.get(Integer.parseInt(splited[i]));
			safa1Size = safa1Size + tempSAFA.stateCount();
			sfa1Size = sfa1Size + tempSFA.stateCount();
			safa2Size = safa2Size + tempSAFA.stateCount();
			sfa2Size = sfa2Size + tempSFA.stateCount();

			safaRHS.add(tempSAFA);
			sfaLHS.add(tempSFA);
			sfaRHS.add(tempSFA);
		}

		SAFA<CharPred, Character> lastSAFA = safaList.get(Integer.parseInt(splited[splited.length - 1]));
		SFA<CharPred, Character> lastSFA = sfaList.get(Integer.parseInt(splited[splited.length - 1]));
		safa2Size = safa2Size + lastSAFA.stateCount();
		sfa2Size = sfa2Size + lastSFA.stateCount();

		safaRHS.add(lastSAFA);
		sfaRHS.add(lastSFA);

		runEquivalent(safaRHS, sfaLHS, sfaRHS, timeOut);
		if (fullTimeSAFA > timeOut) {
			fullTimeSAFA = timeOut;
		}
		if (solverTimeSAFA > timeOut) {
			solverTimeSAFA = timeOut;
		}
		if (subTimeSAFA > timeOut) {
			subTimeSAFA = timeOut;
		}
		if (totalTimeSFA > timeOut) {
			totalTimeSFA = timeOut;
		}

	}

	private static void runEquivalent(ArrayList<SAFA<CharPred, Character>> safaRHS,
			ArrayList<SFA<CharPred, Character>> sfaLHS, ArrayList<SFA<CharPred, Character>> sfaRHS, long timeOut) {
		try {

			long start = System.currentTimeMillis();
			Triple<SAFA<CharPred, Character>, PositiveBooleanExpression, PositiveBooleanExpression> tempTriple = IntersectedSAFA(
					safaRHS);
			long totalTime = System.currentTimeMillis() - start;
			long fullTimeSAFAIntersect = totalTime;
			long solverTimeSAFAIntersect = 0;
			long subTimeSAFAIntersect = 0;

			SAFA<CharPred, Character> tempSAFA = tempTriple.getLeft();
			Timers.setTimeout(Long.MAX_VALUE);
			SAFA.checkEquivalenceOfTwoConfigurations(tempSAFA, tempTriple.getMiddle(), tempSAFA.getInitialState(),
					solver, SAFA.getBooleanExpressionFactory(), timeOut - fullTimeSAFAIntersect);
			fullTimeSAFA = Timers.getFull() + fullTimeSAFAIntersect;
			solverTimeSAFA = Timers.getSolver() + solverTimeSAFAIntersect;
			subTimeSAFA = Timers.getSubsumption() + subTimeSAFAIntersect;
			
			exploredStatesSAFA = Timers.exploredStates;
			successfulSubsumptionsSAFA = Timers.successfulSubs;

		} catch (TimeoutException e) {
			fullTimeSAFA = timeOut;
			solverTimeSAFA = timeOut;
			subTimeSAFA = timeOut;
			exploredStatesSAFA = -1;
			successfulSubsumptionsSAFA = -1;
		} catch (NullPointerException e) {
			fullTimeSAFA = timeOut;
			solverTimeSAFA = timeOut;
			subTimeSAFA = timeOut;
			exploredStatesSAFA = -1;
			successfulSubsumptionsSAFA = -1;
		}

		try {
			long totalTimeLeft = timeOut;
			long startDate = System.currentTimeMillis();
			SFA<CharPred, Character> tempLeftSFA = IntersectedSFA(sfaLHS, totalTimeLeft);
			long endDate = System.currentTimeMillis();
			totalTimeLeft -= endDate - startDate;
			startDate = System.currentTimeMillis();
			SFA<CharPred, Character> tempRightSFA = IntersectedSFA(sfaRHS, totalTimeLeft);
			endDate = System.currentTimeMillis();
			totalTimeLeft -= endDate - startDate;
			startDate = System.currentTimeMillis();
			tempLeftSFA.isHopcroftKarpEquivalentTo(tempRightSFA, solver, totalTimeLeft);
			endDate = System.currentTimeMillis();
			totalTimeLeft -= endDate - startDate;
			totalTimeSFA = timeOut-totalTimeLeft;
			exploredStatesSFA = Timers.exploredStates;
		} catch (Exception e) {
			totalTimeSFA = timeOut;
			exploredStatesSFA = -1;
		}

	}

	private static Triple<SAFA<CharPred, Character>, PositiveBooleanExpression, PositiveBooleanExpression> IntersectedSAFA(
			ArrayList<SAFA<CharPred, Character>> safaList) {
		Triple<SAFA<CharPred, Character>, PositiveBooleanExpression, PositiveBooleanExpression> result = null;
		boolean returnNull = true;
		if (safaList.size() < 2) {
			return null;
		}
		if (safaList.size() == 2) {
			try {
				result = safaList.get(0).intersectionWithGetConjucts(safaList.get(1), solver);
				returnNull = false;
			} catch (Exception e) {
				returnNull = true;
			}
			return returnNull ? null : result;
		} else { // size >2
			try {
				result = safaList.get(0).intersectionWithGetConjucts(safaList.get(1), solver);
				for (int i = 2; i < safaList.size(); i++) {
					result = result.getLeft().intersectionWithGetConjucts(safaList.get(i), solver);
				}
				returnNull = false;
			} catch (Exception e) {
				returnNull = true;
			}
			return returnNull ? null : result;
		}
	}

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

}
