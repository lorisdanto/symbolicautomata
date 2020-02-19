package utilities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Block {

	public HashSet<Integer> set;

	public Block(Collection<Integer> set) {
		super();
		this.set = new HashSet<Integer>(set);
	};
	
	public Block() {
		super();
		this.set = new HashSet<Integer>();
	};
	
	public int size(){
		return set.size();
	}
	
	public Block intersectWith(Block b){
		HashSet<Integer> intersection = new HashSet<Integer>(set);
		intersection.retainAll(b.set);
		return new Block(intersection);
	}
	
	public boolean remove(int s){
		return set.remove(s);
	}

	@Override
	public boolean equals(Object arg0) {
		Block b = (Block) arg0;
		return set.equals(b.set);
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}

	@Override
	public String toString() {
		return set.toString();
	}
	
	private Iterator<Integer> iter;
	public int getFirst(){
		iter = set.iterator();
		return iter.next();
	}
	
	public boolean hasNext(){
		return iter.hasNext();
	}
	
	public int getNext(){
		return iter.next();
	}
	
	
	public boolean add(int p){
		return set.add(p);
	}
	
	public void clear(){
		set = new HashSet<Integer>();
	}
	
}
