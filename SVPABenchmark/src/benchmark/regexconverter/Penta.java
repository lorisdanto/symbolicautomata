package benchmark.regexconverter;

import java.util.ArrayList;

import org.sat4j.specs.TimeoutException;

import automata.safa.SAFA;
import automata.sfa.SFA;
import theory.characters.CharPred;
import theory.intervals.UnaryCharIntervalSolver;

public class Penta {
	UnaryCharIntervalSolver solver = new UnaryCharIntervalSolver();
	private int firstIndex;
	private int secondIndex;
	private int thirdIndex;
	private int fourthIndex;
	private int fifthIndex;
	private SAFA<CharPred, Character> mySAFA1;
	private SAFA<CharPred, Character> mySAFA2;
	private SAFA<CharPred, Character> mySAFA3;
	private SAFA<CharPred, Character> mySAFA4;
	private SAFA<CharPred, Character> mySAFA5;
	
	private SFA<CharPred, Character> mySFA1;
	private SFA<CharPred, Character> mySFA2;
	private SFA<CharPred, Character> mySFA3;
	private SFA<CharPred, Character> mySFA4;
	private SFA<CharPred, Character> mySFA5;
	
	public Penta(int index1, int index2, int index3, int index4, int index5, SAFA<CharPred, Character> safa1, SAFA<CharPred, Character> safa2, SAFA<CharPred, Character> safa3, SAFA<CharPred, Character> safa4, SAFA<CharPred, Character> safa5){
		this.firstIndex = index1;
		this.secondIndex = index2;
		this.thirdIndex = index3;
		this.fourthIndex = index4;
		this.fifthIndex = index5;
		this.mySAFA1 = safa1;
		this.mySAFA2 = safa2;
		this.mySAFA3 = safa3;
		this.mySAFA4 = safa4;
		this.mySAFA5 = safa5;

	}
	
	public Penta(int index1, int index2, int index3, int index4, int index5, SFA<CharPred, Character> sfa1, SFA<CharPred, Character> sfa2, SFA<CharPred, Character> sfa3, SFA<CharPred, Character> sfa4, SFA<CharPred, Character> sfa5){
		this.firstIndex = index1;
		this.secondIndex = index2;
		this.thirdIndex = index3;
		this.fourthIndex = index4;
		this.fifthIndex = index5;
		this.mySFA1 = sfa1;
		this.mySFA2 = sfa2;
		this.mySFA3 = sfa3;
		this.mySFA4 = sfa4;
		this.mySFA5 = sfa5;
	}
	
	public void buildSAFA() throws TimeoutException{
		this.mySAFA1 = this.mySFA1.getSAFA(solver);
		this.mySAFA2 = this.mySFA2.getSAFA(solver); 
		this.mySAFA3 = this.mySFA3.getSAFA(solver); 
		this.mySAFA4 = this.mySFA4.getSAFA(solver); 
		this.mySAFA5 = this.mySFA4.getSAFA(solver); 
	}
	
	public ArrayList<SFA<CharPred, Character>> getSFAlist(){
		ArrayList<SFA<CharPred, Character>> sfaList = new ArrayList<SFA<CharPred, Character>>();
		sfaList.add(this.mySFA1);
		sfaList.add(this.mySFA2);
		sfaList.add(this.mySFA3);
		sfaList.add(this.mySFA4);
		sfaList.add(this.mySFA5);
		return sfaList;
	}
	
	public ArrayList<SAFA<CharPred, Character>> getSAFAlist(){
		ArrayList<SAFA<CharPred, Character>> safaList = new ArrayList<SAFA<CharPred, Character>>();
		safaList.add(this.mySAFA1);
		safaList.add(this.mySAFA2);
		safaList.add(this.mySAFA3);
		safaList.add(this.mySAFA4);
		safaList.add(this.mySAFA5);
		return safaList;
	}
	
	public ArrayList<Integer> getIndex(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(this.firstIndex);
		list.add(this.secondIndex);
		list.add(this.thirdIndex);
		list.add(this.fourthIndex);
		list.add(this.fifthIndex);
		return list;
	}
	
	public int getFirstIndex(){
		return this.firstIndex;
	}
	
	public int getSecondIndex(){
		return this.secondIndex;
	}
	
	public int getThirdIndex(){
		return this.thirdIndex;
	}
	
	public int getFourthIndex(){
		return this.fourthIndex;
	}
	public int getFifthIndex(){
		return this.fifthIndex;
	}
	
	
	
}
