package benchmark.regexconverter;

import java.util.ArrayList;


public class MultiCombination {
	public MultiCombination(int... index){
		for(int i: index){
			myIndexList.add(i);
		}
	}
	
	public MultiCombination(){
		
	}
	
	public void setCommonIndex(int... index){
		this.myIndexList.clear();
		for(int i: index){
			myIndexList.add(i);
		}
	}
	public ArrayList<Integer> getCommonIndex(){
		return this.myIndexList;
	}
	
	public void addToIndexArray(int index){
		myArrayList.add(index);
	}
	
	public ArrayList<Integer> getIndexArray(){
		return myArrayList;
	}
	
	
	
	
	private ArrayList<Integer> myIndexList = new ArrayList<Integer>();
	private ArrayList<Integer> myArrayList = new ArrayList<Integer>();
}
