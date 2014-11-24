/**
 * 
 */
package automata.svpa;

import java.util.Stack;

import automata.Move;

import theory.BooleanAlgebra;
import utilities.Pair;

public abstract class SVPAMove<U,S> extends Move<U, S> implements Cloneable{

	Tag type;	
	
	protected SVPAMove(Integer from, Integer to, Tag type) {
		super(from, to);
		this.type = type;
	}

	public abstract boolean isSatisfiable(BooleanAlgebra<U,S> boolal);
	
	public abstract boolean isDisjointFrom(SVPAMove<U,S> t, BooleanAlgebra<U,S> ba);	
	
	
	
	
	public abstract Pair<Integer, Stack<Pair<Integer, S>>> getNextState(Pair<Integer, Stack<Pair<Integer, S>>> state,
			TaggedSymbol<S> input, BooleanAlgebra<U, S> ba);	

	
	public abstract Object clone();
	
}
