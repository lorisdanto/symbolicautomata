package benchmark.regexconverter;

import java.util.ArrayList;


public class Combination {
	public Combination(int firstIndex, int secondIndex){
		this.firstIndex = firstIndex;
		this.myArrayList.add(secondIndex);
	}
	
	public Combination(int firstIndex){
		this.firstIndex = firstIndex;
	}
	
	public void setCommonIndex(int index){
		this.firstIndex = index;
	}
	public int getCommonIndex(){
		return firstIndex;
	}
	
	public void addToIndexArray(int index){
		myArrayList.add(index);
	}
	
	public ArrayList<Integer> getIndexArray(){
		return myArrayList;
	}
	
	
	
	private int firstIndex;
	private ArrayList<Integer> myArrayList = new ArrayList<Integer>();
}
