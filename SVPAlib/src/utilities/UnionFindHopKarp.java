package utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* 
 * Disjoint-set data structure - Library (Java)
 * 
 * Copyright (c) 2015 Project Nayuki
 * https://www.nayuki.io/page/disjoint-set-data-structure
 * 
 * (MIT License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */


/* 
 * Represents a set of disjoint sets. Also known as the union-find data structure.
 * Main operations are querying if two elements are in the same set, and merging two sets together.
 * Useful for testing graph connectivity, and is used in Kruskal's algorithm.
 */
public final class UnionFindHopKarp<S> {
	
	/*---- Fields ----*/
	
	// Global properties
	private int numSets;
	
	// Per-node properties. This representation is more space-efficient than creating one node object per element.
//	private int[] parents;  // The index of the parent element. An element is a representative iff its parent is itself.
//	private byte[] ranks;   // Always in the range [0, floor(log2(numElems))]. Thus has a maximum value of 30.
//	private int[] sizes;    // Positive number if the element is a representative, otherwise zero.
	
	private Map<Integer,Integer> parents;  // The index of the parent element. An element is a representative iff its parent is itself.
	private Map<Integer,Integer> ranks;   // Always in the range [0, floor(log2(numElems))]. Thus has a maximum value of 30.
	private Map<Integer,Integer> sizes;    // Positive number if the element is a representative, otherwise zero.
	private Map<Integer,Boolean> isFinal;
	private Map<Integer,List<S>> witness; 
	
	
	public boolean contains(int elem){
		return parents.containsKey(elem);
	}
	
	/*---- Constructors ----*/
	
	// Constructs a new set containing the given number of singleton sets.
	// For example, new DisjointSet(3) --> {{0}, {1}, {2}}.
//	public DisjointSet(int numElems) {
//		if (numElems <= 0)
//			throw new IllegalArgumentException("Number of elements must be positive");
//		parents = new HashMap<>(); 				
//		ranks = new HashMap<>();
//		sizes = new HashMap<>();
//		isFinal=new HashMap<>();
//		for (int i = 0; i < numElems; i++) {
//			parents.put(i,i);
//			ranks.put(i,0);
//			sizes.put(i,1);
//			isFin
//		}
//		numSets = numElems;
//	}
	
	// Constructs a new set containing the given number of singleton sets.
	// For example, new DisjointSet(3) --> {{0}, {1}, {2}}.
	public UnionFindHopKarp() {
		parents = new HashMap<>(); 				
		ranks = new HashMap<>();
		sizes = new HashMap<>();
		isFinal = new HashMap<>();
		witness = new HashMap<>();
		numSets = 0;
	}
	
	// Constructs a new set containing the given number of singleton sets.
	// For example, new DisjointSet(3) --> {{0}, {1}, {2}}.
	public void add(int elem, boolean isFin, List<S> wit) {
		if (parents.containsKey(elem))
			throw new IllegalArgumentException("Element should not be in the set already");
		parents.put(elem,elem);
		ranks.put(elem,0);
		sizes.put(elem,1);
		isFinal.put(elem, isFin);
		witness.put(elem, wit);
		numSets++;
	}
	
	
	
	/*---- Methods ----*/
	
	// Returns the number of elements among the set of disjoint sets; this was the number passed
	// into the constructor and is constant for the lifetime of the object. All the other methods
	// require the argument elemIndex to satisfy 0 <= elemIndex < getNumberOfElements().
	public int getNumberOfElements() {
		return parents.size();
	}
	
	
	// Returns the number of disjoint sets overall. This number decreases monotonically as time progresses;
	// each call to mergeSets() either decrements the number by one or leaves it unchanged.
	public int getNumberOfSets() {
		return numSets;
	}
	
	
	// (Private) Returns the representative element for the set containing the given element. This method is also
	// known as "find" in the literature. Also performs path compression, which alters the internal state to
	// improve the speed of future queries, but has no externally visible effect on the values returned.
	public int getRepr(int elemIndex) {
		/*if (elemIndex < 0 || elemIndex >= parents.size())
			throw new IndexOutOfBoundsException();*/
		// Follow parent pointers until we reach a representative
		int parent = parents.get(elemIndex);
		if (parent == elemIndex)
			return elemIndex;
		while (true) {
			int grandparent = parents.get(parent);
			if (grandparent == parent)
				return parent;
			parents.put(elemIndex, grandparent); // Partial path compression
			elemIndex = parent;
			parent = grandparent;
		}
	}
	
	
	// Returns the size of the set that the given element is a member of. 1 <= result <= getNumberOfElements().
	public int getSizeOfSet(int elemIndex) {
		return sizes.get(getRepr(elemIndex));
	}
	
	// Returns the size of the set that the given element is a member of. 1 <= result <= getNumberOfElements().
	public List<S> getWitness(int elemIndex) {
		return witness.get(elemIndex);
	}
	
	
	// Tests whether the given two elements are members of the same set. Note that the arguments are orderless.
	public boolean areInSameSet(int elemIndex0, int elemIndex1) {
		return getRepr(elemIndex0) == getRepr(elemIndex1);
	}
	
	
	// Merges together the sets that the given two elements belong to. This method is also known as "union" in the literature.
	// Returns false if the two elements have different final states conditions
	public boolean mergeSets(int elemIndex0, int elemIndex1) {
		if(isFinal.get(elemIndex0) != isFinal.get(elemIndex1))
			return false;
		
		// Get representatives
		int repr0 = getRepr(elemIndex0);
		int repr1 = getRepr(elemIndex1);
		if (repr0 == repr1)
			return true;
		
		// Compare ranks
		int cmp = ranks.get(repr0) - ranks.get(repr1);
		if (cmp == 0){
			// Increment repr0's rank if both nodes have same rank
			int r = ranks.get(repr0);
			ranks.put(repr0, r+1);
		}
		else if (cmp < 0) {  // Swap to ensure that repr0's rank >= repr1's rank
			int temp = repr0;
			repr0 = repr1;
			repr1 = temp;
		}
		
		// Graft repr1's subtree onto node repr0
		parents.put(repr1, repr0);
		int sizer1 = sizes.get(repr1);
		sizes.put(repr0,sizer1);
		sizes.put(repr1, 0);
		numSets--;
		return true;
	}
	
	
	// For unit tests. This detects many but not all invalid data structures, throwing an AssertionError
	// if a structural invariant is known to be violated. This always returns silently on a valid object.
	void checkStructure() {
		int numRepr = 0;
		for (int i = 0; i < parents.size(); i++) {
			int parent = parents.get(i);
			int rank = ranks.get(i);
			int size = sizes.get(i);
			boolean isRepr = parent == i;
			if (isRepr)
				numRepr++;
			
			boolean ok = true;
			ok &= 0 <= parent && parent < parents.size();
			ok &= 0 <= rank && (isRepr || rank < ranks.get(parent));
			ok &= !isRepr && size == 0 || isRepr && size >= (1 << rank);
			if (!ok)
				throw new AssertionError();
		}
		if (!(1 <= numSets && numSets == numRepr && numSets <= parents.size()))
			throw new AssertionError();
	}

	@Override
	public String toString() {
		HashMap<Integer, HashSet<Integer>> sets = new HashMap<>();
		for(int i:parents.keySet())
			sets.put(i, new HashSet<>());
		for(int i:parents.keySet()){
			int p = parents.get(i);
			HashSet<Integer> set = sets.get(p);
			set.add(i);
			sets.put(p, set);
		}
		String s="";
		for(HashSet<Integer> set: sets.values()){
			if(!set.isEmpty())
				s+=set+" ";
		}
		
		return s;
	}
	
	
	
}
