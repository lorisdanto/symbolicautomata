package benchmark.regexconverter;

import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;

import automata.safa.SAFA;
import automata.sfa.SFA;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class AutomataWrapper {
	UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	private int[] indexArray;
	private SAFA<CharPred, Character>[] safaArray;
	private SFA<CharPred, Character>[] sfaArray;

	public AutomataWrapper(int[] index, SAFA<CharPred, Character>[] safa) {
		if (index.length != safa.length) {
			System.err.println("Wrong construction of Wrapper!");
			System.exit(-1);
		}
		this.indexArray = index;
		this.safaArray = safa;

	}

	public AutomataWrapper(int[] index, SFA<CharPred, Character>[] sfa) {
		if (index.length != sfa.length) {
			System.err.println("Wrong construction of Wrapper!");
			System.exit(-1);
		}
		this.indexArray = index;
		this.sfaArray = sfa;
	}

	public void buildSAFA() throws TimeoutException {
		for (int i = 0; i < sfaArray.length; i++) {
			safaArray[i] = sfaArray[i].getSAFA(solver);
		}
	}

	public ArrayList<SFA<CharPred, Character>> getSFAlist() {
		ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
		for (int i = 0; i < sfaArray.length; i++) {
			sfaList.add(this.sfaArray[i]);
		}

		return sfaList;
	}

	public ArrayList<SAFA<CharPred, Character>> getSAFAlist() {
		ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
		for (int i = 0; i < sfaArray.length; i++) {
			safaList.add(this.safaArray[i]);
		}
		return safaList;
	}

	public ArrayList<Integer> getIndex() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < indexArray.length; i++) {
			list.add(this.indexArray[i]);
		}
		return list;
	}

	public int getIndex(int index) {
		return this.indexArray[index];
	}

	public int getSize(){
		return indexArray.length;
	}

}
