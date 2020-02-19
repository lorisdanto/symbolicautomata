/**
 * 
 */
package automata.svpa;

import java.util.Stack;

import org.sat4j.specs.TimeoutException;

import theory.BooleanAlgebra;
import utilities.Pair;

public abstract class SVPAMove<U,S> implements Cloneable{

	public Integer from;

	public Integer to;
	
	Tag type;	
	
	protected SVPAMove(Integer from, Integer to, Tag type) {
		this.from = from;
		this.to=to;
		this.type = type;
	}

	public abstract boolean isSatisfiable(BooleanAlgebra<U,S> boolal) throws TimeoutException;
	
	public abstract boolean isDisjointFrom(SVPAMove<U,S> t, BooleanAlgebra<U,S> ba) throws TimeoutException;		
	
	public abstract Pair<Integer, Stack<Pair<Integer, S>>> getNextState(Pair<Integer, Stack<Pair<Integer, S>>> state,
			TaggedSymbol<S> input, BooleanAlgebra<U, S> ba) throws TimeoutException;	

	
	public abstract Object clone();

	public abstract String toDotString();

	public abstract S getWitness(BooleanAlgebra<U, S> ba) throws TimeoutException;

	public abstract boolean isEpsilonTransition();
	
}
