package benchmark.regexconverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;
import java.math.*;

import benchmark.SFAprovider;
import benchmark.regexconverter.Combination;
import benchmark.regexconverter.MultiCombination;
import automata.safa.SAFA;
import automata.sfa.SFA;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;
import utilities.Timers;

public class RunEquivalenceExp {
	static FileReader inFile;
	static FileReader pairFile;
	static FileReader tripleFile;
	static FileReader quadraFile;
	static FileReader pentaFile;

	private static PrintWriter equivalence2to3;
	private static PrintWriter equivalence3to4;
	private static PrintWriter equivalence4to5;

	private static UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	private static ArrayList<Combination> pairCombination = new ArrayList<Combination>();
	private static ArrayList<MultiCombination> tripleCombination = new ArrayList<MultiCombination>();
	private static ArrayList<MultiCombination> quadraCombination = new ArrayList<MultiCombination>();

	private static ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
	private static ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
	// to store input file of patterns
	private static ArrayList<String> list = new ArrayList<String>();

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
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	private static void runEquivalenceOf2to3(long timeOut) throws TimeoutException {
		ArrayList<String> pairList = new ArrayList<String>();
		ArrayList<String> tripleList = new ArrayList<String>();
		
		try {
			equivalence2to3 = new PrintWriter("src/benchmark/regexconverter/EquivalenceOf2to3.txt");
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
		
		try {
			tripleFile = new FileReader("src/benchmark/regexconverter/TripleResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		
		try (BufferedReader br = new BufferedReader(pairFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				pairList.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedReader br = new BufferedReader(tripleFile)) {
			String line;
			while ((line = br.readLine()) != null) {
				tripleList.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		equivalence2to3
				.print("name   |SAFA1|   |SAFA2|   |SFA1|   |SFA2|   FullTime   SolverTime   subsTime   SFAtime"+"\n");
		generate2to3(timeOut);
		equivalence2to3.close();
		
		
	}

	
	private static void generate2to3(long timeOut) {
		// TODO Auto-generated method stub
		
	}


	private static void runEquivalenceOf3to4(long timeOut) throws TimeoutException {
		try {
			equivalence3to4 = new PrintWriter("src/benchmark/regexconverter/EquivalenceOf3to4.txt");
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
		
		try {
			quadraFile = new FileReader("src/benchmark/regexconverter/QuadraResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		
		
		
		equivalence3to4
				.print("name   |SAFA1|   |SAFA2|   |SFA1|   |SFA2|   FullTime   SolverTime   subsTime   SFAtime"+"\n");
		generate3to4(timeOut);
		equivalence3to4.close();
		
		
	}

	
	private static void generate3to4(long timeOut) {
		// TODO Auto-generated method stub
		
	}


	private static void runEquivalenceOf4to5(long timeOut) throws TimeoutException {
		try {
			equivalence4to5 = new PrintWriter("src/benchmark/regexconverter/EquivalenceOf4to5.txt");
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

		try {
			pentaFile = new FileReader("src/benchmark/regexconverter/PentaResult.txt");
		} catch (FileNotFoundException ex) {
			System.err.println("File not found.");
			System.exit(-1);
		}
		
		
		
		equivalence4to5
				.print("name   |SAFA1|   |SAFA2|   |SFA1|   |SFA2|   FullTime   SolverTime   subsTime   SFAtime"+"\n");
		generate4to5(timeOut);
		equivalence4to5.close();
		
		
		
	}


	private static void generate4to5(long timeOut) {
		// TODO Auto-generated method stub
		
	}
}
